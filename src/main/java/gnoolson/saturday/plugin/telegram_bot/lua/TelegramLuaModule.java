package gnoolson.saturday.plugin.telegram_bot.lua;

import gnoolson.saturday.plugin.telegram_bot.BotInfo;
import gnoolson.saturday.plugin.telegram_bot.Telegram;
import gnoolson.saturday.plugin.telegram_bot.TelegramBot;
import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.LuaModule;
import lombok.RequiredArgsConstructor;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.ZeroArgFunction;

import java.util.List;

public class TelegramLuaModule extends LuaModule {

    private Telegram telegram;

    /*
     *
     *
     * */
    public TelegramLuaModule() {
        this.set("getOrCreateBot", new GetOrCreateBotFunction(this));
        this.set("stopBot", new StopBotFunction(this));
        this.set("info", new InfoFunction(this));
    }

    public void updateFunctionality(Functionality functionality) {
        this.telegram = (Telegram) functionality;
    }

    public void release() {
    }

    public String name() {
        return "tg";
    }

    /*
     *
     *
     * */
    @RequiredArgsConstructor
    public static class InfoFunction extends ZeroArgFunction {

        private final TelegramLuaModule telegramLuaModule;

        public LuaValue call() {
            List<BotInfo> infoList = telegramLuaModule.telegram.info();
            return new BotInfoListLuaTable(infoList);
        }
    }

    @RequiredArgsConstructor
    public static class StopBotFunction extends OneArgFunction {

        private final TelegramLuaModule telegramLuaModule;

        public LuaValue call(LuaValue nameLuaValue) {
            telegramLuaModule.telegram.stop(nameLuaValue.checkjstring());
            return LuaValue.NIL;
        }
    }

    @RequiredArgsConstructor
    public static class GetOrCreateBotFunction extends TwoArgFunction {

        private final TelegramLuaModule telegramLuaModule;

        public LuaValue call(LuaValue idLuaValue, LuaValue pathLuaValue) {
            TelegramBot bot = telegramLuaModule.telegram.getOrCreateBot(idLuaValue.checkjstring(), pathLuaValue.checkjstring());
            return new TelegramBotLuaTable(bot);
        }
    }

}
