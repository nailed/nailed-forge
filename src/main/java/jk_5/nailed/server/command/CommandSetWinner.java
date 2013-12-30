package jk_5.nailed.server.command;

import com.google.common.collect.Lists;
import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Team;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSetWinner extends NailedCommand {

    @Override
    public String getCommandName(){
        return "setwinner";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0) throw new WrongUsageException("/setwinner <team>");
        map.getGameController().setWinner(map.getTeamManager().getTeam(args[0]));
        //FIXME
        //sender.sendChatToPlayer(ChatMessageComponent.createFromText("Winner is set").setColor(EnumChatFormatting.GREEN));
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
}
