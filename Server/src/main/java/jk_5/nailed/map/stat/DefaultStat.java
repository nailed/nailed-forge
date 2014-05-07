package jk_5.nailed.map.stat;

import com.google.common.collect.Maps;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import net.minecraftforge.common.MinecraftForge;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultStat implements Stat {

    private final String name;
    private IStatType type;
    private boolean enabled = false;
    private Map<String, Object> storage = Maps.newHashMap();

    public DefaultStat(String name) {
        this.name = name;
    }

    public DefaultStat(String name, IStatType type, boolean enabled, Map<String, Object> storage) {
        this.name = name;
        this.type = type;
        this.enabled = enabled;
        this.storage = storage;
    }

    @Override
    public void setDefaultState(boolean defaultState){
        this.enabled = defaultState;
    }

    @Override
    public void enable() {
        this.enabled = true;
        MinecraftForge.EVENT_BUS.post(new StatEvent.Enable(this));
    }

    @Override
    public void disable() {
        this.enabled = false;
        MinecraftForge.EVENT_BUS.post(new StatEvent.Disable(this));
    }

    @Override
    public Stat clone() {
        return new DefaultStat(this.name, this.type, this.enabled, this.storage);
    }

    @Override
    public void store(String key, Object value){
        this.storage.put(key, value);
    }

    @Override
    public Object load(String key){
        return this.storage.get(key);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public IStatType getType() {
        return this.type;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public void setType(IStatType type) {
        this.type = type;
    }
}
