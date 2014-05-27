package jk_5.nailed.server.command;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.network.play.server.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;
import jk_5.nailed.util.invsee.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandInvsee extends NailedCommand {

    public CommandInvsee() {
        super("invsee");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args) {
        if(args.length == 1){
            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            if(player == null){
                throw new CommandException("That player is not online!");
            }
            EntityPlayerMP entity = sender.getEntity();
            if(entity.openContainer != entity.inventoryContainer){
                entity.closeScreen();
            }
            entity.getNextWindowId();

            InventoryOtherPlayer chest = new InventoryOtherPlayer(player.getEntity(), entity);
            entity.playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(entity.currentWindowId, 0, chest.getInventoryName(), chest.getSizeInventory(), true));
            entity.openContainer = new ContainerChest(entity.inventory, chest);
            entity.openContainer.windowId = entity.currentWindowId;
            entity.openContainer.addCraftingToCrafters(entity);
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getUsernameOptions(args);
        }else{
            return null;
        }
    }
}
