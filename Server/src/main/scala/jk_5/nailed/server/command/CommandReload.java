package jk_5.nailed.server.command;

import java.util.*;

import io.netty.channel.*;

import net.minecraft.command.*;
import net.minecraft.util.*;

import net.minecraftforge.permissions.api.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.concurrent.*;
import jk_5.nailed.api.concurrent.scheduler.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.ipc.*;
import jk_5.nailed.permissions.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReload extends NailedCommand {

    public CommandReload() {
        super("reload");
    }

    @Override
    public void processCommandWithMap(final ICommandSender sender, Map map, String[] args) {
        if(args.length != 1){
            this.printUsage(sender);
        }else{
            String rule = args[0];
            if("permissions".equals(rule)){
                PermBuilderFactory<?> factory = PermissionsManager.getPermFactory();
                if(factory instanceof NailedPermissionFactory){
                    NailedPermissionFactory permFactory = (NailedPermissionFactory) factory;
                    permFactory.readConfig();
                    ChatComponentText component = new ChatComponentText("Reloaded permissions!");
                    component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                    sender.addChatMessage(component);
                }
            }else if("mappacks".equals(rule)){
                NailedAPI.getMappackLoader().loadMappacks(new Callback<MappackLoader>() {
                    @Override
                    public void callback(MappackLoader obj) {
                        IChatComponent component = new ChatComponentText("Successfully loaded " + obj.getMappacks().size() + " mappacks");
                        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                        sender.addChatMessage(component);
                    }
                });
            }else if("ipc".equals(rule)){
                NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
                    @Override
                    public void run() {
                        ChannelFuture future = IpcManager.instance().close();
                        if(future != null){
                            future.syncUninterruptibly();
                        }
                        IpcManager.instance().start();
                    }
                });
            }else{
                this.printUsage(sender);
            }
        }
    }

    private void printUsage(ICommandSender sender) {
        IChatComponent comp = new ChatComponentText("Usage: /reload <permissions/mappacks>");
        comp.getChatStyle().setColor(EnumChatFormatting.RED);
        sender.addChatMessage(comp);
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, "permissions", "mappacks", "ipc");
        }else{
            return null;
        }
    }
}
