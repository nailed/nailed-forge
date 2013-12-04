package jk_5.nailed.server.command;

import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.players.Team;
import jk_5.nailed.util.ChatColor;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTeam extends CommandBase {

    @Override
    public String getCommandName() {
        return "team";
    }

    @Override
    public String getCommandUsage(ICommandSender iCommandSender) {
        return "/team set <username> <team>";
    }

    @Override
    public void processCommand(ICommandSender s, String[] strings) {
        if(!(s instanceof EntityPlayer)) throw new CommandException("This command can only be used by players");
        Player sender = PlayerRegistry.instance().getPlayer(((EntityPlayer) s).username);
        if(strings.length == 0) throw new WrongUsageException("/team set <username> <team>");
        if(strings[0].equalsIgnoreCase("set")){
            if(strings.length == 1) throw new WrongUsageException("/team set <username> <team>");
            Player player = PlayerRegistry.instance().getPlayer(strings[1]);
            if(player == null) throw new CommandException("Unknown username " + strings[1]);

            if(strings.length == 2) throw new WrongUsageException("/team set " + strings[1] + " <team>");
            Team team = sender.getCurrentMap().getTeamManager().getTeam(strings[1]);
            if(team == null) throw new CommandException("Unknown team name " + strings[1]);

            if(strings.length == 3){
                sender.getCurrentMap().getTeamManager().setPlayerTeam(player, team);
                sender.getCurrentMap().broadcastChatMessage(ChatColor.GREEN + "Player " + player.getUsername() + " is now in team " + team.getColoredName());
            }
        }
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
