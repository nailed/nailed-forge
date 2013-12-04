package jk_5.nailed.client.player;

import codechicken.lib.data.MCDataInput;
import com.google.common.collect.Lists;
import jk_5.nailed.players.Player;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class ClientPlayerPool {

    private static final ClientPlayerPool INSTANCE = new ClientPlayerPool();

    public static ClientPlayerPool instance() {
        return INSTANCE;
    }

    private final List<Player> players = Lists.newArrayList();

    public void readPlayers(MCDataInput input){
        this.players.clear();
    }
}
