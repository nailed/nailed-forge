package jk_5.nailed.map;

import com.google.common.base.Objects;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

/**
 * No description given
 *
 * @author jk-5
 * @deprecated Use {@link Location}
 */
@Deprecated
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

    @Override
    public String toString(){
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("yaw", yaw)
                .add("pitch", pitch)
                .toString();
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
