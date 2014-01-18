package jk_5.nailed.client.updateNotifier;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import jk_5.asyncirc.Conversation;
import jk_5.asyncirc.ConversationListener;
import jk_5.asyncirc.IrcConnection;
import jk_5.asyncirc.User;
import jk_5.nailed.client.NailedClient;
import jk_5.nailed.client.render.NotificationRenderer;
import lombok.RequiredArgsConstructor;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * No description given
 *
 * @author jk-5
 */
public class UpdateNotificationManager {

    private static IrcConnection connection;
    private static Conversation conversation;
    private static String mcversion = NailedClient.getMinecraftVersion();
    private static ExecutorService executor = Executors.newCachedThreadPool();
    private static Logger logger = LogManager.getLogger("Nailed|Updater");

    public static void main(String[] args){
        init();
    }

    public static void init(){
        /*ConfigTag tag = NailedClient.getConfig().getTag("notificationBot").useBraces();
        String host = tag.getTag("host").getValue("irc.reening.nl");
        int port = tag.getTag("port").getIntValue(6667);
        String channel = tag.getTag("channel").getValue("#nailedupdates");
        boolean enabled = tag.getTag("enabled").getBooleanValue(true);
        String username = "mc-" + Minecraft.getMinecraft().getSession().getUsername()*/
        String host = "irc.reening.nl";
        int port = 6667;
        String channel = "#nailedupdates";
        boolean enabled = true;
        String username = "jk-5";

        if(!enabled){
            logger.info("NotificationBot is disabled");
            return;
        }

        if(connection != null && connection.getChannel().isActive()){
            connection.close();
        }

        logger.info("Initializing NotificationBot");
        connection = new IrcConnection(host, port).setName("mc-" + username).connect().getConnection();
        conversation = connection.joinChannel(channel).conversation();
        conversation.addListener(new Listener());
        conversation.sendMessage("CONNECT " + username);
    }

    private static class Listener implements ConversationListener{
        @Override
        public void onMessage(String s, String message){
            if(s.startsWith("mc-")) return;
            if(!conversation.getUserFromNickname(s).isOp() || !conversation.getUserFromNickname(s).isHalfop() || !conversation.getUserFromNickname(s).isVoiced()){
                return;
            }
            Iterator<String> it = Splitter.on('|').split(message).iterator();
            List<String> data = Lists.newArrayList();
            while(it.hasNext()){
                data.add(it.next());
            }
            logger.debug("Received updater message: " + message);
            if(data.size() > 0){
                if(data.get(0).equalsIgnoreCase("notification")){
                    if(data.size() == 2) NotificationRenderer.addNotification(data.get(1));
                    if(data.size() == 3) NotificationRenderer.addNotification(data.get(1), new ResourceLocation(data.get(2)));
                    if(data.size() == 4) NotificationRenderer.addNotification(data.get(1), new ResourceLocation(data.get(2)), Integer.parseInt(data.get(2)));
                }else if(data.get(0).equalsIgnoreCase("update")){
                    String version = data.get(1);
                    String mcver = version.split("-", 2)[0];
                    String channel = data.get(2);
                    String url = data.get(3);
                    if(mcver.equals(mcversion)){
                        logger.info("New " + channel.toLowerCase() + " version available: " + version);
                        conversation.sendMessage("UPDATING");
                        executor.execute(new FileDownloader(url, new File("downloaded.jar")));
                    }else{
                        conversation.sendMessage("IGNORING");
                    }
                }else{
                    conversation.sendMessage("Fuck off! This is not a toy!");
                }
            }else{
                conversation.sendMessage("Fuck off! This is not a toy!");
            }
        }

        @Override
        public void onJoin(User user){

        }

        @Override
        public void onMode(User user, String s){

        }

        @Override
        public void onNick(User user, String s){

        }

        @Override
        public void onPart(User user, String s){

        }

        @Override
        public void onKick(User user, String s){

        }
    }

    @RequiredArgsConstructor
    private static class FileDownloader implements Runnable{

        private final String url;
        private final File dest;

        @Override
        public void run(){
            logger.info("Started downloading file");
            try{
                FileUtils.copyURLToFile(new URL(this.url), this.dest, 5000, 5000);
                conversation.sendMessage("DOWNLOADED");
                logger.info("File downloaded");
            }catch(IOException e){
                conversation.sendMessage("FAILED");
                StringWriter writer = new StringWriter();
                e.printStackTrace(new PrintWriter(writer));
                conversation.sendMessage(writer.toString().split("\n|\r|\r\n")[0]);
                logger.warn("Error while downloading update", e);
                logger.warn("This error is reported to the developers");
            }
        }
    }
}
