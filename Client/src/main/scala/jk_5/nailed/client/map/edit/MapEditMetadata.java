package jk_5.nailed.client.map.edit;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import jk_5.nailed.map.Location;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapEditMetadata {

    public String name;
    public Location spawnPoint;
    public List<Location> randomSpawnpoints;

    public void readFrom(ByteBuf buffer){
        this.name = ByteBufUtils.readUTF8String(buffer);
        this.spawnPoint = Location.read(buffer);
        int length = buffer.readInt();
        this.randomSpawnpoints = Lists.newArrayListWithCapacity(length);
        for(int i = 0; i < length; i++){
            this.randomSpawnpoints.add(Location.read(buffer));
        }
    }

    public Location getSpawnpoint(double x, double y, double z){
        if(this.spawnPoint.getX() > x + 0.3 && this.spawnPoint.getX() < x + 0.7 && this.spawnPoint.getY() > y - 3 && this.spawnPoint.getY() < y && this.spawnPoint.getZ() > z + 0.3 && this.spawnPoint.getZ() < z + 0.7){
            return this.spawnPoint;
        }
        for(Location spawnpoint : this.randomSpawnpoints){
            if(spawnpoint.getX() > x + 0.3 && spawnpoint.getX() < x + 0.7 && spawnpoint.getY() > y - 3 && spawnpoint.getY() < y && spawnpoint.getZ() > z + 0.3 && spawnpoint.getZ() < z + 0.7){
                return spawnpoint;
            }
        }
        return null;
    }
}
