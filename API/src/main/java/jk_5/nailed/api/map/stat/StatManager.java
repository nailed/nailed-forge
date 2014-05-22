package jk_5.nailed.api.map.stat;

import java.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface StatManager {

    StatConfig getStats();
    List<IStatTileEntity> getStatTiles();

    Stat getStat(String name);
    void registerStatTile(IStatTileEntity tile);
    void unloadStatTile(IStatTileEntity tile);
    void onStatEnable(Stat stat);
    void onStatDisable(Stat stat);
}
