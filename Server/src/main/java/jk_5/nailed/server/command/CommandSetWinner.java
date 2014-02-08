package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.PossibleWinner;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSetWinner extends NailedCommand {

    public CommandSetWinner(){
        super("setwinner");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0) throw new WrongUsageException("/setwinner <winner>");
        PossibleWinner winner = map.getTeamManager().getTeam(args[0]);
        if(winner == null){
            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            if(player.getCurrentMap() != map) throw new CommandException("Player " + args[0] + " is not in this map");
        }
        if(winner == null) throw new CommandException(args[0] + " is not a player or team");

        map.getInstructionController().setWinner(map.getTeamManager().getTeam(args[0]));

        IChatComponent component = new ChatComponentText("Winner set to " + args[0]);
        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
        sender.addChatMessage(component);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args){
        if(args.length != 1) return Arrays.asList();
        Map map = NailedAPI.getMapLoader().getMap(sender.getEntityWorld());
        List<String> teams = Lists.newArrayList();
        for(Team team : map.getTeamManager().getTeams()){
            teams.add(team.getTeamId());
        }
        return CommandBase.getListOfStringsFromIterableMatchingLastWord(args, teams);
    }
}
