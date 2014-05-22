package jk_5.nailed.map.stat;

import cpw.mods.fml.common.eventhandler.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatEventHandler {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatEnable(StatEvent.Enable event) {
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            map.getStatManager().onStatEnable(event.stat);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatDisable(StatEvent.Disable event) {
        for(Map map : NailedAPI.getMapLoader().getMaps()){
            map.getStatManager().onStatDisable(event.stat);
        }
    }
}
