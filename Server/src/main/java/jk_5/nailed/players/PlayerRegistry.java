package jk_5.nailed.players;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import jk_5.nailed.NailedLog;
import jk_5.nailed.event.*;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class PlayerRegistry {

    private static PlayerRegistry INSTANCE = new PlayerRegistry();

    public static PlayerRegistry instance(){
        return INSTANCE;
    }

    @Getter
    private final List<Player> players = Lists.newArrayList();

    public PlayerRegistry() {
        FMLCommonHandler.instance().bus().register(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Player getPlayer(EntityPlayer pl){
        return this.getPlayerById(pl.func_146103_bH().getId());
    }

    public Player getPlayerById(String id){
        for(Player player : this.players){
            if(player.getGameProfile().getId().equals(id)){
                return player;
            }
        }
        return null;
    }

    public Player getPlayerByUsername(String username){
        for(Player player : this.players){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    public Player getOrCreatePlayer(EntityPlayer player){
        return this.getOrCreatePlayer(player.func_146103_bH());
    }

    public Player getOrCreatePlayer(GameProfile gameProfile){
        Player p = this.getPlayerByUsername(gameProfile.getName());
        if(p != null) return p;
        p = new Player(gameProfile);
        this.players.add(p);
        MinecraftForge.EVENT_BUS.post(new PlayerCreatedEvent(p.getEntity(), p));
        return p;
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void formatPlayerName(net.minecraftforge.event.entity.player.PlayerEvent.NameFormat event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        Player player = this.getOrCreatePlayer(event.entityPlayer.func_146103_bH());
        if(player == null) return;
        event.displayname = player.getChatPrefix();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event){
        Player player = this.getOrCreatePlayer(event.player.func_146103_bH());
        if(player == null) return;
        MinecraftForge.EVENT_BUS.post(new PlayerChatEvent(player, event.message));
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event){
        Map map = MapLoader.instance().getMap(event.player.getEntity().worldObj);
        if(map != null) event.player.setCurrentMap(map);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = this.getOrCreatePlayer(event.player);
        NailedLog.info("Player " + player.getUsername() + " logged in in world " + player.getCurrentMap().getSaveFileName());
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

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event){
        Player p = this.getPlayer(event.player);
        Map oldMap = p.getCurrentMap();
        Map newMap = MapLoader.instance().getMap(event.player.worldObj);
        NailedLog.info("Player " + p.getUsername() + " changed dimension");
        NailedLog.info("   From: " + oldMap.getSaveFileName());
        NailedLog.info("   To:   " + newMap.getSaveFileName());
        p.onChangedDimension();
        MinecraftForge.EVENT_BUS.post(new PlayerChangedDimensionEvent(p, oldMap, newMap));
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        Player p = this.getPlayer(event.player);
        NailedLog.info("Player " + p.getUsername() + " respawned");
        p.onRespawn();
    }
}
