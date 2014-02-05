package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.player.Player;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSafehouse extends NailedCommand {

    @Override
    public String getCommandName(){
        return "safehouse";
    }

    @Override
    public void processCommandPlayer(final Player sender, Map map, String[] args){
        final Mappack mappack = NailedAPI.getMappackLoader().getMappack("safehouse");
        if(mappack == null) return; //TODO: message
        Map safehouse = NailedAPI.getMapLoader().createMapServer(mappack);
        sender.teleportToMap(safehouse);
    }
}
