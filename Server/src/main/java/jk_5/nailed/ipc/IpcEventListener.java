package jk_5.nailed.ipc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.events.PlayerJoinEvent;
import jk_5.nailed.api.events.PlayerLeaveEvent;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.packet.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

import java.net.InetSocketAddress;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class IpcEventListener {

    @SubscribeEvent
    public void onPlayerJoin(PlayerJoinEvent event){
        IpcManager.instance().sendPacket(new PacketPlayerJoin(event.player, ((InetSocketAddress) event.player.getNetHandler().netManager.channel().remoteAddress()).getAddress().getHostAddress()));
    }

    @SubscribeEvent
    public void onPlayerJoin(PlayerLeaveEvent event){
        IpcManager.instance().sendPacket(new PacketPlayerLeave(event.player));
    }

    @SubscribeEvent
    public void onPlayerKill(LivingDeathEvent event){
        if(event.entity instanceof EntityPlayer){
            Player victim = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) event.entity);
            if(victim == null) return;
            if(event.source instanceof EntityDamageSource){
                EntityDamageSource damageSource = (EntityDamageSource) event.source;
                if(event.source.getEntity() instanceof EntityPlayer){
                    Player killer = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) damageSource.getEntity());
                    if(killer == null) return;
                    IpcManager.instance().sendPacket(new PacketKill(killer, victim));
                    return;
                }
            }
            IpcManager.instance().sendPacket(new PacketPlayerDeath(victim, event.source.damageType));
        }
    }

    public static void loginPlayer(EntityPlayer player, String username, String password){
        Player p = NailedAPI.getPlayerRegistry().getPlayer(player);
        if(IpcManager.instance().isConnected()){
            IpcManager.instance().sendPacket(new PacketLoginPlayer(p, username, password));
        }
    }
}
