package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.mappack.Mappack;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandMap extends NailedCommand {

    @Override
    public String getCommandName(){
        return "map";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map currentMap, String[] args){
        if(args.length == 0) throw new WrongUsageException("/map <create:remove>");
        if(args[0].equalsIgnoreCase("create")){
            if(args.length == 1) throw new WrongUsageException("/map create <mappackName>");
            String name = args[1];
            Mappack mappack = MapLoader.instance().getMappack(name);
            if(mappack == null) throw new CommandException("Mappack does not exist");
            Map map = MapLoader.instance().newMapServerFor(mappack);

            IChatComponent component = new ChatComponentText("Loading " + map.getSaveFileName());
            component.func_150256_b().func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Go to the map")));
            component.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
            sender.func_145747_a(component);
        }else if(args[0].equalsIgnoreCase("remove")){
            if(args.length == 1) throw new WrongUsageException("/map remove <mapid>");
            Map map = null;
            for(Map m : MapLoader.instance().getMaps()){
                if(args[1].equalsIgnoreCase(m.getSaveFileName())){
                    map = m;
                    break;
                }
            }
            if(map == null) throw new CommandException("Map does not exist");
            map.unloadAndRemove();

            IChatComponent component = new ChatComponentText("Removed " + map.getSaveFileName());
            component.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
            sender.func_145747_a(component);
        }else throw new WrongUsageException("/map <create:remove>");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] strings){
        if(strings.length == 1) return getListOfStringsMatchingLastWord(strings, "create", "remove");
        else if(strings.length == 2){
            if(strings[0].equalsIgnoreCase("create")){
                List<String> ret = Lists.newArrayList();
                for(Mappack mappack : MapLoader.instance().getMappacks()){
                    ret.add(mappack.getMappackID());
                }
                return getListOfStringsFromIterableMatchingLastWord(strings, ret);
            }else if(strings[0].equalsIgnoreCase("remove")){
                List<String> ret = Lists.newArrayList();
                for(Map map : MapLoader.instance().getMaps()){
                    ret.add(map.getSaveFileName());
                }
                return getListOfStringsFromIterableMatchingLastWord(strings, ret);
            }
        }
        return Arrays.asList();
    }
}