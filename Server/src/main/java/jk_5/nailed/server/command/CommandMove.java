package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.camera.Movements.BezierMovement;
import jk_5.nailed.camera.Movements.LinearMovement;
import jk_5.nailed.map.Location;
import net.minecraft.command.WrongUsageException;

import java.util.List;

/**
 * Created by matthias on 16-5-14.
 */
public class CommandMove extends NailedCommand {
    public CommandMove(){super("move");}

    public void processCommandPlayer(Player player, Map map, String[] args) {
        if(args[1].equals("bezier")){
            List<Location> tempLocations = Lists.newArrayList();

            String[] locs = new String[args.length - 2];


            System.arraycopy(args, 3, locs, 0, args.length - 2);

            for (String loc : locs) {
                Location nLocation = player.getCurrentMap().getLocationHandler().getLocation(loc);
                if (nLocation != null) tempLocations.add(nLocation);
            }

            Location[] locations = new Location[tempLocations.size()];

            for (int i = 0; i < tempLocations.size(); ++i) {
                locations[i] = tempLocations.get(i);
            }

            NailedAPI.getMovementHandler().addPlayerMovement(NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]), new BezierMovement(locations, Integer.parseInt(args[2]) * 20, false));
        } else if(args[1].equals("linear")){
            Player nPlayer = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            int time = Integer.parseInt(args[2]) * 20;
            Location startLocation = nPlayer.getCurrentMap().getLocationHandler().getLocation(args[3]);
            Location endLocation = nPlayer.getCurrentMap().getLocationHandler().getLocation(args[4]);

            NailedAPI.getMovementHandler().addPlayerMovement(nPlayer, new LinearMovement(startLocation, endLocation, time, false));
        } else throw new WrongUsageException(this.getCommandUsage(player.getEntity()));
    }
}
