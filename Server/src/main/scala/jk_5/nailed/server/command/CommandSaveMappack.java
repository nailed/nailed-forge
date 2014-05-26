package jk_5.nailed.server.command;

import net.minecraft.command.*;
import net.minecraft.util.*;

import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSaveMappack extends NailedCommand {

    public CommandSaveMappack() {
        super("savemappack");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        Mappack mappack = map.getMappack();
        if(mappack != null){
            if(mappack.saveAsMappack(map)){
                IChatComponent component = new ChatComponentText("Saved to mappack " + map.getMappack().getMappackMetadata().getName());
                component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                sender.addChatMessage(component);
            }else{
                IChatComponent component = new ChatComponentText("Operation not supported for this mappack");
                component.getChatStyle().setColor(EnumChatFormatting.RED);
                sender.addChatMessage(component);
            }
        }else{
            //TODO: prompt the user for a name and save it as a new mappack
            throw new CommandException("This map does not have a mappack. It can\'t be saved (we plan on adding support for this)");
        }
    }
}
