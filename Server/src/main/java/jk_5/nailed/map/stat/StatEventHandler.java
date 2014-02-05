package jk_5.nailed.map.stat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatEventHandler {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatEnable(StatEvent.Enable event){
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            map.getStatManager().onStatEnable(event.stat);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatDisable(StatEvent.Disable event){
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            map.getStatManager().onStatDisable(event.stat);
        }
    }
}
