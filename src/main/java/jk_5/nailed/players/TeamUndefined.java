package jk_5.nailed.players;

import jk_5.nailed.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamUndefined extends Team {

    public TeamUndefined(Map map) {
        super(map, "undefined-" + map.getSaveFileName());
    }
}