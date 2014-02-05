package jk_5.nailed.api;

import jk_5.nailed.api.map.MapLoader;
import jk_5.nailed.api.map.MappackLoader;
import jk_5.nailed.api.player.PlayerRegistry;
import lombok.Getter;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedAPI {

    @Getter @Setter private static MapLoader mapLoader;
    @Getter @Setter private static MappackLoader mappackLoader;
    @Getter @Setter private static PlayerRegistry playerRegistry;
}
