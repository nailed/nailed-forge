package jk_5.nailed.server.command;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.mappack.Spawnpoint;
import jk_5.nailed.map.teleport.TeleportHelper;
import jk_5.nailed.map.teleport.TeleportOptions;
import jk_5.nailed.players.Player;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTP extends NailedCommand {

    @Override
    public String getCommandName(){
        return "tp";
    }

    @Override
    public int getRequiredPermissionLevel(){
        return 2;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] args){
        if(args.length == 1) return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getAllUsernames());
        if(args.length == 2) return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getAllUsernames());
        return null;
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        super.processCommandPlayer(sender, map, args);
    }

    @Override
    public void process(ICommandSender sender, String[] args){
        Entity subject;
        TeleportOptions link;
        try{
            String sSubject = null;
            String sTarget;
            String sX = null;
            String sY = null;
            String sZ = null;

            if(args.length > 3){
                if(args.length > 4){
                    sSubject = args[(args.length - 5)];
                }
                sTarget = args[(args.length - 4)];
                sX = args[(args.length - 3)];
                sY = args[(args.length - 2)];
                sZ = args[(args.length - 1)];
            }else if(args.length == 2){
                sSubject = args[(args.length - 2)];
                sTarget = args[(args.length - 1)];
            }else if(args.length == 1){
                sTarget = args[(args.length - 1)];
            }else{
                throw new WrongUsageException("commands.nailed.tp.usage");
            }

            if(sSubject == null)
                subject = getCommandSenderAsPlayer(sender);
            else{
                subject = getTargetPlayer(sender, sSubject);
            }
            if(subject == null){
                throw new WrongUsageException("commands.nailed.tp.fail.nosubject", new Object[0]);
            }

            link = getLinkInfoForTarget(sender, subject, sTarget, sX, sY, sZ);
            TeleportHelper.travelEntity(subject.worldObj, subject, link);
        }catch(CommandException e){
            sender.func_145747_a(new ChatComponentTranslation(e.getMessage()));
            sender.func_145747_a(new ChatComponentTranslation(getCommandUsage(sender)));
        }
    }

    public static TeleportOptions getLinkInfoForTarget(ICommandSender sender, Entity subject, String sTarget, String sX, String sY, String sZ){
        TeleportOptions link = null;
        try{
            Entity target = getTargetPlayer(sender, sTarget);
            link = createOptionsForLocation(new Spawnpoint(target), MapLoader.instance().getMap(target.worldObj));
        }catch(PlayerNotFoundException e){
            //NOOP. We'll try again later
        }
        if(link == null){
            link = new TeleportOptions();
            int dim = (int) (handleRelativeNumber(sender, subject.dimension, sTarget, 0, 0) - 0.5D);
            Map destMap = MapLoader.instance().getMap(dim);
            if(destMap == null){
                throw new CommandException("commands.nailed.tp.fail.noworld", dim);
            }
            link.setDestination(destMap);
            if((sX != null) && (sY != null) && (sZ != null)){
                int x = (int) handleRelativeNumber(sender, subject.posX, sX);
                int y = (int) handleRelativeNumber(sender, subject.posY, sY, 0, 0);
                int z = (int) handleRelativeNumber(sender, subject.posZ, sZ);
                link.setCoordinates(new Spawnpoint(x, y, z));
            }
        }
        return link;
    }

    private static TeleportOptions createOptionsForLocation(Spawnpoint spawnpoint, Map map){
        TeleportOptions options = new TeleportOptions();
        options.setCoordinates(spawnpoint);
        options.setDestination(map);
        return options;
    }
}
