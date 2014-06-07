package jk_5.nailed.players;

import java.util.*;

import com.google.common.collect.*;
import com.mojang.authlib.*;

import net.minecraft.entity.player.*;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.*;

import jk_5.nailed.*;
import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlayerRegistry implements PlayerRegistry {

    private final List<Player> players = Lists.newArrayList();

    @Override
    public Player getPlayer(EntityPlayer pl) {
        return this.getPlayerById(pl.getGameProfile().getId());
    }

    @Override
    public Player getPlayerById(String id) {
        for(Player player : this.players){
            if(player.getGameProfile().getId().equals(id)){
                return player;
            }
        }
        return null;
    }

    @Override
    public Player getPlayerByUsername(String username) {
        for(Player player : this.players){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    public Player getOrCreatePlayer(EntityPlayer player) {
        return this.getOrCreatePlayer(player.getGameProfile());
    }

    @Override
    public Player getOrCreatePlayer(GameProfile gameProfile) {
        Player p = this.getPlayerByUsername(gameProfile.getName());
        if(p != null){
            return p;
        }
        p = new NailedPlayer(gameProfile);
        this.players.add(p);
        return p;
    }

    @SubscribeEvent
    public void formatPlayerName(net.minecraftforge.event.entity.player.PlayerEvent.NameFormat event) {
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()){
            return;
        }
        Player player = this.getOrCreatePlayer(event.entityPlayer.getGameProfile());
        if(player == null){
            return;
        }
        event.displayname = player.getChatPrefix();
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = this.getOrCreatePlayer(event.player);
        NailedLog.info("Player {} logged in in world {}", player.getUsername(), player.getCurrentMap().getSaveFileName());
        player.onLogin();
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = this.getPlayer(event.player);
        player.onLogout();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
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
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player p = this.getPlayer(event.player);
        NailedLog.info("Player {} respawned", p.getUsername());
        p.onRespawn();
    }

    @Override
    public List<Player> getOnlinePlayers() {
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


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerHurt(LivingHurtEvent event) {
        if(event.entity instanceof EntityPlayer){
            float ammount = event.ammount;
            Player player = this.getPlayer((EntityPlayer) event.entity);
            if(event.source == DamageSource.outOfWorld) return;
            if(player.getEntity().getHealth() - ammount < player.getMinHealth()){
                event.ammount = player.getEntity().getHealth() - player.getMinHealth();
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onPlayerFall(LivingFallEvent event) {
        if(event.entity instanceof EntityPlayer){
            Player player = this.getPlayer((EntityPlayer) event.entity);
            float damageTaken = event.distance - 4;
            if(player.getCurrentMap().getMappack() != null){
                Mappack mappack = player.getCurrentMap().getMappack();
                if(mappack.getMappackMetadata().isFallDamageDisabled()){
                    damageTaken = 0;
                }
            }
            if(player.getEntity().getHealth() - damageTaken < player.getMinHealth()){
                event.setCanceled(true);
                player.getEntity().setHealth(player.getMinHealth());
            }
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.START){
            return;
        }
        for(Player player : this.getOnlinePlayers()){
            if(player.getEntity().getHealth() > player.getMaxHealth()){
                player.getEntity().setHealth(player.getMaxHealth());
            }
        }
    }
}
