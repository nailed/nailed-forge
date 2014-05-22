package jk_5.nailed.server.command;

import net.minecraft.command.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandFps extends NailedCommand {

    public CommandFps() {
        super("fps");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length > 0){
            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            sender.addChatMessage(new ChatComponentText(player.getUsername() + ": " + player.getFps() + " FPS"));
            return;
        }
        int total = 0;
        int count = 0;
        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            if(player.isOnline()){
                sender.addChatMessage(new ChatComponentText(player.getUsername() + ": " + player.getFps() + " FPS"));
                count++;
                total += player.getFps();
            }
        }
        sender.addChatMessage(new ChatComponentText("Average: " + (total / count) + " FPS"));
    }
}
