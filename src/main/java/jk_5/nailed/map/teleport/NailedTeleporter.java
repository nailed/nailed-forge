package jk_5.nailed.map.teleport;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedTeleporter extends Teleporter {

    public NailedTeleporter(WorldServer par1WorldServer){
        super(par1WorldServer);
    }

    public void removeStalePortalLocations(long l1){

    }

    public boolean makePortal(Entity par1Entity){
        return true;
    }

    public void placeInPortal(Entity par1Entity, double par2, double par4, double par6, float par8){
        par1Entity.setLocationAndAngles(par2, par4, par6, par1Entity.rotationYaw, 0.0F);
    }
}
