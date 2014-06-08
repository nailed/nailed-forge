/*package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.command.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;
import jk_5.nailed.camera.movements.*;
import jk_5.nailed.map.*;

public class CommandMove extends NailedCommand {

    public CommandMove() {
        super("move");
    }

    public void processCommandPlayer(Player player, Map map, String[] args) {
        if("bezier".equals(args[1])){
            List<Location> tempLocations = Lists.newArrayList();

            String[] locs = new String[args.length - 2];


            System.arraycopy(args, 3, locs, 0, args.length - 2);

            for(String loc : locs){
                Location nLocation = player.getCurrentMap().getLocationHandler().getLocation(loc);
                if(nLocation != null){
                    tempLocations.add(nLocation);
                }
            }

            Location[] locations = new Location[tempLocations.size()];

            for(int i = 0; i < tempLocations.size(); ++i){
                locations[i] = tempLocations.get(i);
            }

            NailedAPI.getMovementHandler().addPlayerMovement(NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]), new BezierMovement(locations, Integer.parseInt(args[2]) * 20, false));
        }else if("linear".equals(args[1])){
            Player nPlayer = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            int time = Integer.parseInt(args[2]) * 20;
            Location startLocation = nPlayer.getCurrentMap().getLocationHandler().getLocation(args[3]);
            Location endLocation = nPlayer.getCurrentMap().getLocationHandler().getLocation(args[4]);

            NailedAPI.getMovementHandler().addPlayerMovement(nPlayer, new LinearMovement(startLocation, endLocation, time, false));
        }else{
            throw new WrongUsageException(this.getCommandUsage(player.getEntity()));
        }
    }
}*/
