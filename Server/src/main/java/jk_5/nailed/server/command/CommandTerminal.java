package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTerminal extends NailedCommand {

    public CommandTerminal(){
        super("terminal");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.OpenTerminalGui(), sender.getEntity());
    }
}
