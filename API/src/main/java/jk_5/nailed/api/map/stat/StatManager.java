package jk_5.nailed.api.map.stat;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public interface StatManager {

    public StatConfig getStats();
    public List<IStatTileEntity> getStatTiles();

    public Stat getStat(String name);
    public void registerStatTile(IStatTileEntity tile);
    public void unloadStatTile(IStatTileEntity tile);
    public void onStatEnable(Stat stat);
    public void onStatDisable(Stat stat);
}
