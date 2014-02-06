package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandFps extends NailedCommand {

    @Override
    public String getCommandName(){
        return "fps";
    }

    @Override
    public void process(ICommandSender sender, String[] args){
        if (args.length > 0){
            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            sender.addChatMessage(new ChatComponentText(player.getUsername() + ": " + player.getFps() + " FPS"));
            return;
        }
        int total = 0;
        int count = 0;
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.isOnline()){
                sender.addChatMessage(new ChatComponentText(player.getUsername() + ": " + player.getFps() + " FPS"));
                count ++;
                total += player.getFps();
            }
        }
        sender.addChatMessage(new ChatComponentText("Average: " + (total / count) + " FPS"));
    }
}
