package jk_5.nailed.server.command;

import net.minecraft.command.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSafehouse extends NailedCommand {

    public CommandSafehouse() {
        super("safehouse");
    }

    @Override
    public void processCommandPlayer(final Player sender, Map map, String[] args) {
        final Mappack mappack = NailedAPI.getMappackLoader().getMappack("safehouse");
        if(mappack == null){
            NailedLog.error("No safehouse mappack was found. Not teleporting {}", sender.getUsername());
            throw new CommandException("No safehouse mappack was found");
        }
        NailedAPI.getMapLoader().createMapServer(mappack, new Callback<Map>() {
            @Override
            public void callback(Map obj) {
                sender.teleportToMap(obj);
            }
        });
    }
}
