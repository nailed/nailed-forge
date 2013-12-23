package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class NailedCommand extends CommandBase {

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "commands.nailed." + this.getCommandName() + ".usage";
    }

    @Override
    public final void processCommand(ICommandSender sender, String[] args){
        Map map = MapLoader.instance().getMap(sender.getEntityWorld());
        if(map == null) throw new CommandException("Something went wrong! You are not in a world");
        Player player = null;
        if(sender instanceof EntityPlayer) player = PlayerRegistry.instance().getPlayer(((EntityPlayer) sender).username);
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
        throw new CommandException("commands.nailed.error.notValid");
    }

    public final int compareTo(Object o){
        return this.compareTo((ICommand)o);
    }
}
