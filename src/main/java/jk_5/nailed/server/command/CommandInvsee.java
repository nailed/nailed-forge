package jk_5.nailed.server.command;

import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.invsee.InventoryOtherPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.packet.Packet100OpenWindow;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandInvsee extends CommandBase {

    @Override
    public String getCommandName(){
        return "invsee";
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "/invsee <username> - Look at the inventory of another player";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args){
        if(!(sender instanceof EntityPlayer)) throw new CommandException("This command can only be used by players");
        if(args.length == 1){
            Player player = PlayerRegistry.instance().getPlayer(args[0]);
            if(player == null) throw new CommandException("That player is not online!");
            EntityPlayerMP entity = player.getEntity();
            if(entity.openContainer != entity.inventoryContainer){
                entity.closeScreen();
            }
            entity.incrementWindowID();

            InventoryOtherPlayer chest = new InventoryOtherPlayer(entity, (EntityPlayerMP) sender);
            entity.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(entity.currentWindowId, 0, chest.getInvName(), chest.getSizeInventory(), true));
            entity.openContainer = new ContainerChest(entity.inventory, chest);
            entity.openContainer.windowId = entity.currentWindowId;
            entity.openContainer.addCraftingToCrafters(entity);
        }
    }

    @Override
    public int compareTo(Object o){
        return 0;
    }
}
