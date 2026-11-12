package gnoolson.saturday.plugin.telegram_bot.lua;

import gnoolson.saturday.plugin.telegram_bot.BotInfo;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import java.util.List;

public class BotInfoListLuaTable extends LuaTable {

    public BotInfoListLuaTable(List<BotInfo> infoList) {
        int index = 1;

        for (BotInfo botInfo : infoList) {
            LuaTable luaTable = new LuaTable();
            luaTable.set("id", LuaValue.valueOf(botInfo.getId()));
            luaTable.set("username", LuaValue.valueOf(botInfo.getUsername()));
            luaTable.set("created", LuaValue.valueOf((double) botInfo.getCreatedTs()));
            this.set(index, luaTable);

            ++index;
        }
    }

}
