package jk_5.nailed.map.stat;

import com.google.common.collect.Lists;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.util.config.ConfigFile;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
public class StatConfig implements jk_5.nailed.api.map.stat.StatConfig {

    @Getter
    private List<Stat> stats = Lists.newArrayList();

    public StatConfig(ConfigFile file){
        for(ConfigTag tag : file.getSortedTagList()){
            tag.useBraces();
            DefaultStat stat = new DefaultStat(tag.name);
            ConfigTag typeTag = tag.getTag("type");
            if(typeTag != null){
                IStatType type = StatTypeManager.instance().getStatType(typeTag.getValue());
                if(type == null){
                    NailedLog.warn("Unknown stat type " + typeTag.getValue());
                    continue;
                }
                stat.setType(type);
                stat.getType().readAdditionalData(typeTag, stat);
            }
            this.stats.add(stat);
        }
    }

    public StatConfig clone(){
        StatConfig config = new StatConfig();
        for(Stat stat : this.stats){
            config.stats.add(stat.clone());
        }
        return config;
    }

    public Stat getStat(String name) {
        for(Stat stat : this.stats){
            if(stat.getName().equals(name)){
                return stat;
            }
        }
        return null;
    }
}
