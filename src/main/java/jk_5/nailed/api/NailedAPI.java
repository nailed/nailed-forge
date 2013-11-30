package jk_5.nailed.api;

import lombok.Getter;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedAPI {

    /**
     * If you want to register an custom mappack, use this
     */
    @Getter
    @Setter
    private static IMappackRegistrar mappackRegistrar;


}
