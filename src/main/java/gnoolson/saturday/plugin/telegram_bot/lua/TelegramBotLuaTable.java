package gnoolson.saturday.plugin.telegram_bot.lua;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnoolson.luaj_utils.LuaArgUtils;
import gnoolson.luaj_utils.LuaTypeConverter;
import gnoolson.saturday.plugin.telegram_bot.*;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TelegramBotLuaTable extends LuaTable {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private final TelegramBot telegramBot;

    /*
     *
     *
     * */
    public TelegramBotLuaTable(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;

        this.set("getUpdate", new GetUpdateFunction());
        this.set("setCommands", new SetCommandsFunction());
        this.set("sendMarkup", new SendMarkupFunction());
        this.set("sendTextMessage", new SendTextMessageFunction());
        this.set("sendAnswerCallbackQuery", new SendAnswerCallbackQueryFunction());
        this.set("editMessageText", new EditMessageTextFunction());
        this.set("deleteCommands", new DeleteCommandsFunction());
        this.set("deleteMessage", new DeleteMessageFunction());

        this.set("id", telegramBot.getId());
    }

    /*
     *
     *
     * */
    public static class DeleteCommandsFunction extends OneArgFunction {
        public LuaValue call(LuaValue self) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            String response = telegramBotLuaTable.telegramBot.deleteCommands();
            return jsonStringToLuaValue(response);
        }
    }

    public static class EditMessageTextFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue dataLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            EditMessageText editMessageText = convert(LuaArgUtils.getLuaTableFromFunctionArgs(dataLuaValue, 2, "data"));
            String response = telegramBotLuaTable.telegramBot.editMessageText(editMessageText);
            return jsonStringToLuaValue(response);
        }

        private EditMessageText convert(LuaTable data) {
            String chatId = LuaArgUtils.getStringFromLuaTable(data, "data", "chatId");
            int messageId = LuaArgUtils.getIntFromLuaTable(data, "data", "messageId");
            String newText = LuaArgUtils.getStringFromLuaTable(data, "data", "newText");

            return new EditMessageText(chatId, messageId, newText);
        }
    }

    public static class DeleteMessageFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue dataLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            DeleteMessage deleteMessage = convert(LuaArgUtils.getLuaTableFromFunctionArgs(dataLuaValue, 2, "data"));
            String response = telegramBotLuaTable.telegramBot.deleteMessage(deleteMessage);
            return jsonStringToLuaValue(response);
        }

        private DeleteMessage convert(LuaTable data) {
            String chatId = LuaArgUtils.getStringFromLuaTable(data, "data", "chatId");
            int messageId = LuaArgUtils.getIntFromLuaTable(data, "data", "messageId");

            return new DeleteMessage(chatId, messageId);
        }
    }

    public static class SendTextMessageFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue dataLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            TextMessage textMessage = convert(LuaArgUtils.getLuaTableFromFunctionArgs(dataLuaValue, 2, "data"));
            String response = telegramBotLuaTable.telegramBot.sendTextMessage(textMessage);
            return jsonStringToLuaValue(response);
        }

        private TextMessage convert(LuaTable data) {
            String chatId = LuaArgUtils.getStringFromLuaTable(data, "data", "chatId");
            String text = LuaArgUtils.getStringFromLuaTable(data, "data", "text");

            return new TextMessage(chatId, text);
        }
    }

    public static class SendAnswerCallbackQueryFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue queryIdLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            String queryId = LuaArgUtils.getStringFromLuaValue(queryIdLuaValue, "queryId");
            String response = telegramBotLuaTable.telegramBot.sendAnswerCallbackQuery(queryId);

            return jsonStringToLuaValue(response);
        }
    }

    public static class SendMarkupFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue dataLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            Markup markup = convert(LuaArgUtils.getLuaTableFromFunctionArgs(dataLuaValue, 2, "data"));
            String response = telegramBotLuaTable.telegramBot.sendMarkup(markup);
            return jsonStringToLuaValue(response);
        }

        private Markup convert(LuaTable data) {
            String chatId = LuaArgUtils.getStringFromLuaTable(data, "data", "chatId");
            String text = LuaArgUtils.getStringFromLuaTable(data, "data", "text");

            LuaTable markupMatrixLuaTable = LuaArgUtils.getLuaTableFromLuaTable(data, "data", "markup");
            List<List<Button>> keyboard = new ArrayList<>();

            for (int i = 1; i <= markupMatrixLuaTable.length(); i++) {
                LuaValue rowLuaValue = markupMatrixLuaTable.get(i);
                LuaTable rowLuaTable = LuaArgUtils.getLuaTableFromLuaValue(rowLuaValue, "row");

                List<Button> rows = new ArrayList<>();

                for (int j = 1; j <= rowLuaTable.length(); j++) {
                    LuaValue buttonLuaValue = rowLuaTable.get(j);
                    LuaTable buttonLuaTable = LuaArgUtils.getLuaTableFromLuaValue(buttonLuaValue, "button");

                    String buttonText = LuaArgUtils.getStringFromLuaTable(buttonLuaTable, "button", "text");
                    String buttonCallbackData = LuaArgUtils.getStringFromLuaTable(buttonLuaTable, "button", "callbackData");

                    rows.add(new Button(buttonText, buttonCallbackData));
                }

                keyboard.add(rows);
            }

            return new Markup(new TextMessage(chatId, text), keyboard);
        }
    }

    public static class GetUpdateFunction extends OneArgFunction {
        public LuaValue call(LuaValue self) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);

            Optional<String> updateOpt = telegramBotLuaTable.telegramBot.getUpdate();
            if (updateOpt.isPresent()) {
                String json = updateOpt.get();
                return jsonStringToLuaValue(json);
            } else {
                return LuaValue.NIL;
            }
        }
    }

    public static class SetCommandsFunction extends TwoArgFunction {

        public LuaValue call(LuaValue self, LuaValue commandsLuaValue) {
            TelegramBotLuaTable telegramBotLuaTable = LuaArgUtils.getSelfFromFunctionArgs(self);
            LuaTable commandsLuaTable = LuaArgUtils.getLuaTableFromFunctionArgs(commandsLuaValue, 2, "commands");

            List<Command> commands = this.convertLuaTableToCommands(commandsLuaTable);
            String response = telegramBotLuaTable.telegramBot.setCommands(commands);
            return jsonStringToLuaValue(response);
        }

        private List<Command> convertLuaTableToCommands(LuaTable commandsLuaTable) {
            LuaValue[] keys = commandsLuaTable.keys();
            List<Command> commands = new ArrayList<>(keys.length);

            for (LuaValue key : keys) {
                LuaValue luaValue = commandsLuaTable.get(key);

                LuaTable commandLuaTable = LuaArgUtils.getLuaTableFromLuaValue(luaValue, "command");
                String command = LuaArgUtils.getStringFromLuaTable(commandLuaTable, "command", "command");
                String description = LuaArgUtils.getStringFromLuaTable(commandLuaTable, "command", "description");

                commands.add(new Command(command, description));
            }

            return commands;
        }
    }

    public static LuaValue jsonStringToLuaValue(String json) {
        try {
            JsonNode jsonNode = JSON_MAPPER.readTree(json);
            return LuaTypeConverter.toLua(jsonNode);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
