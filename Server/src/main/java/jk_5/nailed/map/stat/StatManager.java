package jk_5.nailed.map.stat;

import java.util.*;

import com.google.common.base.*;
import com.google.common.collect.*;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.stat.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatManager implements jk_5.nailed.api.map.stat.StatManager {

    private final jk_5.nailed.api.map.stat.StatConfig stats;
    private final List<IStatTileEntity> statTiles = Lists.newArrayList();

    public StatManager(Map map) {
        if(map.getMappack() != null){
            Preconditions.checkNotNull(map.getMappack().getStatConfig(), "StatConfig may not be null!");
            this.stats = map.getMappack().getStatConfig().copy();
        }else{
            this.stats = new StatConfig();
        }
    }

    @Override
    public Stat getStat(String statName) {
        return this.stats.getStat(statName);
    }

    @Override
    public void registerStatTile(IStatTileEntity statTile) {
        this.statTiles.add(statTile);
    }

    @Override
    public void unloadStatTile(IStatTileEntity statTile) {
        this.statTiles.remove(statTile);
    }

    @Override
    public void onStatEnable(Stat stat) {
        for(IStatTileEntity tile : this.statTiles){
            if(tile.getStat() == stat){
                tile.enable();
            }
        }
    }

    @Override
    public void onStatDisable(Stat stat) {
        for(IStatTileEntity tile : this.statTiles){
            if(tile.getStat() == stat){
                tile.disable();
            }
        }
    }

    @Override
    public jk_5.nailed.api.map.stat.StatConfig getStats() {
        return this.stats;
    }

    @Override
    public List<IStatTileEntity> getStatTiles() {
        return this.statTiles;
    }
}
