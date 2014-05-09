package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;

/**
 * Created by matthias on 9-5-14.
 *
 * SuperEditMode for editing zoned places
 */
public class CommandSuperEdit extends NailedCommand {
    public CommandSuperEdit(){
        super("SuperEdit");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        if (sender.isOp()) {
            sender.setEditModeEnabled(!sender.isEditModeEnabled());
            sender.setSuperEditModeEnabled(!sender.isEditModeEnabled());
        }
    }
}
