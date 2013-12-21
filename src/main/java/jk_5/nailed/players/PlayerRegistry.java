package jk_5.nailed.players;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import jk_5.nailed.NailedLog;
import jk_5.nailed.event.*;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import lombok.Getter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class PlayerRegistry implements IPlayerTracker {

    private static PlayerRegistry INSTANCE = new PlayerRegistry();

    public static PlayerRegistry instance(){
        return INSTANCE;
    }

    @Getter
    private final List<Player> players = Lists.newArrayList();

    public PlayerRegistry() {
        GameRegistry.registerPlayerTracker(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Player getPlayer(String username){
        for(Player player : this.players){
            if(player.getUsername().equals(username)){
                return player;
            }
        }
        return null;
    }

    public Player getOrCreatePlayer(String username){
        Player p = this.getPlayer(username);
        if(p != null) return p;
        p = new Player(username);
        this.players.add(p);
        MinecraftForge.EVENT_BUS.post(new PlayerCreatedEvent(p.getEntity(), p));
        return p;
    }

    @SuppressWarnings("unused")
    @ForgeSubscribe
    public void formatPlayerName(PlayerEvent.NameFormat event){
        if(FMLCommonHandler.instance().getEffectiveSide().isClient()) return;
        Player player = this.getOrCreatePlayer(event.username);
        if(player == null) return;
        event.displayname = player.getChatPrefix();
    }

    @SuppressWarnings("unused")
    @ForgeSubscribe
    public void onPlayerChat(ServerChatEvent event){
        Player player = this.getOrCreatePlayer(event.username);
        if(player == null) return;
        MinecraftForge.EVENT_BUS.post(new PlayerChatEvent(player, event.message));
    }

    @ForgeSubscribe(priority = EventPriority.HIGHEST)
    @SuppressWarnings("unused")
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event){
        Map map = MapLoader.instance().getMap(event.player.getEntity().worldObj);
        if(map != null) event.player.setCurrentMap(map);
    }

    @Override
    public void onPlayerLogin(EntityPlayer ent){
        Player player = this.getOrCreatePlayer(ent.username);
        NailedLog.info("Player " + player.getUsername() + " logged in in world " + player.getCurrentMap().getSaveFileName());
        player.onLogin();
        MinecraftForge.EVENT_BUS.post(new PlayerJoinEvent(player));
        for(Player p : this.players){
            if(p == player) continue;
            p.sendNotification(player.getUsername() + " joined");
        }
    }

    @Override
    public void onPlayerLogout(EntityPlayer ent){
        Player player = this.getPlayer(ent.username);
        player.onLogout();
        MinecraftForge.EVENT_BUS.post(new PlayerLeaveEvent(player));
        for(Player p : this.players){
            if(p == player) continue;
            p.sendNotification(player.getUsername() + " left");
        }
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player){
        Player p = this.getPlayer(player.username);
        NailedLog.info("Player " + player.username + " changed dimension");
        NailedLog.info("   From: " + p.getCurrentMap().getSaveFileName());
        NailedLog.info("   To:   " + MapLoader.instance().getMap(player.worldObj).getSaveFileName());
        p.onChangedDimension();
        MinecraftForge.EVENT_BUS.post(new PlayerChangedDimensionEvent(p));
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player){
        Player p = this.getPlayer(player.username);
        NailedLog.info("Player " + p.getUsername() + " respawned");
        p.onRespawn();
    }
}
