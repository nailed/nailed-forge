package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.NailedMap;
import jk_5.nailed.map.script.ServerMachine;
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
        ServerMachine machine = ((NailedMap) map).getMachine();
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.OpenTerminalGui(machine.getInstanceId(), machine.getTerminal().getWidth(), machine.getTerminal().getHeight()), sender.getEntity());
        machine.turnOn();
        machine.terminalChanged = true; //Force an update
    }
}
