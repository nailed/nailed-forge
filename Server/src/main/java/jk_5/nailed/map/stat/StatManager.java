package jk_5.nailed.map.stat;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.map.Map;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;

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
        MinecraftForge.EVENT_BUS.register(this);
    }

    public Stat getStat(String statName) {
        return this.stats.getStat(statName);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatTileLoad(StatTileEntityEvent.Load event){
        this.statTiles.add(event.tileEntity);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatTileUnload(StatTileEntityEvent.Unload event){
        this.statTiles.remove(event.tileEntity);
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatEnable(StatEvent.Enable event){
        for(IStatTileEntity tile : this.statTiles){
            if(tile.getStat() == event.stat) tile.enable();
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatDisable(StatEvent.Disable event){
        for(IStatTileEntity tile : this.statTiles){
            if(tile.getStat() == event.stat) tile.disable();
        }
    }
}
