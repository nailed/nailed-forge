package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.command.*;
import net.minecraft.event.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandMap extends NailedCommand {

    public CommandMap() {
        super("map");
    }

    @Override
    public void processCommandWithMap(final ICommandSender sender, Map currentMap, String[] args) {
        if(args.length == 0){
            throw new WrongUsageException("/map <create:remove>");
        }
        if("create".equalsIgnoreCase(args[0])){
            if(args.length == 1){
                throw new WrongUsageException("/map create <mappackName>");
            }
            String name = args[1];
            Mappack mappack = NailedAPI.getMappackLoader().getMappack(name);
            if(mappack == null){
                throw new CommandException("Mappack does not exist");
            }
            IChatComponent component = new ChatComponentText("Loading " + mappack.getMappackID());
            component.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(component);
            NailedAPI.getMapLoader().createMapServer(mappack, new Callback<Map>() {
                @Override
                public void callback(Map obj) {
                    IChatComponent component = new ChatComponentText("Loaded ");
                    component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                    IChatComponent comp = new ChatComponentText(obj.getSaveFileName());
                    comp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Teleport to the map")));
                    comp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + obj.getSaveFileName()));
                    component.appendSibling(comp);
                    sender.addChatMessage(component);
                }
            });
        }else if("remove".equalsIgnoreCase(args[0])){
            if(args.length == 1){
                throw new WrongUsageException("/map remove <mapid>");
            }
            Map map = null;
            for(Map m : NailedAPI.getMapLoader().getMaps()){
                if(args[1].equalsIgnoreCase(m.getSaveFileName())){
                    map = m;
                    break;
                }
            }
            if(map == null){
                throw new CommandException("Map does not exist");
            }
            map.unloadAndRemove();

            IChatComponent component = new ChatComponentText("Removed " + map.getSaveFileName());
            component.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(component);
        }else{
            throw new WrongUsageException("/map <create:remove>");
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, "create", "remove");
        }else if(args.length == 2){
            if("create".equalsIgnoreCase(args[0])){
                List<String> ret = Lists.newArrayList();
                for(Mappack mappack : NailedAPI.getMappackLoader().getMappacks()){
                    ret.add(mappack.getMappackID());
                }
                return getOptions(args, ret);
            }else if("remove".equalsIgnoreCase(args[0])){
                List<String> ret = Lists.newArrayList();
                for(Map map : NailedAPI.getMapLoader().getMaps()){
                    ret.add(map.getSaveFileName());
                }
                return getOptions(args, ret);
            }
        }
        return Arrays.asList();
    }
}
