package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Team;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSetWinner extends CommandBase {

    @Override
    public String getCommandName(){
        return "setwinner";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/setwinner <team>";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(args.length == 0) throw new WrongUsageException("/setwinner <team>");
        String teamName = args[0];
        if(args[0].equalsIgnoreCase("setwinner") && args.length > 1) teamName = args[1];
        Map map = MapLoader.instance().getMap(sender.getEntityWorld());
        Team team = map.getTeamManager().getTeam(teamName);
        map.getGameController().setWinner(team);
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Winner is set").setColor(EnumChatFormatting.GREEN));
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args){
        if(args.length != 1) return Arrays.asList();
        Map map = MapLoader.instance().getMap(sender.getEntityWorld());
        List<String> teams = Lists.newArrayList();
        for(Team team : map.getTeamManager().getTeams()){
            teams.add(team.getTeamId());
        }
        return getListOfStringsFromIterableMatchingLastWord(args, teams);
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
