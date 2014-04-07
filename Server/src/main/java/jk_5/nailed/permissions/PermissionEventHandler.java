package jk_5.nailed.permissions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.server.command.PermissionCommand;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class PermissionEventHandler {

    public static final String CHATNODE = "minecraft.chat";

    public PermissionEventHandler(){
        PermissionsManager.registerPermission(CHATNODE, RegisteredPermValue.TRUE);
    }

    @SubscribeEvent
    public void onCommand(CommandEvent event){
        if(event.command instanceof PermissionCommand){
            PermissionCommand command = (PermissionCommand) event.command;
            String node = command.getPermissionNode();
            if(node == null) return;
            if(event.sender instanceof EntityPlayer){
                EntityPlayer sender = (EntityPlayer) event.sender;
                if(!PermissionsManager.checkPerm(sender, node)){
                    event.setCanceled(true);
                    ChatComponentTranslation message = new ChatComponentTranslation("commands.generic.permission");
                    message.getChatStyle().setColor(EnumChatFormatting.RED);
                    event.sender.addChatMessage(message);
                }
            }else if(event.sender instanceof CommandBlockLogic){
                CommandBlockLogic sender = (CommandBlockLogic) event.sender;
                if(!PermissionsManager.getPerm("[CommandBlock]", node).check()){
                    event.setCanceled(true);
                    ChatComponentTranslation message = new ChatComponentTranslation("commands.generic.permission");
                    message.getChatStyle().setColor(EnumChatFormatting.RED);
                    event.sender.addChatMessage(message);
                }
            }
        }
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event){
        Player player = NailedAPI.getPlayerRegistry().getPlayer(event.player);
        if(player == null){
            event.setCanceled(true);
            IChatComponent message = new ChatComponentText("Your message has been cancelled because you don\'t exist on the server. Relog to fix this problem!");
            message.getChatStyle().setColor(EnumChatFormatting.RED);
            event.player.addChatMessage(message);
        }else if(!PermissionsManager.checkPerm(player.getEntity(), CHATNODE)){
            event.setCanceled(true);
            IChatComponent message = new ChatComponentText("You don\'t have the permission to talk");
            message.getChatStyle().setColor(EnumChatFormatting.RED);
            event.player.addChatMessage(message);
        }
    }
}
