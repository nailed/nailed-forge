package jk_5.nailed.map.scoreboard;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.Objective;
import lombok.Getter;

/**
 * No description given
 *
 * @author jk-5
 */
public class ObjectiveImpl implements Objective {

    @Getter private final Map map;
    private final String id;
    @Getter private String displayName;

    public ObjectiveImpl(Map map, String id){
        this.map = map;
        this.id = id;
        this.displayName = id;
    }

    @Override
    public String getID(){
        return this.map.getID() + "_" + this.id;
    }
}
