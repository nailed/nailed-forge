package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
        if(map == null) this.process(sender, args);
        Player player = null;
        if(sender instanceof EntityPlayer) player = PlayerRegistry.instance().getPlayer(((EntityPlayer) sender));
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

    public final int compareTo(Object o){
        return this.compareTo((ICommand)o);
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
        boolean flag = arg.startsWith("~");
        double d1 = flag ? origin : 0.0D;

        if((!flag) || (arg.length() > 1)){
            boolean flag1 = arg.contains(".");

            if(flag){
                arg = arg.substring(1);
            }

            d1 += parseDouble(par1ICommandSender, arg);

            if((!flag1) && (!flag)){
                d1 += 0.5D;
            }
        }

        if((min != 0) || (max != 0)){
            if(d1 < min){
                throw new NumberInvalidException("commands.generic.double.tooSmall", new Object[]{Double.valueOf(d1), Integer.valueOf(min)});
            }

            if(d1 > max){
                throw new NumberInvalidException("commands.generic.double.tooBig", new Object[]{Double.valueOf(d1), Integer.valueOf(max)});
            }
        }

        return d1;
    }
}
