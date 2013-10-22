package jk_5.nailed.network;

import cpw.mods.fml.common.IPlayerTracker;
import jk_5.nailed.NailedLog;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlayerTracker implements IPlayerTracker {

    public void onPlayerLogin(EntityPlayer player){

    }

    public void onPlayerLogout(EntityPlayer player){

    }

    public void onPlayerChangedDimension(EntityPlayer player){
        NailedLog.info("Player %s changed dimension", player.username);
    }

    public void onPlayerRespawn(EntityPlayer player){

    }
}
