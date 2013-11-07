package jk_5.nailed.network;

import cpw.mods.fml.common.IPlayerTracker;
import jk_5.nailed.NailedLog;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlayerTracker implements IPlayerTracker {

    @Override
    public void onPlayerLogin(EntityPlayer player){

    }

    @Override
    public void onPlayerLogout(EntityPlayer player){

    }

    @Override
    public void onPlayerChangedDimension(EntityPlayer player){
        NailedLog.info("Player %s changed dimension", player.username);
    }

    @Override
    public void onPlayerRespawn(EntityPlayer player){

    }
}