package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.mappack.Mappack;
import jk_5.nailed.players.Player;

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
        final Mappack mappack = MapLoader.instance().getMappack("safehouse");
        if(mappack == null) return; //TODO: message
        Map safehouse = MapLoader.instance().newMapServerFor(mappack);
        sender.teleportToMap(safehouse);
    }
}
