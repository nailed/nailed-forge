package jk_5.nailed.api;

import jk_5.nailed.api.camera.*;
import jk_5.nailed.api.command.*;
import jk_5.nailed.api.concurrent.scheduler.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.api.zone.*;

import lombok.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedAPI {

    @Getter
    @Setter
    private static MapLoader mapLoader;
    @Getter
    @Setter
    private static MappackLoader mappackLoader;
    @Getter
    @Setter
    private static PlayerRegistry playerRegistry;
    @Getter
    @Setter
    private static Scheduler scheduler;
    @Getter
    @Setter
    private static Teleporter teleporter;
    @Getter
    @Setter
    private static ZoneRegistry zoneRegistry;
    @Getter
    @Setter
    private static MovementHandler movementHandler;
    @Getter
    @Setter
    private static CommandRegistry commandRegistry;
}
