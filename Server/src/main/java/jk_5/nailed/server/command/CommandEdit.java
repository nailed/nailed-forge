package jk_5.nailed.server.command;

import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandEdit extends NailedCommand {

    public CommandEdit() {
        super("edit");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        sender.setEditModeEnabled(!sender.isEditModeEnabled());
    }
}
