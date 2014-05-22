package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * Created by matthias on 22-5-14.
 */
public class CommandWhereAmI extends NailedCommand {

    public CommandWhereAmI(){
        super("whereami");
    }

    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        IChatComponent comp = new ChatComponentText("You are in map " + map.getSaveFileName());
        comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
        sender.addChatMessage(comp);
    }
}
