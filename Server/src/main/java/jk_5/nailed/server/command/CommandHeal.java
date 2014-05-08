package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;

/**
 * Created by matthias on 5/8/14.
 *
 * heal command
 */
public class CommandHeal extends NailedCommand {
    public CommandHeal() {
        super("heal");
    }

    @Override
    public void processCommandPlayer(Player player, Map map, String[] args){
        if (map.getGameManager().isGameRunning()) return;
        player.getEntity().setHealth(20); // set health to 20 (10 hearts)
        player.getEntity().getFoodStats().setFoodLevel(20); // set food level to 20 (10 bars)
    }
}
