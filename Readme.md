# Nyagram

![Java 21](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2+-green?style=flat-square&logo=springboot)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)
![Version](https://img.shields.io/badge/version-1.1.0-red?style=flat-square)

[🇷🇺 Читать на русском](Readme_RU.md)

**Nyagram** is a modern, reactive, and type-safe framework for building Telegram Bots using **Java 21** and **Spring Boot**.

Forget about manual JSON parsing, infinite `switch-case` statements, and state machine hell. Nyagram handles the routine, allowing you to focus on business logic.

📚 **[Documentation & API Reference](https://nyagram.kaleert.pro)**

---

## 🔥 Why Nyagram?

*   **⚡ Virtual Threads (Project Loom):** High concurrency and performance out of the box.
*   **🧠 Built-in FSM:** Powerful Finite State Machine for creating complex dialogs and funnels.
*   **🎮 Declarative Style:** Clean code with `@CommandHandler`, `@Callback`, and `@StateAction` annotations.
*   **🛡 Security:** Flexible system of `Permissions` and access `Levels`.
*   **💎 Type-Safety:** No more `Map<String, Object>`. Strong types for the entire API (including Telegram Stars and Business).
*   **🔌 Dual Mode:** Switch between **Long Polling** and **Webhook** with a single config line.

---

## 📦 Installation

Requires **Java 21+** and **Spring Boot 3.2+**.

### Gradle
```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation 'com.kaleert:nyagram:1.1.0'
}
```

### Maven
```xml
<dependency>
    <groupId>com.kaleert</groupId>
    <artifactId>nyagram</artifactId>
    <version>1.1.0</version>
</dependency>
```

---

## ⚡ Quick Start

### 1. Configuration (`application.yml`)

```yaml
nyagram:
  bot-token: "YOUR_BOT_TOKEN"
  bot-username: "YourBotName"
  mode: POLLING # or WEBHOOK
  
  worker-thread-count: 10 # Uses Virtual Threads under the hood
```

### 2. Your First Bot

```java
@BotCommand(value = "/start", description = "Start the bot")
public class StartCommand {

    @CommandHandler
    public void handle(CommandContext ctx) {
        ctx.reply("Hello! I'm running on <b>Nyagram</b> 🚀");
    }
}
```

---

## 🛠 Key Features

### 1. Smart Arguments & Flags
The library parses the message text automatically.

```java
// User sends: /ban @spammer 24h -f
@CommandHandler("ban")
public void banUser(
    CommandContext ctx,
    @CommandArgument("target") String username,
    @CommandArgument("duration") Duration duration, // Parses "24h", "30m" automatically
    @Flag("f") boolean force // true if -f flag is present
) {
    if (force) {
        // Ban immediately...
        ctx.reply("User " + username + " banned for " + duration);
    }
}
```

### 2. Callbacks with Path Variables
Forget about manual `split(":")`.

```java
// Button data: "buy:item:52"
@Callback("buy:item:{id}")
public void onBuy(
    CommandContext ctx,
    @CallbackVar("id") Long itemId
) {
    ctx.reply("You selected item #" + itemId);
    // answerCallbackQuery is sent automatically!
}
```

### 3. Finite State Machine (FSM)
Create complex dialog flows effortlessly.

```java
@StateAction("WAITING_FOR_NAME")
public void onNameInput(
    CommandContext ctx, 
    UserSession session
) {
    String name = ctx.getText();
    session.putData("name", name);
    
    // Transition to the next state
    sessionManager.updateState(ctx.getUserId(), "WAITING_FOR_AGE");
    
    ctx.reply("Nice to meet you, " + name + "! How old are you?");
}
```

---

## 🧩 Advanced Features

*   **Middleware Pipeline:** Intercept requests, log actions, or check bans before command execution.
*   **Broadcast API:** Smart message broadcasting respecting Telegram limits.
*   **Telegram Payments 2.0:** Full support for Stars and fiat currencies.
*   **Telegram Business:** Support for business connections and messages.

---

## 🤝 Contributing

We welcome ideas and pull requests! 
If you find a bug, please open an [Issue](https://github.com/kaleert/nyagram/issues).

1.  Fork it
2.  Create your feature branch (`git checkout -b feature/amazing-feature`)
3.  Commit your changes (`git commit -m 'Add amazing feature'`)
4.  Push to the branch (`git push origin feature/amazing-feature`)
5.  Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
    <strong>Made with ❤️ by Kaleert</strong><br>
    2025-2026
</div>