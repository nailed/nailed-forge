package jk_5.nailed.server.command;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandLobby extends NailedCommand {

    public CommandLobby() {
        super("lobby");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        sender.teleportToMap(NailedAPI.getMapLoader().getLobby());
    }
}
