package jk_5.nailed.coremod;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedModContainer extends DummyModContainer {

    @Override
    public boolean registerBus(EventBus bus, LoadController controller){
        return true;
    }
}
