package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.command.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTeam extends NailedCommand {

    public CommandTeam() {
        super("team");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            throw new WrongUsageException("/team join <username> <team>");
        }
        if("join".equalsIgnoreCase(args[0])){
            if(args.length == 1){
                throw new WrongUsageException("/team join <username> <team>");
            }
            Player player = NailedAPI.getPlayerRegistry().getPlayer(getTargetPlayer(sender, args[1]));
            if(player == null){
                throw new CommandException("Unknown username " + args[1]);
            }

            if(args.length == 2){
                throw new WrongUsageException("/team join " + args[1] + " <team>");
            }
            Team team = map.getTeamManager().getTeam(args[2]);
            if(team == null){
                throw new CommandException("Unknown team name " + args[2]);
            }

            if(args.length == 3){
                map.getTeamManager().setPlayerTeam(player, team);
                map.broadcastChatMessage(ChatColor.GREEN + "Player " + player.getUsername() + " is now in team " + team.getColoredName());
            }
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        Map map = NailedAPI.getMapLoader().getMap(sender.getEntityWorld());
        if(args.length == 1){
            return getOptions(args, "join");
        }else if(args.length == 2){
            if("join".equalsIgnoreCase(args[0])){
                return getUsernameOptions(args);
            }
        }else if(args.length == 3){
            if("join".equalsIgnoreCase(args[0])){
                List<String> teams = Lists.newArrayList();
                for(Team team : map.getTeamManager().getTeams()){
                    teams.add(team.getTeamId());
                }
                return getOptions(args, teams);
            }
        }
        return null;
    }
}
