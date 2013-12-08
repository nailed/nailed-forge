package jk_5.nailed.map.stat;

import com.google.common.collect.Lists;
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
public class StatConfig {

    @Getter
    private List<Stat> stats = Lists.newArrayList();

    public StatConfig(ConfigFile file){
        for(ConfigTag tag : file.getSortedTagList()){
            tag.useBraces();
            System.out.println(tag.name);
            DefaultStat stat = new DefaultStat(tag.name);
            ConfigTag typeTag = tag.getTag("type");
            if(typeTag != null){
                System.out.println(typeTag.getValue());
                stat.setType(StatTypeManager.instance().getStatType(typeTag.getValue()));
                stat.getType().readAdditionalData(typeTag);
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
