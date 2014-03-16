package jk_5.nailed.client.map.edit;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.map.Spawnpoint;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapEditMetadata {

    public String name;
    public Spawnpoint spawnPoint;
    public List<Spawnpoint> randomSpawnpoints;

    public void readFrom(ByteBuf buffer){
        this.name = ByteBufUtils.readUTF8String(buffer);
        this.spawnPoint = Spawnpoint.read(buffer);
        int length = buffer.readInt();
        this.randomSpawnpoints = Lists.newArrayListWithCapacity(length);
        for(int i = 0; i < length; i++){
            this.randomSpawnpoints.add(Spawnpoint.read(buffer));
        }
    }

    public Spawnpoint getSpawnpoint(int x, int y, int z){
        if(this.spawnPoint.posX == x && this.spawnPoint.posY == y && this.spawnPoint.posZ == z){
            return this.spawnPoint;
        }
        for(Spawnpoint spawnpoint : this.randomSpawnpoints){
            if(spawnpoint.posX == x && spawnpoint.posY == y && spawnpoint.posZ == z){
                return spawnpoint;
            }
        }
        return null;
    }
}
