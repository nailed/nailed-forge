package jk_5.nailed.crashreporter;

import jk_5.nailed.crashreporter.notificationhandlers.NotificationHandlerIrc;
import jk_5.nailed.crashreporter.pasteproviders.PasteProviderHastebin;
import jk_5.nailed.crashreporter.pasteproviders.PasteProviderPastebin;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CrashReporterTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile){
        Thread.currentThread().setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e){
                CrashReporter.report(t, e);
            }
        });

        try{
            for(int i = 0; i < args.size() - 1; i++){
                if(args.get(i).equals("--username")){
                    CrashReporter.username = args.get(i + 1);
                }
            }

            CrashReporter.getLogger().info("Found username: " + CrashReporter.username);

            HandlerRegistry.registerPasteProvider("pastebin", new PasteProviderPastebin());
            HandlerRegistry.registerPasteProvider("hastebin", new PasteProviderHastebin());

            HandlerRegistry.registerNotificationHandler("irc", new NotificationHandlerIrc());
        }catch(Throwable e){
            CrashReporter.report(e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader){
        classLoader.addTransformerExclusion("jk_5.nailed.crashreporter.transformer.");
        classLoader.registerTransformer("jk_5.nailed.crashreporter.transformer.CrashReportTransformer");
    }

    @Override
    public String getLaunchTarget(){
        return null;
    }

    @Override
    public String[] getLaunchArguments(){
        return new String[0];
    }
}
