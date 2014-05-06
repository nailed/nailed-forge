package jk_5.nailed.irc;

import jk_5.asyncirc.Conversation;
import jk_5.nailed.util.config.ConfigTag;

/**
 * No description given
 *
 * @author jk-5
 */
public class IrcChannel {

    private String name;
    private String password;
    private boolean autojoin;
    private Conversation conversation;

    public static IrcChannel read(ConfigTag tag){
        IrcChannel channel = new IrcChannel();
        channel.name = tag.getTag("name").getValue("");
        channel.password = tag.getTag("password").getValue();
        channel.autojoin = tag.getTag("autojoin").getBooleanValue(true);
        return channel;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAutojoin() {
        return autojoin;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }
}
