package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday_plugin_api.Functionality;

import java.util.List;

public interface Telegram extends Functionality {

    TelegramBot getOrCreateBot(String botId, String path);

    void shutdown();

    void stop(String botId);

    List<BotInfo> info();

}
