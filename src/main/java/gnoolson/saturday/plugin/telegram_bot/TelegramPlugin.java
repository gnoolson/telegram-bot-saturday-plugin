package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday_plugin_api.SaturdayPlugin;
import lombok.Getter;

@Getter
public class TelegramPlugin extends SaturdayPlugin {

    private Telegram telegram;

    /*
     *
     *
     * */
    @Override
    public void start() {
        this.telegram = new TelegramImpl(this.scriptLauncherGateway);
    }

    @Override
    public void stop() {
        this.telegram.shutdown();
        this.telegram = null;
    }

}
