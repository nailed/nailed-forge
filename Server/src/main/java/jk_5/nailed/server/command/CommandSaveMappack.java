package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandSaveMappack extends NailedCommand {

    @Override
    public String getCommandName(){
        return "savemappack";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(map.getMappack().saveAsMappack(map)){
            IChatComponent component = new ChatComponentText("Saved to mappack " + map.getMappack().getMappackMetadata().getName());
            component.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(component);
        }else{
            IChatComponent component = new ChatComponentText("Operation not supported for this mappack");
            component.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(component);
        }
    }
}
