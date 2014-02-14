package jk_5.nailed.api.map;

import jk_5.nailed.api.config.ConfigTag;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class Spawnpoint extends ChunkCoordinates {

    public float yaw;
    public float pitch;

    public Spawnpoint(int x, int y, int z){
        super(x, y, z);
        this.yaw = 0f;
        this.pitch = 0f;
    }

    public Spawnpoint(int x, int y, int z, float yaw, float pitch){
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Spawnpoint(ChunkCoordinates par1ChunkCoordinates){
        super(par1ChunkCoordinates);
        this.yaw = 0f;
        this.pitch = 0f;
    }

    public Spawnpoint(ChunkCoordinates par1ChunkCoordinates, float yaw, float pitch){
        super(par1ChunkCoordinates);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Spawnpoint(Spawnpoint spawnpoint){
        super(spawnpoint);
        this.yaw = spawnpoint.yaw;
        this.pitch = spawnpoint.pitch;
    }

    public Spawnpoint(Entity dest){
        super((int) dest.posX, (int) dest.posY, (int) dest.posZ);
        this.yaw = dest.rotationYaw;
        this.pitch = dest.rotationPitch;
    }

    @Override
    public int compareTo(Object o){
        return this.compareTo((ChunkCoordinates) o);
    }

    public static Spawnpoint readFromConfig(ConfigTag tag){
        return new Spawnpoint(tag.getTag("x").getIntValue(), tag.getTag("y").getIntValue(64), tag.getTag("z").getIntValue(), tag.getTag("yaw").getIntValue(0), tag.getTag("pitch").getIntValue(0));
    }

    public TeleportOptions teleport(){
        TeleportOptions options = new TeleportOptions();
        options.setCoordinates(this);
        return options;
    }

    public void writeToConfig(ConfigTag tag){
        tag.getTag("x").setIntValue(this.posX);
        tag.getTag("y").setIntValue(this.posY);
        tag.getTag("z").setIntValue(this.posZ);
        tag.getTag("yaw").setIntValue((int) this.yaw);
        tag.getTag("pitch").setIntValue((int) this.pitch);
    }
}
