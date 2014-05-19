package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * Created by matthias on 5/8/14.
 *
 * heal command
 */
public class CommandHeal extends NailedCommand {

    public CommandHeal() {
        super("heal");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            if(sender instanceof EntityPlayer){
                if(map.getGameManager().isGameRunning()){
                    throw new CommandException("You may not heal people when a game is running");
                }
                EntityPlayer p = (EntityPlayer) sender;
                p.setHealth(20);
                p.getFoodStats().setFoodLevel(20);
            }else{
                throw new CommandException("Usage: /heal <player>");
            }
            return;
        }else if(args.length > 1){
            if(sender instanceof EntityPlayer){
                throw new CommandException("Usage: /heal [player]");
            }else{
                throw new CommandException("Usage: /heal <player>");
            }
        }
        EntityPlayerMP[] healed = NailedCommand.getPlayersList(sender, args[0]);
        for(EntityPlayerMP p : healed){
            p.setHealth(20);
            p.getFoodStats().setFoodLevel(20);
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, MinecraftServer.getServer().getAllUsernames());
        }else{
            return null;
        }
    }
}
