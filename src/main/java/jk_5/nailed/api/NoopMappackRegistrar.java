package jk_5.nailed.api;

import jk_5.nailed.map.mappack.Mappack;

/**
 * No description given
 *
 * @author jk-5
 */
public class NoopMappackRegistrar implements IMappackRegistrar {

    @Override
    public void registerMappack(Mappack mappack) {
        //NOOP
    }
}
