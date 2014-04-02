package jk_5.nailed.irc;

import jk_5.asyncirc.Conversation;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
public class IrcChannel {

    @Getter private String name;
    @Getter private String password;
    @Getter private boolean autojoin;
    @Getter @Setter private Conversation conversation;

    public static IrcChannel read(ConfigTag tag){
        IrcChannel channel = new IrcChannel();
        channel.name = tag.getTag("name").getValue("");
        channel.password = tag.getTag("password").getValue();
        channel.autojoin = tag.getTag("autojoin").getBooleanValue(true);
        return channel;
    }
}
