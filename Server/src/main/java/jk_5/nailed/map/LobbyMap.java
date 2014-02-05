package jk_5.nailed.map;

import jk_5.nailed.api.NailedAPI;

/**
 * Map for the normal overworld, used for the lobby
 * This uses a fixed dimension id of 0. Only use this for dimension 0!
 *
 * @author jk-5
 */
public class LobbyMap extends NailedMap {

    public LobbyMap(){
        super(NailedAPI.getMappackLoader().getMappack("lobby"), 0);
        if(this.getMappack() != null){
            this.getMappack().prepareWorld(this.getSaveFolder());
        }
    }
}
