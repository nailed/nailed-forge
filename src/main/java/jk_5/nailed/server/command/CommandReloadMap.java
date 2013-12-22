package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadMap extends CommandBase {

    @Override
    public String getCommandName(){
        return "reloadmap";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/reloadmap <mapname> - Restores the map from the mappack";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(args.length == 0) throw new WrongUsageException("/reloadmap <mapname>");
        Map map = null;
        for(Map m : MapLoader.instance().getMaps()){
            if(args[0].equalsIgnoreCase(m.getSaveFileName())){
                map = m;
                break;
            }
        }
        if(map == null) throw new CommandException("Map does not exist");
        map.reloadFromMappack();
        sender.sendChatToPlayer(ChatMessageComponent.createFromText("Reloaded map " + map.getSaveFileName()).setColor(EnumChatFormatting.GREEN));
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
