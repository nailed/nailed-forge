package jk_5.nailed.crashreporter;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import jk_5.nailed.crashreporter.notificationhandlers.NotificationHandlerIrc;
import jk_5.nailed.crashreporter.pasteproviders.PasteProviderHastebin;
import jk_5.nailed.crashreporter.pasteproviders.PasteProviderPastebin;

import java.io.File;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
@IFMLLoadingPlugin.TransformerExclusions({"jk_5.nailed.crashreporter.transformer."})
public class Loader implements IFMLLoadingPlugin {

    public static File coremodLocation;

    public Loader(){
        HandlerRegistry.registerPasteProvider("pastebin", new PasteProviderPastebin());
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
        return "jk_5.nailed.crashreporter.CRSanityChecker";
    }

    @Override
    public void injectData(Map<String, Object> data){
        if(data.containsKey("coremodLocation")){
            coremodLocation = (File) data.get("coremodLocation");
        }
    }

    @Override
    public String getAccessTransformerClass(){
        return null;
    }
}
