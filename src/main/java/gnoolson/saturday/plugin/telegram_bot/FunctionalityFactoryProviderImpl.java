package gnoolson.saturday.plugin.telegram_bot;

import gnoolson.saturday_plugin_api.FunctionalityFactory;
import gnoolson.saturday_plugin_api.FunctionalityFactoryProvider;
import gnoolson.saturday_plugin_api.SaturdayPlugin;
import org.pf4j.Extension;

@Extension
public class FunctionalityFactoryProviderImpl implements FunctionalityFactoryProvider {

    private FunctionalityFactory functionalityFactory;

    /*
     *
     *
     * */
    @Override
    public FunctionalityFactory getFactory() {
        return this.functionalityFactory;
    }

    @Override
    public void setup(SaturdayPlugin saturdayPlugin) {
        TelegramPlugin telegramPlugin = (TelegramPlugin) saturdayPlugin;
        this.functionalityFactory = new FunctionalityFactoryImpl(telegramPlugin.getTelegram());
    }

}
