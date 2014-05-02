package jk_5.nailed.permissions;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
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
