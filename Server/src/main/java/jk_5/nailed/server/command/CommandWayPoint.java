package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.map.Location;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;

/**
 * Created by matthias on 16-5-14.
 */
public class CommandWayPoint extends NailedCommand {

    public CommandWayPoint(){
        super("waypoint");
    }

    @Override
    public void processCommandPlayer(Player player, Map map, String[] args){
        EntityPlayerMP sender = player.getEntity();
        if (args.length == 0){
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
        if(args[0].equals("add")){
            if(args.length > 2){
                if(args.length == 5){ // add <name> x y z
                    double x;
                    double y;
                    double z;
                    if(args[2].startsWith("~")){
                        x = Double.parseDouble((args[2].split("~"))[0]) + sender.posX;
                    }else x = sender.posX;
                    if(args[3].startsWith("~")){
                        y = Double.parseDouble((args[3].split("~"))[0]) + sender.posY;
                    }else y = sender.posY;
                    if(args[4].startsWith("~")){
                        z = Double.parseDouble((args[4].split("~"))[0]) + sender.posZ;
                    }else z = sender.posZ;
                    player.getCurrentMap().getLocationHandler().addLocation(args[1], new Location(x, y, z));
                } else if(args.length == 7) { // add <name> x y z pitch yaw
                    double x;
                    double y;
                    double z;
                    float pitch;
                    float yaw;
                    if(args[2].startsWith("~")){
                        x = Double.parseDouble((args[2].split("~"))[0]) + sender.posX;
                    }else x = sender.posX;
                    if(args[3].startsWith("~")){
                        y = Double.parseDouble((args[3].split("~"))[0]) + sender.posY;
                    }else y = sender.posY;
                    if(args[4].startsWith("~")){
                        z = Double.parseDouble((args[4].split("~"))[0]) + sender.posZ;
                    }else z = sender.posZ;
                    if(args[5].startsWith("~")){
                        pitch = Float.parseFloat((args[5].split("~"))[0]) + sender.cameraPitch;
                    }else pitch = sender.cameraPitch;
                    if(args[6].startsWith("~")){
                        yaw = Float.parseFloat((args[6].split("~"))[0]) + sender.cameraYaw;
                    }else yaw = sender.cameraYaw;
                    player.getCurrentMap().getLocationHandler().addLocation(args[1], new Location(x, y, z, pitch, yaw));
                }
            }
        }else if(args[0].equals("remove")){
            if(args.length != 2) throw new WrongUsageException(this.getCommandUsage(sender)); // remove <name>
            player.getCurrentMap().getLocationHandler().removeLocation(args[1]);
        }else if(args[0].equals("set")){
            if(args.length != 2) throw new WrongUsageException(this.getCommandUsage(sender)); // set <name>
            player.getCurrentMap().getLocationHandler().addLocation(args[1], player.getLocation());
        }else if(args[0].equals("list")) {
            HashMap<String, Location> locations = player.getCurrentMap().getLocationHandler().getLocations();
            String keys = locations.keySet().toString();
            player.sendChat(keys);
        }else{
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }
}
