package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.util.invsee.InventoryOtherPlayer;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandInvsee extends NailedCommand {

    public CommandInvsee(){
        super("invsee");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        if(args.length == 1){
            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
            if(player == null) throw new CommandException("That player is not online!");
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
    public List addTabCompletionOptions(ICommandSender iCommandSender, String[] strings){
        if(strings.length != 1) return Arrays.asList();
        return CommandBase.getListOfStringsMatchingLastWord(strings, MinecraftServer.getServer().getAllUsernames());
    }
}
