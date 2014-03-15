package jk_5.nailed.map;

import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.util.config.ConfigTag;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 */
public class Spawnpoint extends ChunkCoordinates {

    public String name;
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
        Spawnpoint spawn = new Spawnpoint(tag.getTag("x").getIntValue(), tag.getTag("y").getIntValue(64), tag.getTag("z").getIntValue(), tag.getTag("yaw").getIntValue(0), tag.getTag("pitch").getIntValue(0));
        spawn.name = tag.name;
        return spawn;
    }

    public void writeToConfig(ConfigTag tag){
        tag.name = this.name;
        tag.getTag("x").setIntValue(this.posX);
        tag.getTag("y").setIntValue(this.posY);
        tag.getTag("z").setIntValue(this.posZ);
        tag.getTag("yaw").setIntValue((int) this.yaw);
        tag.getTag("pitch").setIntValue((int) this.pitch);
    }

    @Override
    public String toString(){
        final StringBuffer sb = new StringBuffer("Spawnpoint{");
        sb.append("x=").append(posX);
        sb.append(", y=").append(posY);
        sb.append(", z=").append(posZ);
        sb.append(", pitch=").append(pitch);
        sb.append(", yaw=").append(yaw);
        sb.append(", name=").append(name);
        sb.append('}');
        return sb.toString();
    }

    public void write(ByteBuf buffer){
        buffer.writeInt(this.posX);
        buffer.writeInt(this.posY);
        buffer.writeInt(this.posZ);
        buffer.writeFloat(this.yaw);
        buffer.writeFloat(this.pitch);
        ByteBufUtils.writeUTF8String(buffer, this.name);
    }

    public static Spawnpoint read(ByteBuf buffer){
        Spawnpoint point = new Spawnpoint(buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readFloat(), buffer.readFloat());
        point.name = ByteBufUtils.readUTF8String(buffer);
        return point;
    }
}
