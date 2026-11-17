package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday.plugin.telegram_bot.lua.TelegramBotLuaTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TelegramBotLuaTableTest {

    @Test
    public void editMessageTextFunction() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.editMessageText(Mockito.any())).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.EditMessageTextFunction editMessageTextFunction = new TelegramBotLuaTable.EditMessageTextFunction();

        LuaTable editMessageText = new LuaTable();
        editMessageText.set("chatId", "qwerty");
        editMessageText.set("messageId", 1234);
        editMessageText.set("newText", "text_______");

        editMessageTextFunction.call(telegramBotLua, editMessageText);

        Mockito.verify(telegramBot).editMessageText(new EditMessageText("qwerty", 1234, "text_______"));
    }

    @Test
    public void getUpdate() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.getUpdate()).thenReturn(Optional.of("{\"key\":\"value\"}"));

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.GetUpdateFunction getUpdateFunction = new TelegramBotLuaTable.GetUpdateFunction();
        LuaValue json = getUpdateFunction.call(telegramBotLua);

        LuaTable updateLuaTable = json.checktable();
        Assertions.assertEquals(LuaString.valueOf("value"), updateLuaTable.get("key"));
    }

    @Test
    public void setCommands() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.setCommands(Mockito.any())).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.SetCommandsFunction setCommandsFunction = new TelegramBotLuaTable.SetCommandsFunction();

        LuaTable data = new LuaTable();
        data.set(1, new LuaTable() {{
            set("command", "_COMMAND_");
            set("description", "_DESCRIPTION_");
        }});
        data.set(2, new LuaTable() {{
            set("command", "_COMMAND_1_");
            set("description", "_DESCRIPTION_1_");
        }});

        setCommandsFunction.call(telegramBotLua, data);

        Mockito.verify(telegramBot).setCommands(new ArrayList<Command>() {{
            add(new Command("_COMMAND_", "_DESCRIPTION_"));
            add(new Command("_COMMAND_1_", "_DESCRIPTION_1_"));
        }});
    }

    @Test
    public void sendMarkup() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.sendMarkup(Mockito.any())).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.SendMarkupFunction sendMarkupFunction = new TelegramBotLuaTable.SendMarkupFunction();

        LuaTable data = new LuaTable();
        data.set("chatId", "9999");
        data.set("text", "__MARKUP__");
        data.set("markup", new LuaTable() {{
            set(1, new LuaTable() {{
                new LuaTable() {{
                    set("text", "BUTTON_1_1");
                    set("callbackData", "DATA_1_1");
                }};
                new LuaTable() {{
                    set("text", "BUTTON_1_2");
                    set("callbackData", "DATA_1_2");
                }};
            }});
            set(2, new LuaTable() {{
                new LuaTable() {{
                    set("text", "BUTTON_2_1");
                    set("callbackData", "DATA_2_1");
                }};
                new LuaTable() {{
                    set("text", "BUTTON_2_2");
                    set("callbackData", "DATA_2_2");
                }};
            }});
        }});

        sendMarkupFunction.call(telegramBotLua, data);

        Mockito.verify(telegramBot).sendMarkup(new Markup(new TextMessage("9999", "__MARKUP__"), new ArrayList<List<Button>>() {{
            add(new ArrayList<Button>() {{
                new Button("BUTTON_1_1", "DATA_1_1");
                new Button("BUTTON_1_2", "DATA_1_2");
            }});
            add(new ArrayList<Button>() {{
                new Button("BUTTON_2_1", "DATA_2_1");
                new Button("BUTTON_2_2", "DATA_2_2");
            }});
        }}));
    }

    @Test
    public void sendTextMessage() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.sendTextMessage(Mockito.any())).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.SendTextMessageFunction sendTextMessageFunction = new TelegramBotLuaTable.SendTextMessageFunction();

        LuaTable data = new LuaTable();
        data.set("chatId", "9999");
        data.set("text", "__TEXT__");

        sendTextMessageFunction.call(telegramBotLua, data);

        Mockito.verify(telegramBot).sendTextMessage(new TextMessage("9999", "__TEXT__"));
    }

    @Test
    public void sendAnswerCallbackQuery() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.sendAnswerCallbackQuery(Mockito.any())).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.SendAnswerCallbackQueryFunction sendAnswerCallbackQueryFunction = new TelegramBotLuaTable.SendAnswerCallbackQueryFunction();
        sendAnswerCallbackQueryFunction.call(telegramBotLua, LuaValue.valueOf("__query_id__"));

        Mockito.verify(telegramBot).sendAnswerCallbackQuery("__query_id__");
    }

    @Test
    public void deleteCommands() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.deleteCommands()).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.DeleteCommandsFunction deleteCommandsFunction = new TelegramBotLuaTable.DeleteCommandsFunction();
        deleteCommandsFunction.call(telegramBotLua);

        Mockito.verify(telegramBot).deleteCommands();
    }

    @Test
    public void deleteMessage() {
        TelegramBot telegramBot = Mockito.mock(TelegramBot.class);
        Mockito.when(telegramBot.getId()).thenReturn("00000");
        Mockito.when(telegramBot.deleteMessage(Mockito.any())).thenReturn("{}");

        TelegramBotLuaTable telegramBotLua = new TelegramBotLuaTable(telegramBot);

        TelegramBotLuaTable.DeleteMessageFunction deleteMessageFunction = new TelegramBotLuaTable.DeleteMessageFunction();

        LuaTable data = new LuaTable();
        data.set("chatId", "9999");
        data.set("messageId", 1234);

        deleteMessageFunction.call(telegramBotLua, data);

        Mockito.verify(telegramBot).deleteMessage(new DeleteMessage("9999", 1234));
    }

}
