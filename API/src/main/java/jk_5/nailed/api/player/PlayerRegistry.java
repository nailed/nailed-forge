package jk_5.nailed.api.player;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface PlayerRegistry {

    public List<Player> getPlayers();
    public Player getPlayer(EntityPlayer player);
    public Player getPlayerById(String id);
    public Player getPlayerByUsername(String username);
    public Player getOrCreatePlayer(GameProfile profile);
}
