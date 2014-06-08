/*package jk_5.nailed.server.command;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.entity.player.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;
import jk_5.nailed.map.*;

public class CommandWayPoint extends NailedCommand {

    public CommandWayPoint() {
        super("waypoint");
    }

    @Override
    public void processCommandPlayer(Player player, Map map, String[] args) {
        EntityPlayerMP sender = player.getEntity();
        if(args.length == 0){
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
        if("add".equals(args[0])){
            if(args.length > 2){
                if(args.length == 5){ // add <name> x y z
                    double x;
                    double y;
                    double z;
                    if(args[2].startsWith("~")){
                        x = Double.parseDouble((args[2].split("~"))[0]) + sender.posX;
                    }else{
                        x = sender.posX;
                    }
                    if(args[3].startsWith("~")){
                        y = Double.parseDouble((args[3].split("~"))[0]) + sender.posY;
                    }else{
                        y = sender.posY;
                    }
                    if(args[4].startsWith("~")){
                        z = Double.parseDouble((args[4].split("~"))[0]) + sender.posZ;
                    }else{
                        z = sender.posZ;
                    }
                    player.getCurrentMap().getLocationHandler().addLocation(args[1], new Location(x, y, z));
                }else if(args.length == 7){ // add <name> x y z pitch yaw
                    double x;
                    double y;
                    double z;
                    float pitch;
                    float yaw;
                    if(args[2].startsWith("~")){
                        x = Double.parseDouble((args[2].split("~"))[0]) + sender.posX;
                    }else{
                        x = sender.posX;
                    }
                    if(args[3].startsWith("~")){
                        y = Double.parseDouble((args[3].split("~"))[0]) + sender.posY;
                    }else{
                        y = sender.posY;
                    }
                    if(args[4].startsWith("~")){
                        z = Double.parseDouble((args[4].split("~"))[0]) + sender.posZ;
                    }else{
                        z = sender.posZ;
                    }
                    if(args[5].startsWith("~")){
                        pitch = Float.parseFloat((args[5].split("~"))[0]) + sender.cameraPitch;
                    }else{
                        pitch = sender.cameraPitch;
                    }
                    if(args[6].startsWith("~")){
                        yaw = Float.parseFloat((args[6].split("~"))[0]) + sender.cameraYaw;
                    }else{
                        yaw = sender.cameraYaw;
                    }
                    player.getCurrentMap().getLocationHandler().addLocation(args[1], new Location(x, y, z, pitch, yaw));
                }
            }
        }else if("remove".equals(args[0])){
            if(args.length != 2){
                throw new WrongUsageException(this.getCommandUsage(sender)); // remove <name>
            }
            player.getCurrentMap().getLocationHandler().removeLocation(args[1]);
        }else if("set".equals(args[0])){
            if(args.length != 2){
                throw new WrongUsageException(this.getCommandUsage(sender)); // set <name>
            }
            player.getCurrentMap().getLocationHandler().addLocation(args[1], player.getLocation());
        }else if("list".equals(args[0])){
            HashMap<String, Location> locations = player.getCurrentMap().getLocationHandler().getLocations();
            String keys = locations.keySet().toString();
            player.sendChat(keys);
        }else{
            throw new WrongUsageException(this.getCommandUsage(sender));
        }
    }
}*/
