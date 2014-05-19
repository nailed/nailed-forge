package jk_5.nailed.api;

import jk_5.nailed.api.camera.MovementHandler;
import jk_5.nailed.api.command.CommandRegistry;
import jk_5.nailed.api.concurrent.scheduler.Scheduler;
import jk_5.nailed.api.map.MapLoader;
import jk_5.nailed.api.map.MappackLoader;
import jk_5.nailed.api.map.teleport.Teleporter;
import jk_5.nailed.api.player.PlayerRegistry;
import jk_5.nailed.api.zone.ZoneRegistry;
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
    @Getter @Setter private static Scheduler scheduler;
    @Getter @Setter private static Teleporter teleporter;
    @Getter @Setter private static ZoneRegistry zoneRegistry;
    @Getter @Setter private static MovementHandler movementHandler;
    @Getter @Setter private static CommandRegistry commandRegistry;
}
