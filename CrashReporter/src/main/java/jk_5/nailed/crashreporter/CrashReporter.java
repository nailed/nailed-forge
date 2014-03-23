package jk_5.nailed.crashreporter;

import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * No description given
 *
 * @author jk-5
 */
public class CrashReporter {

    @Getter
    private static final Logger logger = LogManager.getLogger("CrashReporter");
    public static String username = "unknown";

    /**
     * This method is called by an ASM hook we inject into CrashReport.saveToFile()
     *
     * @param cause The cause of the crash
     * @param text The content of the crashreport
     */
    @SuppressWarnings("unused")
    public static void report(Throwable cause, String text){
        String message = cause.toString();
        /*try{
            kickAllPlayers();
        }catch(Exception ignored){}*/

        String link = null;
        for(PasteProvider provider : HandlerRegistry.getPasteProviders()){
            try{
                link = provider.paste(message, text);
                break;
            }catch(Throwable e){
                logger.error("Error while pasting to " + provider.getClass().getSimpleName(), e);
            }
        }

        if(link == null){
            logger.error("No pastebin providers could handle the request");
            link = "<No link>";
        }

        logger.info("Posted the crashreport to " + link);

        for(NotificationHandler handler : HandlerRegistry.getNotificationHandlers()){
            try{
                handler.notify(message, text, link);
            }catch(Throwable e){
                logger.error(handler.getClass().getSimpleName() + " has thrown an Exception while attempting to notify", e);
            }
        }
    }

    private static void kickAllPlayers(){
        /*ServerConfigurationManager manager = MinecraftServer.getServer().getConfigurationManager();
        while(!manager.playerEntityList.isEmpty()){
            ((EntityPlayerMP) manager.playerEntityList.get(0)).playerNetServerHandler.kickPlayerFromServer("Server Crashed!");
        }*/
    }

    public static void report(Thread thread, Throwable cause){
        StringBuilder builder = new StringBuilder();
        builder.append("**** Nailed crashed while starting ****");
        builder.append("\n\n");
        builder.append("Time: ");
        builder.append(new SimpleDateFormat().format(new Date()));
        builder.append("\n");
        builder.append("Username: ");
        builder.append(username);
        builder.append("\n");
        builder.append("Crashed Thread: ");
        builder.append(thread.getName());
        builder.append("\n\n");
        appendThrowable(builder, cause);
        builder.append("\n");
        report(cause, builder.toString());
    }

    public static void report(Throwable cause){
        report(Thread.currentThread(), cause);
    }

    private static void appendThrowable(StringBuilder b, Throwable t){
        StringWriter stringwriter = null;
        PrintWriter printwriter = null;
        try{
            stringwriter = new StringWriter();
            printwriter = new PrintWriter(stringwriter);
            t.printStackTrace(printwriter);
            b.append(stringwriter.toString());
        }catch(Exception e){
            b.append(t.toString());
        }finally{
            if(stringwriter != null) try{stringwriter.close();}catch(Exception ignored){}
            if(printwriter != null) try{printwriter.close();}catch(Exception ignored){}
        }
    }
}
