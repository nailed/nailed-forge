package jk_5.nailed.network;

import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkModHandler;
import jk_5.nailed.coremod.NailedModContainer;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedNetworkHandler extends NetworkModHandler {

    public NailedNetworkHandler(NailedModContainer container){
        super(container, NailedModContainer.class.getAnnotation(NetworkMod.class));
    }
}
