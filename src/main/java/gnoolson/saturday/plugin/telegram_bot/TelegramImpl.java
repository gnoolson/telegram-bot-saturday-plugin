//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday_plugin_api.Output;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class TelegramImpl implements Telegram {

    private final Map<String, TelegramBot> map = new HashMap<>();
    private final ScriptLauncherGateway scriptLauncherGateway;

    /*
     *
     *
     * */
    public TelegramImpl(ScriptLauncherGateway scriptLauncherGateway) {
        this.scriptLauncherGateway = scriptLauncherGateway;
    }

    @Override
    public synchronized TelegramBot getOrCreateBot(String botId, String path) {
        return this.map.computeIfAbsent(botId, (_id) -> this.createTelegramBot(_id, path));
    }

    @Override
    public synchronized void shutdown() {
        for (String id : this.map.keySet()) {
            this.stop(id);
        }
    }

    @Override
    public synchronized void stop(String botId) {
        TelegramBot telegramBot = this.map.remove(botId);
        if (telegramBot != null) {
            telegramBot.stop();
        }
    }

    @Override
    public synchronized List<BotInfo> info() {
        List<BotInfo> result = new ArrayList<>(this.map.size());

        for (Map.Entry<String, TelegramBot> entry : this.map.entrySet()) {
            result.add(new BotInfo(
                    entry.getKey(),
                    entry.getValue().getUsername(),
                    entry.getValue().getCreatedTs()));
        }

        return result;
    }

    /*
     *
     *
     * */
    private TelegramBot createTelegramBot(String id, String path) {
        try {
            Properties prop = new Properties();
            File file = ResourceUtils.getFile(path);

            try (FileInputStream fis = new FileInputStream(file)) {
                prop.load(fis);
            }

            String token = prop.getProperty("tg.token");
            String username = prop.getProperty("tg.username");
            String scriptId = prop.getProperty("tg.script.script_id");
            int queue = Integer.valueOf(prop.getProperty("tg.queue"));
            boolean executeScript = Boolean.parseBoolean(prop.getProperty("tg.script.execute"));

            return new TelegramBotImpl(id,
                    username,
                    token,
                    scriptId,
                    queue,
                    executeScript,
                    this.scriptLauncherGateway
            );
        } catch (IOException e) {
            throw new RuntimeException(e); // +
        }
    }

    @Override
    public void writeResultToOutput(Output output) {

    }

}
