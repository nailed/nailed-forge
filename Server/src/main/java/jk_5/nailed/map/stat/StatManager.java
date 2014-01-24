package jk_5.nailed.map.stat;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import lombok.Getter;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatManager {

    private final Map map;
    @Getter private final StatConfig stats;
    @Getter private final List<IStatTileEntity> statTiles = Lists.newArrayList();

    public StatManager(Map map){
        this.map = map;
        if(map.getMappack() != null){
            Preconditions.checkNotNull(map.getMappack().getStatConfig(), "StatConfig may not be null!");
            this.stats = this.map.getMappack().getStatConfig().clone();
        }else{
            this.stats = new StatConfig();
        }
    }

    public Stat getStat(String statName) {
        return this.stats.getStat(statName);
    }

    public void registerStatTile(IStatTileEntity statTile){
        this.statTiles.add(statTile);
    }

    public void unloadStatTile(IStatTileEntity statTile){
        this.statTiles.remove(statTile);
    }

    public void onStatEnable(Stat stat){
        for(IStatTileEntity tile : this.statTiles){
            if(tile.getStat() == stat) tile.enable();
        }
    }

    public void onStatDisable(Stat stat){
        for(IStatTileEntity tile : this.statTiles){
            if(tile.getStat() == stat) tile.disable();
        }
    }
}
