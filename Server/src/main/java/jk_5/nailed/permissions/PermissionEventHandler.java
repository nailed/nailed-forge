package jk_5.nailed.permissions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.server.command.PermissionCommand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.permissions.api.PermissionsManager;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class PermissionEventHandler {

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
            }
        }
    }
}
