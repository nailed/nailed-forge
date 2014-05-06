package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.MappackLoader;
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

    public CommandReloadMappacks(){
        super("reloadmappacks");
    }

    @Override
    public void processCommandWithMap(final ICommandSender sender, Map map, String[] args){
        NailedAPI.getMappackLoader().loadMappacks(new Callback<MappackLoader>() {
            @Override
            public void callback(MappackLoader obj) {
                IChatComponent component = new ChatComponentText("Successfully loaded " + obj.getMappacks().size() + " mappacks");
                component.getChatStyle().setColor(EnumChatFormatting.GREEN);
                sender.addChatMessage(component);
            }
        });
    }
}
