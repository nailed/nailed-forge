package jk_5.nailed.players;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.IPlayerTracker;
import jk_5.nailed.NailedLog;
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

    @Override
    public void onPlayerLogin(EntityPlayer player){

    }

    @Override
    public void onPlayerLogout(EntityPlayer player){

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player){

    }

    @Override
    public void onPlayerRespawn(EntityPlayer player){

    }
}
