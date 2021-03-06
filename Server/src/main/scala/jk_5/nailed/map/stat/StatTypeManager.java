package jk_5.nailed.map.stat;

import java.util.Map;

import com.google.common.collect.Maps;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

import net.minecraftforge.common.MinecraftForge;

import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.map.stat.types.StatTypeGameHasWinner;
import jk_5.nailed.map.stat.types.StatTypeGameloopPaused;
import jk_5.nailed.map.stat.types.StatTypeGameloopRunning;
import jk_5.nailed.map.stat.types.StatTypeGameloopStopped;
import jk_5.nailed.map.stat.types.StatTypeIsWinner;
import jk_5.nailed.map.stat.types.StatTypeModifiable;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeManager {

    private static final StatTypeManager instance = new StatTypeManager();

    private Map<String, IStatType> statTypes = Maps.newHashMap();

    public StatTypeManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static StatTypeManager instance() {
        return instance;
    }

    @SubscribeEvent
    public void addStatTypesFromEvent(RegisterStatTypeEvent event) {
        event.register("gameloopRunning", new StatTypeGameloopRunning());
        event.register("gameloopStopped", new StatTypeGameloopStopped());
        event.register("gameloopPaused", new StatTypeGameloopPaused());
        event.register("modifiable", new StatTypeModifiable());
        event.register("iswinner", new StatTypeIsWinner());
        event.register("gameHasWinner", new StatTypeGameHasWinner());
    }

    public IStatType getStatType(String name) {
        return this.statTypes.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getStatType(Class<T> cl) {
        for(IStatType type : this.statTypes.values()){
            if(type.getClass() == cl){
                return (T) type;
            }
        }
        return null;
    }

    public Map<String, IStatType> getStatTypes() {
        return this.statTypes;
    }
}
