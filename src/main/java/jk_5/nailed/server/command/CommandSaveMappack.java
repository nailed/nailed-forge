package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
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
            sender.func_145747_a(new ChatComponentText("Saved to mappack " + map.getMappack().getMappackMetadata().getName()).setColor(EnumChatFormatting.GREEN));
        }else{
            sender.func_145747_a(new ChatComponentText("Operation not supported for this mappack").setColor(EnumChatFormatting.RED));
        }
    }
}
