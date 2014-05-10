package jk_5.nailed.server.command;

import com.google.common.base.Joiner;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.zone.IZone;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandZone extends NailedCommand {

    public CommandZone() {
        super("zone");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        Set<IZone> zones = map.getZoneManager().getZones(sender);
        sender.sendChat(Joiner.on(", ").join(zones));
    }
}