package jk_5.nailed.server.command;

import net.minecraft.command.*;
import net.minecraft.entity.player.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandRandomSpawnpoint extends NailedCommand {

    public CommandRandomSpawnpoint() {
        super("randomspawnpoint");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        if(args.length == 0){
            TeleportOptions options = new TeleportOptions();
            options.setLocation(map.getRandomSpawnpoint());
            NailedAPI.getTeleporter().teleportEntity(sender.getEntity(), options);
        }else{
            this.processCommandWithMap(sender.getEntity(), map, args);
        }
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 1){
            EntityPlayerMP[] players = getPlayersList(sender, args[0]);
            for(EntityPlayerMP player : players){
                TeleportOptions options = new TeleportOptions();
                options.setLocation(map.getRandomSpawnpoint());
                NailedAPI.getTeleporter().teleportEntity(player, options);
            }
        }else{
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }
}
