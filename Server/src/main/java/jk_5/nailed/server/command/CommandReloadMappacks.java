package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReloadMappacks extends NailedCommand {

    @Override
    public String getCommandName(){
        return "reloadmappacks";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 3;
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        MapLoader.instance().loadMappacks();

        IChatComponent component = new ChatComponentText("Successfully loaded " + MapLoader.instance().getMappacks().size() + " mappacks");
        component.func_150256_b().func_150238_a(EnumChatFormatting.GREEN);
        sender.func_145747_a(component);
    }
}
