package jk_5.nailed.server.command;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.ipc.mappack.IpcMappackRegistry;
import jk_5.nailed.ipc.packet.PacketRequestMappackLoad;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandMap extends NailedCommand implements SubpermissionCommand {

    private String createPerm;
    private String removePerm;
    private String listPerm;

    public CommandMap() {
        super("map");
    }

    @Override
    public void registerPermissions(String owner) {
        PermissionsManager.registerPermission(createPerm = owner + ".commands.map.create", RegisteredPermValue.OP);
        PermissionsManager.registerPermission(removePerm = owner + ".commands.map.remove", RegisteredPermValue.OP);
        PermissionsManager.registerPermission(listPerm = owner + ".commands.map.list", RegisteredPermValue.TRUE);
    }

    @Override
    public boolean hasPermission(String sender, String[] args) {
        if(args.length == 0) return true;
        if(args[0].equalsIgnoreCase("create")){
            PermissionsManager.getPerm(sender, createPerm).check();
        }else if(args[0].equalsIgnoreCase("remove")){
            PermissionsManager.getPerm(sender, removePerm).check();
        }else if(args[0].equalsIgnoreCase("list")){
            PermissionsManager.getPerm(sender, listPerm).check();
        }
        return true;
    }

    @Override
    public void processCommandWithMap(final ICommandSender sender, Map currentMap, String[] args) {
        if(args.length == 0){
            throw new WrongUsageException("/map <create:remove:list>");
        }
        if("create".equalsIgnoreCase(args[0])){
            if(args.length == 1){
                throw new WrongUsageException("/map create <mappackName>");
            }
            String name = args[1];
            Mappack mappack = NailedAPI.getMappackLoader().getMappack(name);
            if(mappack == null){
                if(IpcMappackRegistry.getRemoteMappacks().contains(name)){
                    IpcManager.instance().sendPacket(new PacketRequestMappackLoad(name));

                    IChatComponent component = new ChatComponentText("Sent load request to IPC server");
                    component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                    sender.addChatMessage(component);
                }else{
                    throw new CommandException("Mappack does not exist");
                }
            }else{
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
            }
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
        }else if("list".equalsIgnoreCase(args[0])){
            IChatComponent base = new ChatComponentText("");
            IChatComponent c = new ChatComponentText("Loaded maps: ");
            c.getChatStyle().setColor(EnumChatFormatting.GREEN);
            base.getChatStyle().setColor(EnumChatFormatting.GRAY);
            base.appendSibling(c);
            boolean first = true;
            for(Map map : NailedAPI.getMapLoader().getMaps()){
                if(!first) base.appendText(", ");
                IChatComponent comp = new ChatComponentText(map.getSaveFileName());
                comp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Click to teleport")));
                comp.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/goto " + map.getSaveFileName()));
                base.appendSibling(comp);
                first = false;
            }
            sender.addChatMessage(base);
        }else{
            throw new WrongUsageException("/map <create:remove:list>");
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, "create", "remove", "list");
        }else if(args.length == 2){
            if("create".equalsIgnoreCase(args[0])){
                List<String> ret = Lists.newArrayList();
                for(Mappack mappack : NailedAPI.getMappackLoader().getMappacks()){
                    ret.add(mappack.getMappackID());
                }
                ret.addAll(IpcMappackRegistry.getRemoteMappacks());
                return getOptions(args, ret);
            }else if("remove".equalsIgnoreCase(args[0])){
                List<String> ret = Lists.newArrayList();
                for(Map map : NailedAPI.getMapLoader().getMaps()){
                    ret.add(map.getSaveFileName());
                }
                return getOptions(args, ret);
            }
        }
        return null;
    }
}
