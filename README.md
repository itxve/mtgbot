# mtgbot（有空再优化matchText吧 目前基本都能用）

基于 Spring Boot 和 long polling 的 Telegram Bot 核心库。通过 `@BotController` 注册控制器，并用 `@ChatMapping`
将消息或其他更新映射到处理方法。

## 通过 JitPack 引入

先在 GitHub 仓库创建并推送一个 release tag（例如 `v0.0.1`），然后在使用方项目中添加 JitPack 仓库和依赖。版本号使用实际发布的
tag。

### Maven

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>

<dependencies>
<dependency>
    <groupId>com.github.itxve</groupId>
    <artifactId>mtgbot</artifactId>
    <version>v0.0.1</version>
</dependency>
</dependencies>
```

### Gradle (Groovy)

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.itxve:mtgbot:0.0.3'
}
```

### Gradle (Kotlin DSL)

```kotlin
repositories {
    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation("com.github.itxve:mtgbot:0.0.3")
}
```

示例中的 `0.0.3` 是版本格式示意；请确保仓库中存在对应的 Git tag / GitHub release。首次请求该版本时，JitPack 会尝试构建仓库。

## 基本用法

控制器需要是 Spring Bean。`@BotController` 已包含 `@Component`，处理方法可接收框架支持的参数类型。

```java
import com.pengrad.telegrambot.model.Update;
import com.skk.mtgbot.annotation.BotController;
import com.skk.mtgbot.annotation.ChatMapping;
import com.skk.mtgbot.core.ChatType;
import com.skk.mtgbot.core.MsgUpdateType;

@BotController
public class ExampleController {

    // 私聊中发送 /start 时触发
    @ChatMapping(value = "^/start$", chatTypes = ChatType.Private)
    public void start(String text, Update update) {
        System.out.println("收到 " + text + "，chatId=" + update.message().chat().id());
    }

    // /echo 后面的文字也属于匹配文本；String 参数接收完整消息
    @ChatMapping(value = "^/echo\\s+.+$")
    public void echo(String text) {
        System.out.println("收到回显请求：" + text);
    }

    // 群聊中的数字计算命令，正则按整条文本匹配
    @ChatMapping(value = "^/roll\\s+\\d+$", chatTypes = {ChatType.group, ChatType.supergroup})
    public void roll(com.pengrad.telegrambot.model.Message message) {
        System.out.println("群 " + message.chat().id() + " 收到：" + message.text());
    }

    // 按 callback query 的 data 匹配
    @ChatMapping(value = "^confirm:[0-9]+$", msgType = MsgUpdateType.CALLBACK_QUERY)
    public void confirm(com.pengrad.telegrambot.model.CallbackQuery callback) {
        System.out.println("确认数据：" + callback.data());
    }

    // 按 inline query 的 query 匹配
    @ChatMapping(value = "^weather\\s+.+$", msgType = MsgUpdateType.INLINE_QUERY)
    public void weather(String query) {
        System.out.println("Inline query：" + query);
    }
}
```

## 媒体组和 caption

媒体组需要使用 `MEDIA_GROUP`。caption 用于正则匹配；`BatchedMediaGroup` 可取得聚合后的全部消息。

```java
import com.skk.mtgbot.core.MsgUpdateType;
import com.skk.mtgbot.core.media.BatchedMediaGroup;

@ChatMapping(value = "^旅行照片$", msgType = MsgUpdateType.MEDIA_GROUP)
public void photos(BatchedMediaGroup group) {
    System.out.println("媒体组包含 " + group.getAllMessages().size() + " 条消息");
}
```

## 注解参数

| 参数        | 默认值         | 说明                                                                      |
|-------------|----------------|---------------------------------------------------------------------------|
| `value`     | `.*`           | Java 正则表达式，使用整条文本匹配（`matches()`）                          |
| `msgType`   | `MESSAGE`      | 匹配消息文本、媒体组 caption、callback data、inline query 或 Web App data |
| `chatTypes` | `ChatType.all` | 限定私聊、群组、超级群或频道                                              |
| `order`     | `0`            | 匹配处理顺序，数值较小的先处理                                            |
| `condition` | 默认条件       | 可提供自定义 `BotHandlerCondition` 实现                                   |

方法参数解析支持 `String`、Telegram SDK 的 `Update`、`Message`、`CallbackQuery`、`InlineQuery`、Web App data 对象，以及媒体组的
`BatchedMediaGroup`。不支持的参数类型会被传入 `null`。

处理方法目前由 dispatcher 调用；如需给用户回复，请在方法中注入并使用 Telegram Bot API。应用还需启用项目的自动配置并提供 Bot
token 配置。
