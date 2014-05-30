package jk_5.nailed.server.command;

import java.util.*;
import javax.annotation.*;

import com.google.common.base.*;

import net.minecraft.command.*;
import net.minecraft.server.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandKick extends NailedCommand {

    public CommandKick() {
        super("kick");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            throw new WrongUsageException("Usage: /kick <player> [reason]");
        }
        Player target = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
        String reason = "No reason given";
        if(args.length > 1){
            String[] newArray = new String[args.length - 1];
            System.arraycopy(args, 1, newArray, 0, newArray.length);
            reason = Joiner.on(' ').join(newArray);
        }

        target.kick("Kicked by " + sender.getCommandSenderName() + ". Reason: " + reason);

        IChatComponent msg = new ChatComponentText("Player " + target.getUsername() + " was kicked by " + sender.getCommandSenderName());
        msg.getChatStyle().setColor(EnumChatFormatting.RED);
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(msg);
        msg = new ChatComponentText("Reason: " + reason);
        msg.getChatStyle().setColor(EnumChatFormatting.RED);
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(msg);

        msg = new ChatComponentText("Successfully kicked player " + target.getUsername());
        msg.getChatStyle().setColor(EnumChatFormatting.GREEN);
        sender.addChatMessage(msg);
    }

    @Nullable
    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getUsernameOptions(args);
        }else return null;
    }
}
