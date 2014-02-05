package jk_5.nailed.players;

import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamUndefined extends NailedTeam {

    public TeamUndefined(Map map) {
        super(map, "undefined-" + map.getSaveFileName());
    }

    @Override
    public void onWorldSet(){
        //NOOP
    }
}
