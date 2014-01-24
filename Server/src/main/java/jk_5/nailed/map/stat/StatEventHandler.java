package jk_5.nailed.map.stat;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;

/**
 * No description given
 *
 * @author jk-5
 */
public class StatEventHandler {

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatEnable(StatEvent.Enable event){
        for(Map map : MapLoader.instance().getMaps()){
            map.getStatManager().onStatEnable(event.stat);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onStatDisable(StatEvent.Disable event){
        for(Map map : MapLoader.instance().getMaps()){
            map.getStatManager().onStatDisable(event.stat);
        }
    }
}
