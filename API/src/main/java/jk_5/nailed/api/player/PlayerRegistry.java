package jk_5.nailed.api.player;

import java.util.*;

import com.mojang.authlib.*;

import net.minecraft.entity.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface PlayerRegistry {

    List<Player> getPlayers();
    List<Player> getOnlinePlayers();
    Player getPlayer(EntityPlayer player);
    Player getPlayerById(String id);
    Player getPlayerByUsername(String username);
    Player getOrCreatePlayer(GameProfile profile);
}
