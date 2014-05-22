package jk_5.nailed.ipc;

import java.net.*;

import net.minecraft.entity.player.*;
import net.minecraft.util.*;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

import net.minecraftforge.event.entity.living.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.ipc.packet.*;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class IpcEventListener {

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        IpcManager.instance().sendPacket(new PacketPlayerJoin(event.player.getGameProfile(), ((InetSocketAddress) ((EntityPlayerMP) event.player).playerNetServerHandler.netManager.channel().remoteAddress()).getAddress().getHostAddress()));
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedOutEvent event) {
        IpcManager.instance().sendPacket(new PacketPlayerLeave(event.player.getGameProfile()));
    }

    @SubscribeEvent
    public void onPlayerKill(LivingDeathEvent event) {
        if(event.entity instanceof EntityPlayer){
            Player victim = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.entity);
            if(victim == null){
                return;
            }
            if(event.source instanceof EntityDamageSource){
                EntityDamageSource damageSource = (EntityDamageSource) event.source;
                if(event.source.getEntity() instanceof EntityPlayer){
                    Player killer = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) damageSource.getEntity());
                    if(killer == null){
                        return;
                    }
                    IpcManager.instance().sendPacket(new PacketKill(killer, victim));
                    return;
                }
            }
            IpcManager.instance().sendPacket(new PacketPlayerDeath(victim, event.source.damageType));
        }
    }

    public static void loginPlayer(EntityPlayer player, String username, String password) {
        if("§uuidauth".equals(password)){
            Player p = NailedAPI.getPlayerRegistry().getPlayerById(username);
        }
        Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
        if(IpcManager.instance().isConnected()){
            IpcManager.instance().sendPacket(new PacketLoginPlayer(p, username, password));
        }
    }
}
