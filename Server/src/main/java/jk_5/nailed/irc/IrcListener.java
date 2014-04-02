package jk_5.nailed.irc;

import jk_5.asyncirc.ConversationListener;
import jk_5.asyncirc.User;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class IrcListener implements ConversationListener {

    private final IrcChannel channel;

    @Override
    public void onMessage(User sender, String message){
        IrcHelper.sendIrcChatToGame(this.channel, sender, message);
    }

    @Override
    public void onJoin(User user){

    }

    @Override
    public void onMode(User u, String change){

    }

    @Override
    public void onNick(User u, String oldNick){

    }

    @Override
    public void onPart(User u, String message){

    }

    @Override
    public void onKick(User u, String message){

    }
}