package jk_5.nailed.server.command;

import net.minecraft.command.*;

import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandStartGame extends NailedCommand {

    public CommandStartGame() {
        super("startgame");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        map.getGameManager().startGame();
    }
}
