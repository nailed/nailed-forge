package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.command.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.team.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSetWinner extends NailedCommand {

    public CommandSetWinner() {
        super("setwinner");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            throw new WrongUsageException("/setwinner <winner>");
        }
        PossibleWinner winner = map.getTeamManager().getTeam(args[0]);
        if(winner == null){
            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            if(player.getCurrentMap() != map){
                throw new CommandException("Player " + args[0] + " is not in this map");
            }
        }
        if(winner == null){
            throw new CommandException(args[0] + " is not a player nor team");
        }

        map.getGameManager().setWinner(map.getTeamManager().getTeam(args[0]));

        IChatComponent component = new ChatComponentText("Winner set to " + args[0]);
        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
        sender.addChatMessage(component);
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length != 1){
            return Arrays.asList();
        }
        Map map = NailedAPI.getMapLoader().getMap(sender.getEntityWorld());
        List<String> suggestions = Lists.newArrayList();
        for(Team team : map.getTeamManager().getTeams()){
            suggestions.add(team.getTeamId());
        }
        for(Player player : map.getPlayers()){
            suggestions.add(player.getUsername());
        }
        return getOptions(args, suggestions);
    }
}
