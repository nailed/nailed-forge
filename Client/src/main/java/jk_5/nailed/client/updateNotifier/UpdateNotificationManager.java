package jk_5.nailed.client.updateNotifier;

import jk_5.nailed.client.NailedClient;
import jk_5.nailed.client.NailedLog;
import jk_5.nailed.client.render.NotificationRenderer;
import jk_5.nailed.client.util.config.ConfigTag;
import net.minecraft.util.ResourceLocation;

/**
 * No description given
 *
 * @author jk-5
 */
public class UpdateNotificationManager {

    private static NotificationBot bot;

    public static void main(String[] args){
        init();
    }

    public static void init(){
        ConfigTag tag = NailedClient.getConfig().getTag("notificationBot").useBraces();
        String host = tag.getTag("host").getValue("irc.reening.nl");
        int port = tag.getTag("port").getIntValue(6667);
        String channel = tag.getTag("channel").getValue("#nailednotification");
        boolean enabled = tag.getTag("enabled").getBooleanValue(true);

        if(!enabled){
            NailedLog.info("NotificationBot is disabled");
            return;
        }
        NailedLog.info("Initializing NotificationBot");
        bot = new NotificationBot(host, port, channel);
    }

    public static void handleIncoming(String message){
        String data[] = message.split("|");
        if(data.length > 0){
            if(data[0].equals("notification")){
                if(data.length == 2) NotificationRenderer.addNotification(data[1]);
                if(data.length == 3) NotificationRenderer.addNotification(data[1], null, Integer.parseInt(data[2]));
                if(data.length == 4) NotificationRenderer.addNotification(data[1], new ResourceLocation(data[3]), Integer.parseInt(data[2]));
            }
        }
    }

    private static class NotificationBotConnectThread extends Thread {

        private NotificationBotConnectThread() {
            this.setName("NotificationBot");
            this.setDaemon(false);
        }

        @Override
        public void run() {
            UpdateNotificationManager.bot.connect();
        }
    }
}
