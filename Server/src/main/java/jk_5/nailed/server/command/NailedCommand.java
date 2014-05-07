package jk_5.nailed.server.command;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class NailedCommand implements ICommand {

    private final String commandName;

    public NailedCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "commands.nailed." + this.commandName + ".usage";
    }

    @Override
    public List getCommandAliases(){
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender var1){
        return true; //Permissions will be checked by the permissionsmanager using the commandevent
    }

    @Override
    public List addTabCompletionOptions(ICommandSender var1, String[] var2){
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] var1, int var2){
        return false;
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] args){
        Map map = NailedAPI.getMapLoader().getMap(sender.getEntityWorld());
        Player player = null;
        if(sender instanceof EntityPlayer) player = NailedAPI.getPlayerRegistry().getPlayer(((EntityPlayer) sender));
        if(player == null){
            this.processCommandWithMap(sender, map, args);
        }else{
            this.processCommandPlayer(player, map, args);
        }
    }

    public void processCommandPlayer(Player sender, Map map, String[] args){
        this.processCommandWithMap(sender.getEntity(), map, args);
    }

    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        this.process(sender, args);
    }

    public void process(ICommandSender sender,String[] args){
        throw new CommandException("commands.nailed.error.notValid");
    }

    @Override
    public final int compareTo(@Nonnull Object o){
        return this.compareTo((ICommand)o);
    }

    public final int compareTo(ICommand command){
        return this.commandName.compareTo(command.getCommandName());
    }

    public static EntityPlayerMP getTargetPlayer(ICommandSender sender, String target){
        EntityPlayerMP entityplayermp = PlayerSelector.matchOnePlayer(sender, target);

        if(entityplayermp == null){
            entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(target);
        }
        if(entityplayermp == null){
            throw new PlayerNotFoundException();
        }
        return entityplayermp;
    }

    public static double handleRelativeNumber(ICommandSender sender, double origin, String arg){
        return handleRelativeNumber(sender, origin, arg, -30000000, 30000000);
    }

    public static double handleRelativeNumber(ICommandSender par1ICommandSender, double origin, String arg, int min, int max){
        boolean isRelative = arg.startsWith("~");
        double value = isRelative ? origin : 0.0D;

        if(!isRelative || arg.length() > 1){
            boolean isDouble = arg.contains(".");

            if(isRelative){
                arg = arg.substring(1);
            }

            value += CommandBase.parseDouble(par1ICommandSender, arg);

            if(!isDouble && !isRelative){
                value += 0.5D;
            }
        }

        if(min != 0 || max != 0){
            if(value < min){
                throw new NumberInvalidException("commands.generic.double.tooSmall", value, min);
            }
            if(value > max){
                throw new NumberInvalidException("commands.generic.double.tooBig", value, max);
            }
        }

        return value;
    }

    public static EntityPlayerMP[] getPlayersList(ICommandSender sender, String pattern){
        EntityPlayerMP[] players = PlayerSelector.matchPlayers(sender, pattern);
        if(players == null){
            Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername(pattern);
            if(p == null) throw new CommandException("commands.nailed.tp.fail.notarget");
            players = new EntityPlayerMP[]{p.getEntity()};
        }
        return players;
    }

    public String getCommandName() {
        return this.commandName;
    }
}
