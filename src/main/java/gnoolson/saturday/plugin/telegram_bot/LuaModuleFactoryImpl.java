package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday.plugin.telegram_bot.lua.TelegramLuaModule;
import gnoolson.saturday_plugin_api.LuaModule;
import gnoolson.saturday_plugin_api.LuaModuleFactory;


public class LuaModuleFactoryImpl implements LuaModuleFactory {

    public LuaModule getInstance() {
        return new TelegramLuaModule();
    }

}
