package jk_5.nailed.crashreporter;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import jk_5.nailed.crashreporter.notificationhandlers.NotificationHandlerIrc;
import jk_5.nailed.crashreporter.pasteproviders.PasteProviderHastebin;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@IFMLLoadingPlugin.TransformerExclusions({"jk_5.nailed.crashreporter.transformer."})
public class Loader implements IFMLLoadingPlugin {

    public Loader(){
        HandlerRegistry.registerPasteProvider("hastebin", new PasteProviderHastebin());

        HandlerRegistry.registerNotificationHandler("irc", new NotificationHandlerIrc());
    }

    @Override
    public String[] getASMTransformerClass(){
        return new String[]{
                "jk_5.nailed.crashreporter.transformer.CrashReportTransformer"
        };
    }

    @Override
    public String getModContainerClass(){
        return "jk_5.nailed.crashreporter.ReporterModContainer";
    }

    @Override
    public String getSetupClass(){
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data){

    }

    @Override
    public String getAccessTransformerClass(){
        return null;
    }
}
