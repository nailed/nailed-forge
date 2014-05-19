package jk_5.nailed.server.command;

import io.netty.channel.ChannelFuture;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MappackLoader;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.permissions.NailedPermissionFactory;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.PermissionsManager;

import java.util.List;

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
            if(rule.equals("permissions")){
                PermBuilderFactory<?> factory = PermissionsManager.getPermFactory();
                if(factory instanceof NailedPermissionFactory){
                    NailedPermissionFactory permFactory = (NailedPermissionFactory) factory;
                    permFactory.readConfig();
                    ChatComponentText component = new ChatComponentText("Reloaded permissions!");
                    component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                    sender.addChatMessage(component);
                }
            }else if(rule.equals("mappacks")) {
                NailedAPI.getMappackLoader().loadMappacks(new Callback<MappackLoader>() {
                    @Override
                    public void callback(MappackLoader obj) {
                        IChatComponent component = new ChatComponentText("Successfully loaded " + obj.getMappacks().size() + " mappacks");
                        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                        sender.addChatMessage(component);
                    }
                });
            }else if(rule.equals("ipc")){
                NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
                    @Override
                    public void run(){
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

    private void printUsage(ICommandSender sender){
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
