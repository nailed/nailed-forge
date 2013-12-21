package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTime extends CommandBase {

    @Override
    public String getCommandName(){
        return "time";
    }

    @Override
    public String getCommandUsage(ICommandSender sender){
        return "/time - Change the time";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        World world = sender.getEntityWorld();
        boolean hasWorld = world != null;
        Map map = MapLoader.instance().getMap(world);
        if(args.length > 0){
            if(args[0].equals("set")){
                if(args.length > 1){
                    int target;
                    if(args[1].equals("day")) target = 6000;
                    else if(args[1].equals("night")) target = 18000;
                    else target = parseIntBounded(sender, args[1], 0, 23999);
                    if(!hasWorld && args.length == 3){
                        map = MapLoader.instance().getMap(parseInt(sender, args[2]));
                    }
                    if(map != null){
                        map.getWorld().setWorldTime(target);
                    }
                }
            }else{
                try{
                    int number = parseInt(sender, args[0]);
                    map = MapLoader.instance().getMap(number);
                    if(map != null){
                        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Current time in " + map.getSaveFileName() + ": " + map.getWorld().getWorldTime()));
                    }
                }catch(NumberInvalidException e){

                }
            }
        }else if(hasWorld){
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Current time in " + map.getSaveFileName() + ": " + map.getWorld().getWorldTime()));
        }
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
