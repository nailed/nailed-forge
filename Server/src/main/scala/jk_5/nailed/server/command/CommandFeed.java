package jk_5.nailed.server.command;

import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import jk_5.nailed.api.map.Map;
import jk_5.nailed.util.NailedFoodStats;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandFeed extends NailedCommand {

    public CommandFeed() {
        super("feed");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            if(sender instanceof EntityPlayer){
                if(map.getGameManager().isGameRunning()){
                    throw new CommandException("You may not feed people while a game is running");
                }
                EntityPlayer p = (EntityPlayer) sender;
                p.setHealth(20);
                ((NailedFoodStats)p.getFoodStats()).setFood(20);
            }else{
                throw new CommandException("Usage: /feed <player>");
            }
            return;
        }else if(args.length > 1){
            if(sender instanceof EntityPlayer){
                throw new CommandException("Usage: /feed [player]");
            }else{
                throw new CommandException("Usage: /feed <player>");
            }
        }
        EntityPlayerMP[] healed = NailedCommand.getPlayersList(sender, args[0]);
        for(EntityPlayerMP p : healed){
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
