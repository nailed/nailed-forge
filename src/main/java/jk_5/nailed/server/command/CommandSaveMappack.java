package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.util.EnumChatFormatting;

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
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Saved to mappack " + map.getMappack().getMappackMetadata().getName()).setColor(EnumChatFormatting.GREEN));
        }else{
            sender.sendChatToPlayer(ChatMessageComponent.createFromText("Operation not supported for this mappack").setColor(EnumChatFormatting.RED));
        }
    }
}
