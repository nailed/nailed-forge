package jk_5.nailed.api.map;

import net.minecraft.world.World;

import java.io.File;
import java.util.List;
import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public interface MapLoader {

    public File getMapsFolder();
    public Random getRandomSpawnpointSelector();
    public Map getLobby();
    public List<Map> getMaps();
    public void registerMap(Map map);
    public Map createMapServer(Mappack mappack);
    public Map getMap(String name);
    public Map getMap(int id);
    public Map getMap(World world);
    public void removeMap(Map map);
}
