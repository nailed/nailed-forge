package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.Gamemode;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.Arrays;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandGamemode extends NailedCommand {

    @Override
    public String getCommandName(){
        return "gamemode";
    }

    @Override
    public List getCommandAliases(){
        return Arrays.asList("gm");
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "commands.gamemode.usage";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 0){
            if(sender instanceof EntityPlayer){
                Player target = PlayerRegistry.instance().getPlayer((EntityPlayer) sender);
                Gamemode gamemode = this.getToggledGamemode(target);
                if(gamemode == null) throw new CommandException("Unknown gamemode");
                target.setGameMode(gamemode);
            }else throw new CommandException("You are not a player");
        }else if(args.length == 1){
            if(sender instanceof EntityPlayer){
                Gamemode gamemode = this.getGameModeFromCommand(args[0]);
                Player target = PlayerRegistry.instance().getPlayer((EntityPlayer) sender);
                target.setGameMode(gamemode);
            }else{
                Player target = PlayerRegistry.instance().getPlayerByUsername(args[0]);
                if(target == null) throw new CommandException("Player " + args[0] + " was not found");
                Gamemode gamemode = this.getToggledGamemode(target);
                target.setGameMode(gamemode);
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

    public List addTabCompletionOptions(ICommandSender sender, String[] args){
        if(args.length == 1){
            return getListOfStringsMatchingLastWord(args, "survival", "creative", "adventure");
        }else if(args.length == 2){
            return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
        }
        return null;
    }

    public boolean isUsernameIndex(String[] args, int index){
        return index == 1;
    }
}
