package jk_5.nailed.server.command;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.entity.player.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGamemode extends NailedCommand {

    public CommandGamemode() {
        super("gamemode");
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("gm");
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "commands.gamemode.usage";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 0){
            // /gm
            if(sender instanceof EntityPlayer){
                Player target = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) sender);
                Gamemode gamemode = this.getToggledGamemode(target);
                if(gamemode == null){
                    throw new CommandException("Unknown gamemode");
                }
                target.setGameMode(gamemode);
            }else{
                throw new CommandException("You are not a player");
            }
        }else if(args.length == 1){
            // /gm 1
            if(sender instanceof EntityPlayer){
                Gamemode gamemode = this.getGameModeFromCommand(args[0]); //TODO: bulletproof this for nulls
                Player target = NailedAPI.getPlayerRegistry().getPlayer((EntityPlayer) sender);
                target.setGameMode(gamemode);
            }else{
                throw new CommandException("You are not a player");
            }
        }else if(args.length == 2){
            // /gm 1 username
            EntityPlayerMP[] matches = getPlayersList(sender, args[1]);
            Gamemode newmode = this.getGameModeFromCommand(args[0]);
            for(EntityPlayerMP match : matches){
                Player target = NailedAPI.getPlayerRegistry().getPlayer(match);
                if(target == null){
                    continue;
                }
                target.setGameMode(newmode);
            }
        }
    }

    protected Gamemode getGameModeFromCommand(String mode) {
        if("survival".equalsIgnoreCase(mode) || "s".equalsIgnoreCase(mode) || "0".equals(mode)){
            return Gamemode.SURVIVAL;
        }else if("creative".equalsIgnoreCase(mode) || "c".equalsIgnoreCase(mode) || "1".equals(mode)){
            return Gamemode.CREATIVE;
        }else if("adventure".equalsIgnoreCase(mode) || "a".equalsIgnoreCase(mode) || "2".equals(mode)){
            return Gamemode.ADVENTURE;
        }
        return null;
    }

    protected Gamemode getToggledGamemode(Player player) {
        if(player.getGameMode() == Gamemode.CREATIVE){
            return Gamemode.SURVIVAL;
        }else{
            return Gamemode.CREATIVE;
        }
    }

    public List<String> addAutocomplete(ICommandSender sender, String[] args) {
        if(args.length == 1){
            return getOptions(args, "survival", "creative", "adventure");
        }else if(args.length == 2){
            return getUsernameOptions(args);
        }
        return null;
    }
}
