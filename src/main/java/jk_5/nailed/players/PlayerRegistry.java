package jk_5.nailed.players;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.entity.player.EntityPlayer;

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

    private final List<Player> players = Lists.newArrayList();

    public PlayerRegistry() {
        GameRegistry.registerPlayerTracker(this);
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
        return p;
    }

    @Override
    public void onPlayerLogin(EntityPlayer ent){
        Player player = this.getOrCreatePlayer(ent.username);
        for(Player p : this.players){
            if(p == player) continue;
            p.sendNotification(player.getUsername() + " joined");
        }
    }

    @Override
    public void onPlayerLogout(EntityPlayer ent){
        Player player = this.getPlayer(ent.username);
        for(Player p : this.players){
            if(p == player) continue;
            p.sendNotification(player.getUsername() + " left");
        }
    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player){

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player){

    }
}
