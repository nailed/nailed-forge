package jk_5.nailed.crashreporter.notificationhandlers;

import jk_5.asyncirc.Conversation;
import jk_5.asyncirc.IrcConnection;
import jk_5.nailed.crashreporter.NotificationHandler;

/**
 * No description given
 *
 * @author jk-5
 */
public class NotificationHandlerIrc implements NotificationHandler {

    @Override
    public void notify(String title, String text, String url) throws NotifyException{
        IrcConnection connection = new IrcConnection("irc.reening.nl");
        Conversation conversation = connection.setName("Reporter").connect().syncUninterruptibly().joinChannel("#crashes").syncUninterruptibly().conversation();
        conversation.sendMessage(url);
        connection.close();
    }
}
