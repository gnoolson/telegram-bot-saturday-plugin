package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday_plugin_api.Functionality;
import gnoolson.saturday_plugin_api.FunctionalityFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FunctionalityFactoryImpl implements FunctionalityFactory {

    private final Telegram telegram;

    /*
     *
     *
     * */
    public Functionality getInstance() {
        return telegram;
    }

}
