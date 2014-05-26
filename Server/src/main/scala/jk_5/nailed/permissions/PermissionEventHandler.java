package jk_5.nailed.permissions;

import net.minecraft.util.*;

import cpw.mods.fml.common.eventhandler.*;

import net.minecraftforge.event.*;
import net.minecraftforge.permissions.api.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class PermissionEventHandler {

    public static final String CHATNODE = "minecraft.chat";

    public PermissionEventHandler() {
        PermissionsManager.registerPermission(CHATNODE, RegisteredPermValue.TRUE);
    }

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
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
