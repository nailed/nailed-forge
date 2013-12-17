package jk_5.nailed.map.teleport;

import jk_5.nailed.map.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedTeleporter extends Teleporter {

    private final Map map;

    public NailedTeleporter(Map map){
        super((WorldServer) map.getWorld());
        this.map = map;
    }

    @Override
    public void removeStalePortalLocations(long l1){

    }

    @Override
    public boolean makePortal(Entity par1Entity){
        return true;
    }

    @Override
    public void placeInPortal(Entity entity, double x, double y, double z, float par8){
        TeleportOptions opts = this.map.getSpawnTeleport();
        ChunkCoordinates coords = opts.getCoordinates();
        entity.setLocationAndAngles(coords.posX + 0.5D, coords.posY + 0.5D, coords.posZ + 0.5D, opts.getYaw(), 0);
        entity.motionX = entity.motionY = entity.motionZ = 0.0D;
    }
}
