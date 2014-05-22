package jk_5.nailed.server.command;

import java.util.*;
import javax.annotation.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;
import jk_5.nailed.map.*;
import jk_5.nailed.map.script.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTerminal extends NailedCommand {

    public CommandTerminal() {
        super("terminal");
    }

    @Nullable
    @Override
    public List<String> getAliases() {
        return Arrays.asList("term");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        ServerMachine machine = ((NailedMap) map).getMachine();
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.OpenTerminalGui(machine.getInstanceId(), machine.getTerminal().getWidth(), machine.getTerminal().getHeight()), sender.getEntity());
        if(!machine.isOn()){
            machine.turnOn();
            machine.terminalChanged = true; //Force an update
            NailedMap nm = (NailedMap) map;
            nm.mounted = map.getMappack() == null; //Also force a remount of the mappack data, if we have a mappack
            nm.mappackMount = null;
        }
    }
}
