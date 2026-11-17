package gnoolson.saturday.plugin.telegram_bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

class Dev {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    @Test
    public void runBot() throws Exception {
        TelegramPlugin telegramPlugin = new TelegramPlugin();
        telegramPlugin.setScriptLauncherGateway(new ScriptLauncherGateway() {
            @Override
            public void execute(String scriptId, Map<String, Object> args) {
                System.out.println(args);
            }
        });
        telegramPlugin.start();

        Telegram telegram = telegramPlugin.getTelegram();
        TelegramBot dev = telegram.getOrCreateBot("dev", "file:/data/project/java/_CONFIG/tg_bot.properties");

        dev.setCommands(new ArrayList<Command>(){{
            add(new Command("command_1", "description___1"));
            add(new Command("command_2", "description___2"));
        }});

        while (true) {
            Optional<String> updateOpt = dev.getUpdate();
            if (!updateOpt.isPresent()) {
                Thread.sleep(1000);
                continue;
            }

            String json = updateOpt.get();
            System.out.println(json);

            JsonNode jsonNode = JSON_MAPPER.readTree(json);
            String chatId = jsonNode.get("message").get("chat").get("id").asText();
            String responseJson = dev.sendTextMessage(new TextMessage(chatId, "response"));

            System.out.println(responseJson);

//            dev.sendMarkup(new Markup(new OutgoingTextMessage(chatId, "Menu"), new ArrayList<List<Button>>(){{
//                add(new ArrayList<Button>(){{
//                    add(new Button("label", "callbackData____1"));
//                }});
//            }}));
        }

    }

}

