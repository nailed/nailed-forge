package jk_5.nailed.server.command;

import net.minecraft.command.*;
import net.minecraft.util.*;

import jk_5.nailed.api.map.*;

/**
 * Created by matthias on 22-5-14.
 */
public class CommandWhereAmI extends NailedCommand {

    public CommandWhereAmI() {
        super("whereami");
    }

    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        IChatComponent comp = new ChatComponentText("You are in map " + map.getSaveFileName());
        comp.getChatStyle().setColor(EnumChatFormatting.GOLD);
        sender.addChatMessage(comp);
    }
}
