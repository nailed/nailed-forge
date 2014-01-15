package jk_5.nailed.map.mappack;

import jk_5.nailed.common.util.config.ConfigTag;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class Spawnpoint extends ChunkCoordinates {

    public float yaw;
    public float pitch;

    public Spawnpoint(int x, int y, int z, float yaw, float pitch){
        super(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
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

    @Override
    public int compareTo(Object o){
        return this.compareTo((ChunkCoordinates) o);
    }

    public static Spawnpoint readFromConfig(ConfigTag tag){
        return new Spawnpoint(tag.getTag("x").getIntValue(), tag.getTag("y").getIntValue(64), tag.getTag("z").getIntValue(), tag.getTag("yaw").getIntValue(0), tag.getTag("pitch").getIntValue(0));
    }

    public void writeToConfig(ConfigTag tag){
        tag.getTag("x").setIntValue(this.posX);
        tag.getTag("y").setIntValue(this.posY);
        tag.getTag("z").setIntValue(this.posZ);
        tag.getTag("yaw").setIntValue((int) this.yaw);
        tag.getTag("pitch").setIntValue((int) this.pitch);
    }
}
