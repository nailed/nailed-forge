package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.players.Team;
import jk_5.nailed.util.ChatColor;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTeam extends NailedCommand {

    @Override
    public String getCommandName() {
        return "team";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0) throw new WrongUsageException("/team join <username> <team>");
        if(args[0].equalsIgnoreCase("join")){
            if(args.length == 1) throw new WrongUsageException("/team join <username> <team>");
            Player player = PlayerRegistry.instance().getPlayerByUsername(args[1]);
            if(player == null) throw new CommandException("Unknown username " + args[1]);

            if(args.length == 2) throw new WrongUsageException("/team join " + args[1] + " <team>");
            Team team = map.getTeamManager().getTeam(args[2]);
            if(team == null) throw new CommandException("Unknown team name " + args[2]);

            if(args.length == 3){
                map.getTeamManager().setPlayerTeam(player, team);
                map.broadcastChatMessage(ChatColor.GREEN + "Player " + player.getUsername() + " is now in team " + team.getColoredName());
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings){
        Map map = MapLoader.instance().getMap(iCommandSender.getEntityWorld());
        if(strings.length == 1) return getListOfStringsMatchingLastWord(strings, "join");
        else if(strings.length == 2){
            if(strings[0].equalsIgnoreCase("join")){
                return getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
            }
        }else if(strings.length == 3){
            if(strings[0].equalsIgnoreCase("join")){
                List<String> teams = Lists.newArrayList();
                for(Team team : map.getTeamManager().getTeams()){
                    teams.add(team.getTeamId());
                }
                return getListOfStringsFromIterableMatchingLastWord(strings, teams);
            }
        }
        return Arrays.asList();
    }
}
