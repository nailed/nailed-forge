package jk_5.nailed.map.stat;

import com.google.common.collect.Maps;
import jk_5.nailed.map.stat.types.*;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatTypeManager {

    private static final StatTypeManager instance = new StatTypeManager();

    public static StatTypeManager instance(){
        return instance;
    }

    @Getter private Map<String, IStatType> statTypes = Maps.newHashMap();

    public StatTypeManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void addStatTypesFromEvent(RegisterStatTypeEvent event){
        event.register("gameloopRunning", new StatTypeGameloopRunning());
        event.register("gameloopStopped", new StatTypeGameloopStopped());
        event.register("gameloopPaused", new StatTypeGameloopPaused());
        event.register("modifiable", new StatTypeModifiable());
        event.register("iswinner", new StatTypeIsWinner());
        event.register("gameHasWinner", new StatTypeGameHasWinner());
    }

    public IStatType getStatType(String name){
        return this.statTypes.get(name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getStatType(Class<T> cl){
        for(IStatType type : this.statTypes.values()){
            if(type.getClass() == cl){
                return (T) type;
            }
        }
        return null;
    }
}
