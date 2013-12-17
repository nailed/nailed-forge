package jk_5.nailed.server.command;

import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandStartGame extends CommandBase {

    @Override
    public String getCommandName() {
        return "startgame";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/startgame";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommand(ICommandSender iCommandSender, String[] strings) {
        Player player = PlayerRegistry.instance().getPlayer(iCommandSender.getCommandSenderName());
        if(player == null) return;
        player.getCurrentMap().getGameController().startGame();
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
