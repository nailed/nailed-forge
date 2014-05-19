package jk_5.nailed.server.command;

import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.CommandException;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSafehouse extends NailedCommand {

    public CommandSafehouse(){
        super("safehouse");
    }

    @Override
    public void processCommandPlayer(final Player sender, Map map, String[] args){
        final Mappack mappack = NailedAPI.getMappackLoader().getMappack("safehouse");
        if(mappack == null){
            NailedLog.error("No safehouse mappack was found. Not teleporting {}", sender.getUsername());
            throw new CommandException("No safehouse mappack was found");
        }
        NailedAPI.getMapLoader().createMapServer(mappack, new Callback<Map>() {
            @Override
            public void callback(Map obj){
                sender.teleportToMap(obj);
            }
        });
    }
}
