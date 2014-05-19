package jk_5.nailed.server.command;

import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGamemode extends NailedCommand {

    public CommandGamemode(){
        super("gamemode");
    }

    @Override
    public List<String> getAliases(){
        return Arrays.asList("gm");
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "commands.gamemode.usage";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0){
            // /gm
            if(sender instanceof EntityPlayer){
                Player target = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) sender);
                Gamemode gamemode = this.getToggledGamemode(target);
                if(gamemode == null) throw new CommandException("Unknown gamemode");
                target.setGameMode(gamemode);
            }else throw new CommandException("You are not a player");
        }else if(args.length == 1){
            // /gm 1
            if(sender instanceof EntityPlayer){
                Gamemode gamemode = this.getGameModeFromCommand(args[0]); //TODO: bulletproof this for nulls
                Player target = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) sender);
                target.setGameMode(gamemode);
            }else throw new CommandException("You are not a player");
        }else if(args.length == 2){
            // /gm 1 username
            EntityPlayerMP[] matches = getPlayersList(sender, args[1]);
            Gamemode newmode = this.getGameModeFromCommand(args[0]);
            for(EntityPlayerMP match : matches){
                Player target = NailedAPI.getPlayerRegistry().getPlayer(match);
                if(target == null) continue;
                target.setGameMode(newmode);
            }
        }
    }

    protected Gamemode getGameModeFromCommand(String mode){
        if(mode.equalsIgnoreCase("survival") || mode.equalsIgnoreCase("s") || mode.equals("0")){
            return Gamemode.SURVIVAL;
        }else if(mode.equalsIgnoreCase("creative") || mode.equalsIgnoreCase("c") || mode.equals("1")){
            return Gamemode.CREATIVE;
        }else if(mode.equalsIgnoreCase("adventure") || mode.equalsIgnoreCase("a") || mode.equals("2")){
            return Gamemode.ADVENTURE;
        }
        return null;
    }

    protected Gamemode getToggledGamemode(Player player){
        if(player.getGameMode() == Gamemode.CREATIVE){
            return Gamemode.SURVIVAL;
        }else{
            return Gamemode.CREATIVE;
        }
    }

    public List<String> addAutocomplete(ICommandSender sender, String[] args){
        if(args.length == 1){
            return getOptions(args, "survival", "creative", "adventure");
        }else if(args.length == 2){
            return getUsernameOptions(args);
        }
        return null;
    }
}
