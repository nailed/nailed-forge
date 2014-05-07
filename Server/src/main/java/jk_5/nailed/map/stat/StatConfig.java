package jk_5.nailed.map.stat;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatConfig implements jk_5.nailed.api.map.stat.StatConfig {

    private List<Stat> stats = Lists.newArrayList();

    public StatConfig(JsonArray json) {
        for(JsonElement element : json){
            JsonObject obj = element.getAsJsonObject();
            DefaultStat stat = new DefaultStat(obj.get("name").getAsString());
            if(obj.has("type")){
                String t = obj.get("type").getAsString();
                IStatType type = StatTypeManager.instance().getStatType(t);
                if(type == null){
                    NailedLog.warn("Unknown stat type {}", t);
                    continue;
                }
                stat.setType(type);
                type.readAdditionalData(obj, stat);
            }
            this.stats.add(stat);
        }
    }

    public StatConfig() {
    }

    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportedException")
    public StatConfig clone() {
        StatConfig config = new StatConfig();
        for(Stat stat : this.stats){
            config.stats.add(stat.clone());
        }
        return config;
    }

    @Override
    public Stat getStat(String name) {
        for(Stat stat : this.stats){
            if(stat.getName().equals(name)){
                return stat;
            }
        }
        return null;
    }

    @Override
    public List<Stat> getStats() {
        return this.stats;
    }
}
