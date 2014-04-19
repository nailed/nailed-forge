package jk_5.nailed.ipc;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.events.PlayerJoinEvent;
import jk_5.nailed.api.events.PlayerLeaveEvent;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.ipc.packet.PacketKill;
import jk_5.nailed.ipc.packet.PacketPlayerDeath;
import jk_5.nailed.ipc.packet.PacketPlayerJoin;
import jk_5.nailed.ipc.packet.PacketPlayerLeave;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntityDamageSource;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 * No description given
 *
 * @author jk-5
 */
@SuppressWarnings("unused")
public class IpcEventListener {

    @SubscribeEvent
    public void onPlayerJoin(PlayerJoinEvent event){
        IpcManager.instance().sendPacket(new PacketPlayerJoin(event.player));
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.DisplayLogin(), event.player.getEntity());
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
}
