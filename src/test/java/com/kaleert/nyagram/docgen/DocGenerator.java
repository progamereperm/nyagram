package com.kaleert.nyagram.docgen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.nodeTypes.NodeWithJavadoc;
import com.github.javaparser.ast.nodeTypes.NodeWithTypeParameters;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DocGenerator {

    private static final String SOURCE_ROOT = "src/main/java";
    private static final String OUTPUT_FILE = "../nyagram-docs/public/data/api.yaml";
    private static final String PROJECT_VERSION = "1.0.0";
    
    private static final JavaParser parser;
    private static final ObjectMapper mapper;
    
    // Статистика
    private static final List<Gap> gapList = new ArrayList<>();
    private static final AtomicInteger missingDocsCount = new AtomicInteger(0);
    private static final AtomicInteger totalDocsCount = new AtomicInteger(0); // <-- НОВОЕ ПОЛЕ

    static {
        ParserConfiguration config = new ParserConfiguration();
        try { config.setLanguageLevel(ParserConfiguration.LanguageLevel.valueOf("JAVA_21")); } 
        catch (IllegalArgumentException e) { config.setLanguageLevel(ParserConfiguration.LanguageLevel.JAVA_17); }
        parser = new JavaParser(config);

        mapper = new ObjectMapper(new YAMLFactory().disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER));
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static void main(String[] args) throws IOException {
        System.out.println("🚀 Запуск генератора документации...");
        
        List<PackageDoc> packages = new ArrayList<>();
        Map<String, List<ClassDoc>> packageMap = new HashMap<>();

        try (Stream<Path> paths = Files.walk(Paths.get(SOURCE_ROOT))) {
            List<Path> javaFiles = paths.filter(Files::isRegularFile)
                 .filter(p -> p.toString().endsWith(".java"))
                 .collect(Collectors.toList());
            
            System.out.println("🔎 Сканирование " + javaFiles.size() + " файлов...");
            
            AtomicInteger processed = new AtomicInteger(0);
            for (Path path : javaFiles) {
                parseFile(path, packageMap);
                printProgress(processed.incrementAndGet(), javaFiles.size());
            }
            System.out.println();
        }

        for (Map.Entry<String, List<ClassDoc>> entry : packageMap.entrySet()) {
            String shortPkgName = entry.getKey().replace("com.kaleert.nyagram.", "");
            packages.add(new PackageDoc(shortPkgName, entry.getValue()));
        }
        packages.sort(Comparator.comparing(p -> p.name));

        ApiRoot root = new ApiRoot(PROJECT_VERSION, packages);
        File outFile = new File(OUTPUT_FILE);
        if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();
        mapper.writeValue(outFile, root);
        
        System.out.println("\n✅ Файл сохранен: " + outFile.getAbsolutePath());
        
        // --- ВЫВОД СТАТИСТИКИ ---
        printFinalStats();
        printChecklist();
    }

    private static void printProgress(int current, int total) {
        int percent = (current * 100) / total;
        System.out.print("\r[" + "=".repeat(percent / 2) + " ".repeat(50 - (percent / 2)) + "] " + percent + "%");
    }

    private static void printFinalStats() {
        int total = totalDocsCount.get();
        int missing = missingDocsCount.get();
        int done = total - missing;
        double percent = total > 0 ? ((double) done / total) * 100 : 100;

        System.out.println("\n📊 СТАТИСТИКА ДОКУМЕНТАЦИИ:");
        System.out.printf("   Всего элементов:  %d\n", total);
        System.out.printf("   Готово:           %d\n", done);
        System.out.printf("   Нужно сделать:    %d\n", missing);
        
        // Рисуем прогресс бар
        int barLength = 30;
        int filledLength = (int) (percent / 100 * barLength);
        String bar = "█".repeat(filledLength) + "░".repeat(barLength - filledLength);
        
        System.out.printf("   Прогресс:         [%s] %.2f%%\n", bar, percent);
    }

    private static void printChecklist() {
        if (gapList.isEmpty()) {
            System.out.println("\n🎉 Поздравляю! Всё задокументировано на 100%.");
            return;
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📋 ЧЕК-ЛИСТ (ТОП-15 ВАЖНЫХ ПРОПУСКОВ)");
        System.out.println("=".repeat(60));

        gapList.sort(Comparator.comparingInt(Gap::weight).reversed().thenComparing(Gap::location));

        int limit = 15;
        for (int i = 0; i < Math.min(gapList.size(), limit); i++) {
            Gap gap = gapList.get(i);
            System.out.printf("[%d] %-10s %s\n", (i + 1), gap.type, gap.location);
            System.out.printf("    👉 %s\n", gap.todo);
        }
        System.out.println("-".repeat(60));
    }

    private static void parseFile(Path path, Map<String, List<ClassDoc>> packageMap) {
        try {
            ParseResult<CompilationUnit> result = parser.parse(path);
            if (!result.isSuccessful()) return;
            CompilationUnit cu = result.getResult().orElse(null);
            if (cu == null) return;

            String pkgName = cu.getPackageDeclaration()
                    .map(pd -> pd.getNameAsString())
                    .orElse("default")
                    .replace("com.kaleert.nyagram.", "");

            packageMap.putIfAbsent(pkgName, new ArrayList<>());
            List<ClassDoc> pkgClasses = packageMap.get(pkgName);

            cu.findAll(TypeDeclaration.class).forEach(rawType -> {
                TypeDeclaration<?> type = (TypeDeclaration<?>) rawType;

                String tempTypeName = type.getNameAsString();
                com.github.javaparser.ast.Node parent = type.getParentNode().orElse(null);
                while (parent instanceof TypeDeclaration) {
                    tempTypeName = ((TypeDeclaration<?>) parent).getNameAsString() + "." + tempTypeName;
                    parent = parent.getParentNode().orElse(null);
                }
                
                final String typeName = tempTypeName; 
                final String finalPkgName = pkgName;

                pkgClasses.removeIf(c -> c.name.equals(typeName));

                String classDescription = extractDescription(type); // Переименовал переменную для ясности!
                
                if (isDescriptionEmpty(classDescription)) {
                    addGap(finalPkgName, typeName, "CLASS", "Добавь JavaDoc описание классу", calculateWeight(finalPkgName, "CLASS"));
                }

                String docType = determineType(type);
                int classLine = getLine(type);
                boolean isDeprecated = type.isAnnotationPresent("Deprecated");
                
                String rawSince = extractTag(type, JavadocBlockTag.Type.SINCE);
                final String effectiveClassSince = (rawSince != null && !rawSince.isEmpty()) ? rawSince : PROJECT_VERSION;
                String classExample = cleanExampleCode(extractTag(type, "example"));

                List<String> extendsList = type.isClassOrInterfaceDeclaration() ? 
                    type.asClassOrInterfaceDeclaration().getExtendedTypes().stream().map(Object::toString).toList() : null;
                List<String> implementsList = type.isClassOrInterfaceDeclaration() ? 
                    type.asClassOrInterfaceDeclaration().getImplementedTypes().stream().map(Object::toString).toList() : null;

                List<GenericTypeDoc> typeParams = new ArrayList<>();
                if (type instanceof NodeWithTypeParameters<?>) {
                    var nodeWithTP = (NodeWithTypeParameters<?>) type;
                    Map<String, String> paramMap = extractParamMap(type);
                    nodeWithTP.getTypeParameters().forEach(tp -> {
                        String name = tp.getNameAsString();
                        String desc = paramMap.getOrDefault("<" + name + ">", "No description provided.");
                        typeParams.add(new GenericTypeDoc(name, desc));
                    });
                }

                List<EnumConstantDoc> enumConstants = new ArrayList<>();
                if (type.isEnumDeclaration()) {
                    type.asEnumDeclaration().getEntries().forEach(entry -> {
                        String name = entry.getNameAsString();
                        String desc = extractDescription(entry);
                        enumConstants.add(new EnumConstantDoc(name, desc));
                    });
                }

                List<FieldDoc> fields = new ArrayList<>();
                List<MethodDoc> constructors = new ArrayList<>();
                List<MethodDoc> methods = new ArrayList<>();

                if (type.isRecordDeclaration()) {
                    RecordDeclaration rec = type.asRecordDeclaration();
                    rec.getParameters().forEach(rc -> {
                        String fieldType = rc.getTypeAsString();
                        String name = rc.getNameAsString();
                        String desc = rc.getComment()
                                .filter(c -> c.isJavadocComment())
                                .map(c -> javadocToMarkdown(c.asJavadocComment().parse().getDescription().toText()))
                                .orElse("Record component");
                        
                        totalDocsCount.incrementAndGet(); 
                        
                        if (desc.equals("Record component")) {
                             Map<String, String> classParams = extractParamMap(type);
                             if (classParams.containsKey(name)) {
                                 desc = classParams.get(name);
                             }
                        }
                        fields.add(new FieldDoc(name, fieldType, "private final", desc, getLine(rc)));
                    });
                    
                    if (rec.getConstructors().isEmpty()) {
                        List<ParamDoc> cParams = rec.getParameters().stream()
                            .map(rc -> new ParamDoc(rc.getNameAsString(), rc.getTypeAsString(), ""))
                            .collect(Collectors.toList());
                        String sig = typeName + "(" + cParams.stream().map(p -> p.type + " " + p.name).collect(Collectors.joining(", ")) + ")";
                        constructors.add(new MethodDoc(typeName, "new", "API", "Canonical constructor", sig, cParams, new ReturnDoc(typeName, ""), Collections.emptyList(), classLine, false, effectiveClassSince, null));
                    }
                }

                type.getFields().forEach(field -> {
                    String fieldDesc = extractDescription(field);
                    String fieldType = field.getElementType().asString();
                    String visibility = field.getAccessSpecifier().asString().toLowerCase();
                    int line = getLine(field);
                    field.getVariables().forEach(var -> 
                        fields.add(new FieldDoc(var.getNameAsString(), fieldType, visibility, fieldDesc, line))
                    );
                });

                type.getConstructors().forEach(c -> {
                    String cDesc = extractDescription(c); // Специфичное описание конструктора
                    String sig = c.getDeclarationAsString(true, true, true);
                    String tag = c.isPublic() ? "API" : "Internal";
                    Map<String, String> ctorParamDocs = extractParamMap(c);
                    List<ThrowsDoc> thrownExceptions = extractThrows(c);

                    List<ParamDoc> params = c.getParameters().stream()
                            .map(p -> {
                                String pName = p.getNameAsString();
                                String pDesc = ctorParamDocs.getOrDefault(pName, "No description.");
                                return new ParamDoc(pName, p.getTypeAsString(), pDesc);
                            })
                            .collect(Collectors.toList());
                    
                    constructors.add(new MethodDoc(
                        typeName, "new_" + c.getParameters().size(), tag, cDesc, sig, params, 
                        new ReturnDoc(typeName, ""), thrownExceptions, getLine(c), 
                        c.isAnnotationPresent("Deprecated"), effectiveClassSince, null
                    ));
                });

                type.getMethods().forEach(m -> {
                    if (m.isAnnotationPresent("Override") || m.getNameAsString().equals("toString") || m.getNameAsString().equals("equals") || m.getNameAsString().equals("hashCode")) {
                        return; 
                    }

                    String methodDescription = extractDescription(m); 

                    if (m.isPublic()) {
                        totalDocsCount.incrementAndGet(); 
                        if (isDescriptionEmpty(methodDescription)) {
                            addGap(finalPkgName, typeName + "#" + m.getNameAsString(), "METHOD", "Опиши метод", calculateWeight(finalPkgName, "METHOD"));
                        }
                    }

                    String signature = m.getDeclarationAsString(true, true, true);
                    int line = getLine(m);
                    String tag = (methodDescription.contains("@Internal") || m.isAnnotationPresent("Internal") || !m.isPublic()) ? "Internal" : "API";
                    boolean methodDeprecated = m.isAnnotationPresent("Deprecated");
                    String methodSince = cleanExampleCode(extractTag(m, JavadocBlockTag.Type.SINCE));
                    if (methodSince == null) methodSince = effectiveClassSince;
                    
                    String returnDesc = cleanExampleCode(extractTag(m, JavadocBlockTag.Type.RETURN));
                    String methodExample = cleanExampleCode(extractTag(m, "example"));

                    Map<String, String> paramDocs = extractParamMap(m);
                    List<ParamDoc> params = m.getParameters().stream().map(p -> {
                        String pName = p.getNameAsString();
                        String pDesc = paramDocs.getOrDefault(pName, "No description.");
                        return new ParamDoc(pName, p.getTypeAsString(), pDesc);
                    }).collect(Collectors.toList());

                    List<ThrowsDoc> thrownExceptions = extractThrows(m);

                    methods.add(new MethodDoc(
                            m.getNameAsString(),
                            m.getNameAsString() + "_" + m.getParameters().size(),
                            tag, 
                            methodDescription,
                            signature, params,
                            new ReturnDoc(m.getTypeAsString(), returnDesc),
                            thrownExceptions, line, methodDeprecated, methodSince, methodExample
                    ));
                });

                pkgClasses.add(new ClassDoc(
                    typeName, 
                    typeName, 
                    docType, 
                    classDescription,
                    fields, 
                    constructors, 
                    methods, 
                    enumConstants,
                    classLine, 
                    isDeprecated, 
                    effectiveClassSince, 
                    classExample,
                    extendsList, 
                    implementsList, 
                    typeParams
                ));
            });
        } catch (Exception e) { e.printStackTrace(); }
    }

    // --- GAP HELPERS ---

    private record Gap(String type, String location, String todo, int weight) {}

    private static void addGap(String pkg, String location, String type, String todo, int weight) {
        missingDocsCount.incrementAndGet();
        gapList.add(new Gap(type, pkg + "." + location, todo, weight));
    }

    private static boolean isDescriptionEmpty(String desc) {
        return desc == null || desc.isBlank() || desc.equals("No description provided.");
    }

    private static int calculateWeight(String pkg, String type) {
        int score = 0;
        
        // Приоритетные пакеты
        if (pkg.contains("api.methods") || pkg.contains("api.objects")) score += 100;
        else if (pkg.contains("command")) score += 90;
        else if (pkg.contains("client")) score += 80;
        else score += 10;

        // Приоритетные типы элементов
        switch (type) {
            case "CLASS" -> score += 50;
            case "METHOD" -> score += 30;
            case "FIELD" -> score += 20;
            case "PARAM" -> score += 10;
        }
        return score;
    }

    // --- PARSING HELPERS (CLEANUP) ---

    private static String javadocToMarkdown(String javadoc) {
        if (javadoc == null || javadoc.isBlank()) return "";
        return javadoc
            .replaceAll("\\{@code\\s+(.+?)\\}", "`$1`")
            .replaceAll("\\{@link\\s+(?:[^}]*?\\s+)?([^}]+)\\}", "`$1`")
            .replaceAll("(?i)<p\\s*/?>", "\n\n")
            .replaceAll("(?i)</p>", "")
            .replaceAll("(?i)<ul>", "")
            .replaceAll("(?i)</ul>", "")
            .replaceAll("(?i)<li>", "\n- ")
            .replaceAll("(?i)</li>", "")
            .replaceAll("(?i)<b>|<strong>", "**")
            .replaceAll("(?i)</b>|</strong>", "**")
            .replaceAll("(?i)<i>|<em>", "*")
            .replaceAll("(?i)</i>|</em>", "*")
            .replaceAll("(?i)<h1>", "# ")
            .replaceAll("(?i)<h2>", "## ")
            .replaceAll("(?i)<h3>", "### ")
            .replaceAll("(?i)</h[1-6]>", "\n")
            .replaceAll("(?i)<pre>", "\n```java\n")
            .replaceAll("(?i)</pre>", "\n```\n")
            .replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"")
            .trim();
    }

    private static String cleanExampleCode(String content) {
        if (content == null || content.isBlank()) return null;
        String res = content.trim();
        res = res.replaceAll("(?i)</?pre>", "").trim();
        res = res.replaceAll("(?s)\\{@code\\s+(.+?)\\}", "$1");
        res = res.replace("&lt;", "<").replace("&gt;", ">").replace("&amp;", "&").replace("&quot;", "\"");
        return res.trim();
    }

    private static String extractDescription(NodeWithJavadoc<?> node) {
        return node.getJavadoc()
                .map(d -> javadocToMarkdown(d.getDescription().toText()))
                .filter(s -> !s.isEmpty())
                .orElse("No description provided.");
    }
    
    private static String extractDescription(TypeDeclaration<?> type) {
        return type.getJavadoc()
                .map(d -> javadocToMarkdown(d.getDescription().toText()))
                .filter(s -> !s.isEmpty())
                .orElse("No description provided.");
    }

    private static String extractDescription(EnumConstantDeclaration entry) {
        return entry.getJavadoc()
                .map(d -> javadocToMarkdown(d.getDescription().toText()))
                .filter(s -> !s.isEmpty())
                .orElse("");
    }

    private static String extractTag(NodeWithJavadoc<?> node, JavadocBlockTag.Type tagType) {
        return node.getJavadoc().flatMap(jd -> jd.getBlockTags().stream().filter(t -> t.getType() == tagType)
                .map(t -> t.getContent().toText().trim()).findFirst()).orElse(null);
    }
    
    private static String extractTag(NodeWithJavadoc<?> node, String tagName) {
        return node.getJavadoc().flatMap(jd -> jd.getBlockTags().stream().filter(t -> t.getTagName().equals(tagName))
                .map(t -> t.getContent().toText().trim()).findFirst()).orElse(null);
    }

    private static Map<String, String> extractParamMap(NodeWithJavadoc<?> node) {
        Map<String, String> map = new HashMap<>();
        node.getJavadoc().ifPresent(javadoc -> {
            for (JavadocBlockTag tag : javadoc.getBlockTags()) {
                if (tag.getType() == JavadocBlockTag.Type.PARAM) {
                    String content = tag.getContent().toText().trim();
                    String name = tag.getName().orElse("");
                    if (!name.isEmpty() && !content.isEmpty()) {
                        if (content.startsWith(name)) content = content.substring(name.length()).trim();
                        map.put(name, content.isEmpty() ? "No description." : content);
                    }
                }
            }
        });
        return map;
    }

    private static List<ThrowsDoc> extractThrows(NodeWithJavadoc<?> node) {
        List<ThrowsDoc> list = new ArrayList<>();
        node.getJavadoc().ifPresent(javadoc -> {
            for (JavadocBlockTag tag : javadoc.getBlockTags()) {
                if (tag.getType() == JavadocBlockTag.Type.THROWS || tag.getType() == JavadocBlockTag.Type.EXCEPTION) {
                    String content = tag.getContent().toText().trim();
                    String name = tag.getName().orElse("Exception");
                    if (content.startsWith(name)) content = content.substring(name.length()).trim();
                    list.add(new ThrowsDoc(name, content.isEmpty() ? "No description." : content));
                }
            }
        });
        return list;
    }

    private static int getLine(com.github.javaparser.ast.Node node) {
        return node.getRange().map(r -> r.begin.line).orElse(0);
    }

    private static String determineType(TypeDeclaration<?> type) {
        if (type.isClassOrInterfaceDeclaration()) return type.asClassOrInterfaceDeclaration().isInterface() ? "Interface" : "Class";
        if (type.isEnumDeclaration()) return "Enum";
        if (type.isRecordDeclaration()) return "Record";
        return "Type";
    }

    // DTOs
    public record ApiRoot(String version, List<PackageDoc> packages) {}
    public record PackageDoc(String name, List<ClassDoc> items) {}
    public record ClassDoc(String id, String name, String type, String description, List<FieldDoc> fields, List<MethodDoc> constructors, List<MethodDoc> methods, List<EnumConstantDoc> enumConstants, int line, boolean deprecated, String since, String example, List<String> extendsList, List<String> implementsList, List<GenericTypeDoc> typeParameters) {}
    public record EnumConstantDoc(String name, String description) {}
    public record GenericTypeDoc(String name, String description) {}
    public record FieldDoc(String name, String type, String visibility, String description, int line) {}
    public record MethodDoc(String name, String anchor, String tag, String description, String signature, List<ParamDoc> parameters, ReturnDoc returns, List<ThrowsDoc> exceptions, int line, boolean deprecated, String since, String example) {}
    public record ParamDoc(String name, String type, String desc) {}
    public record ReturnDoc(String type, String desc) {}
    public record ThrowsDoc(String type, String desc) {}
}
