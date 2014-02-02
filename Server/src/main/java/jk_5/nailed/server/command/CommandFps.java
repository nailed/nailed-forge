package jk_5.nailed.server.command;

import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
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
            Player player = PlayerRegistry.instance().getPlayerByUsername(args[0]);
            sender.func_145747_a(new ChatComponentText(player.getUsername() + ": " + player.getFps() + " FPS"));
            return;
        }
        int total = 0;
        int count = 0;
        for(Player player : PlayerRegistry.instance().getPlayers()){
            if(player.isOnline()){
                sender.func_145747_a(new ChatComponentText(player.getUsername() + ": " + player.getFps() + " FPS"));
                count ++;
                total += player.getFps();
            }
        }
        sender.func_145747_a(new ChatComponentText("Average: " + (total / count) + " FPS"));
    }
}
