package jk_5.nailed.server.command;

import java.util.*;

import com.google.common.collect.*;

import org.apache.commons.lang3.tuple.*;

import net.minecraft.command.*;
import net.minecraft.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;

import jk_5.nailed.api.*;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.*;
import jk_5.nailed.api.map.teleport.*;
import jk_5.nailed.api.player.*;
import jk_5.nailed.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTP extends NailedCommand {

    public CommandTP() {
        super("tp");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        List<Pair<Entity, TeleportOptions>> options = Lists.newArrayList();
        try{
            if(args.length == 1){
                //  /tp destinationPlayer
                EntityPlayer teleporting;
                if(sender instanceof EntityPlayer){
                    teleporting = (EntityPlayer) sender;
                }else{
                    throw new CommandException("commands.nailed.tp.fail.notarget");
                }
                Player target = NailedAPI.getPlayerRegistry().getPlayerByUsername(args[0]);
                if(target == null){
                    throw new CommandException("commands.nailed.tp.fail.notarget");
                }
                TeleportOptions option = new TeleportOptions();
                option.setLocation(target.getLocation());
                option.setDestination(target.getCurrentMap());
                options.add(new ImmutablePair<Entity, TeleportOptions>(teleporting, option));
            }else if(args.length == 2){
                //  /tp teleportingPlayer destinationPlayer | destinationMap
                EntityPlayerMP[] players = getPlayersList(sender, args[0]);
                TeleportOptions option = getDestination(sender, args[1]);
                for(EntityPlayerMP player : players){
                    options.add(new ImmutablePair<Entity, TeleportOptions>(player, option));
                }
            }else if(args.length == 3){
                //  /tp x y z
                EntityPlayer teleporting;
                if(sender instanceof EntityPlayer){
                    teleporting = (EntityPlayer) sender;
                }else{
                    throw new CommandException("commands.nailed.tp.fail.notarget");
                }
                double x = handleRelativeNumber(sender, teleporting.posX, args[0]);
                double y = handleRelativeNumber(sender, teleporting.posY, args[1], 0, 0);
                double z = handleRelativeNumber(sender, teleporting.posZ, args[2]);
                TeleportOptions option = new TeleportOptions();
                option.setLocation(new Location(x, y, z, teleporting.rotationYaw, teleporting.rotationPitch));
                option.setDestination(NailedAPI.getMapLoader().getMap(teleporting.worldObj));
                options.add(new ImmutablePair<Entity, TeleportOptions>(teleporting, option));
            }else if(args.length == 4){
                //  /tp teleportingPlayer x y z
                EntityPlayerMP[] players = getPlayersList(sender, args[0]);
                for(EntityPlayerMP player : players){
                    double x = handleRelativeNumber(sender, player.posX, args[1]);
                    double y = handleRelativeNumber(sender, player.posY, args[2], 0, 0);
                    double z = handleRelativeNumber(sender, player.posZ, args[3]);
                    TeleportOptions option = new TeleportOptions();
                    option.setLocation(new Location(x, y, z, player.rotationYaw, player.rotationPitch));
                    option.setDestination(NailedAPI.getMapLoader().getMap(player.worldObj));
                    options.add(new ImmutablePair<Entity, TeleportOptions>(player, option));
                }
            }else if(args.length == 5){
                //  /tp teleportingPlayer x y z options
                String opt = args[4];
                boolean particles = !opt.contains("noparticle");
                boolean sound = !opt.contains("nosound");
                boolean momentum = opt.contains("momentum");
                boolean clearInv = opt.contains("clearinventory");
                EntityPlayerMP[] players = getPlayersList(sender, args[0]);
                for(EntityPlayerMP player : players){
                    double x = handleRelativeNumber(sender, player.posX, args[1]);
                    double y = handleRelativeNumber(sender, player.posY, args[2], 0, 0);
                    double z = handleRelativeNumber(sender, player.posZ, args[3]);
                    TeleportOptions option = new TeleportOptions();
                    option.setSound(sound ? option.getSound() : null);
                    option.setSpawnParticles(particles);
                    option.setMaintainMomentum(momentum);
                    option.setClearInventory(clearInv);
                    option.setLocation(new Location(x, y, z, player.rotationYaw, player.rotationPitch));
                    option.setDestination(NailedAPI.getMapLoader().getMap(player.worldObj));
                    options.add(new ImmutablePair<Entity, TeleportOptions>(player, option));
                }
            }
            for(Pair<Entity, TeleportOptions> p : options){
                NailedAPI.getTeleporter().teleportEntity(p.getKey(), p.getValue());
            }
        }catch(CommandException e){
            sender.addChatMessage(new ChatComponentTranslation(e.getMessage()));
            sender.addChatMessage(new ChatComponentTranslation(this.getCommandUsage(sender)));
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender par1ICommandSender, String[] args) {
        if(args.length == 1 || args.length == 2){
            return getUsernameOptions(args);
        }else{
            return null;
        }
    }

    private static TeleportOptions getDestination(ICommandSender sender, String data) {
        TeleportOptions dest = new TeleportOptions();
        Player p = NailedAPI.getPlayerRegistry().getPlayerByUsername(data);
        if(p != null){
            dest.setLocation(p.getLocation());
            dest.setDestination(p.getCurrentMap());
        }else{
            Map map = NailedAPI.getMapLoader().getMap(data);
            if(map != null){
                Mappack mappack = map.getMappack();
                if(mappack != null){
                    dest.setLocation(mappack.getMappackMetadata().getSpawnPoint());
                }
                dest.setDestination(map);
            }else{
                map = NailedAPI.getMapLoader().getMap(CommandBase.parseInt(sender, data));
                if(map != null){
                    Mappack mappack = map.getMappack();
                    if(mappack != null){
                        dest.setLocation(mappack.getMappackMetadata().getSpawnPoint());
                    }
                    dest.setDestination(map);
                }else{
                    throw new CommandException("commands.nailed.tp.fail.nodest");
                }
            }
        }
        return dest;
    }
}