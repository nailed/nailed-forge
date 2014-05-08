package jk_5.nailed.players;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.events.PlayerJoinEvent;
import jk_5.nailed.api.events.PlayerLeaveEvent;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.player.PlayerRegistry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlayerRegistry implements PlayerRegistry {

    private final List<Player> players = Lists.newArrayList();

    @Override
    public Player getPlayer(EntityPlayer pl){
        return this.getPlayerById(pl.getGameProfile().getId());
    }

    @Override
    public Player getPlayerById(String id){
        for(Player player : this.players){
            if(player.getGameProfile().getId().equals(id)){
                return player;
            }
        }
        return null;
    }

    @Override
    public Player getPlayerByUsername(String username){
        for(Player player : this.players){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    public Player getOrCreatePlayer(EntityPlayer player){
        return this.getOrCreatePlayer(player.getGameProfile());
    }

    @Override
    public Player getOrCreatePlayer(GameProfile gameProfile){
        Player p = this.getPlayerByUsername(gameProfile.getName());
        if(p != null){
            return p;
        }
        p = new NailedPlayer(gameProfile);
        this.players.add(p);
        return p;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void formatPlayerName(net.minecraftforge.event.entity.player.PlayerEvent.NameFormat event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        Player player = this.getOrCreatePlayer(event.entityPlayer.getGameProfile());
        if(player == null) return;
        event.displayname = player.getChatPrefix();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = this.getOrCreatePlayer(event.player);
        NailedLog.info("Player {} logged in in world {}", player.getUsername(), player.getCurrentMap().getSaveFileName());
        player.onLogin();
        MinecraftForge.EVENT_BUS.post(new PlayerJoinEvent(player));
        for(Player p : this.players){
            if(p == player) continue;
            p.sendNotification(player.getUsername() + " joined");
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event){
        Player player = this.getPlayer(event.player);
        player.onLogout();
        MinecraftForge.EVENT_BUS.post(new PlayerLeaveEvent(player));
        for(Player p : this.players){
            if(p == player) continue;
            p.sendNotification(player.getUsername() + " left");
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        Player p = this.getPlayer(event.player);
        Map oldMap = p.getCurrentMap();
        Map newMap = NailedAPI.getMapLoader().getMap(event.player.worldObj);
        p.setCurrentMap(newMap);
        NailedLog.info("Player {} changed dimension", p.getUsername());
        NailedLog.info("   From: {}", oldMap.getSaveFileName());
        NailedLog.info("   To:   {}", newMap.getSaveFileName());
        p.onChangedDimension();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        Player p = this.getPlayer(event.player);
        NailedLog.info("Player {} respawned", p.getUsername());
        p.onRespawn();
    }

    @Override
    public List<Player> getOnlinePlayers(){
        List<Player> ret = Lists.newArrayList();
        for(Player player : this.players){
            if(player.isOnline()){
                ret.add(player);
            }
        }
        return ret;
    }

    @Override
    public List<Player> getPlayers() {
        return this.players;
    }


    @SubscribeEvent
    public void onPlayerHurt(LivingHurtEvent event){
        if (event.entity instanceof EntityPlayer){
            float ammount = event.ammount;
            Player player = this.getPlayer((EntityPlayer) event.entity);
            if (player.getEntity().getHealth() - ammount < player.getMinHealth()){
                player.getEntity().setHealth(player.getMinHealth());
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        for(Player player : this.getOnlinePlayers()){
            if( player.getEntity().getHealth() > player.getMaxHealth()){
                player.getEntity().setHealth(player.getMaxHealth());
            }
        }
    }
}
