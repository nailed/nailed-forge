package jk_5.nailed.server.command;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandClearInventory;
import net.minecraft.command.CommandDebug;
import net.minecraft.command.CommandEnchant;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandGive;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.CommandHelp;
import net.minecraft.command.CommandKill;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.CommandPlaySound;
import net.minecraft.command.CommandSetPlayerTimeout;
import net.minecraft.command.CommandSetSpawnpoint;
import net.minecraft.command.CommandSpreadPlayers;
import net.minecraft.command.CommandXP;
import net.minecraft.command.IAdminCommand;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerSelector;
import net.minecraft.command.WrongUsageException;
import net.minecraft.command.server.CommandAchievement;
import net.minecraft.command.server.CommandBanIp;
import net.minecraft.command.server.CommandBanPlayer;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.command.server.CommandBroadcast;
import net.minecraft.command.server.CommandDeOp;
import net.minecraft.command.server.CommandEmote;
import net.minecraft.command.server.CommandListBans;
import net.minecraft.command.server.CommandListPlayers;
import net.minecraft.command.server.CommandMessage;
import net.minecraft.command.server.CommandMessageRaw;
import net.minecraft.command.server.CommandOp;
import net.minecraft.command.server.CommandPardonIp;
import net.minecraft.command.server.CommandPardonPlayer;
import net.minecraft.command.server.CommandSaveAll;
import net.minecraft.command.server.CommandSaveOff;
import net.minecraft.command.server.CommandSaveOn;
import net.minecraft.command.server.CommandScoreboard;
import net.minecraft.command.server.CommandSetBlock;
import net.minecraft.command.server.CommandStop;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.command.server.CommandTestFor;
import net.minecraft.command.server.CommandTestForBlock;
import net.minecraft.command.server.CommandWhitelist;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

import jk_5.nailed.api.command.Command;
import jk_5.nailed.api.command.CommandRegistry;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedCommandManager extends CommandHandler implements IAdminCommand, CommandRegistry {

    private static final String commandWarningsPerm = "minecraft.commandWarnings";
    private static final Logger logger = LogManager.getLogger();
    private static final Map<ICommand, String> commandOwners = Maps.newHashMap();

    public NailedCommandManager() {
        PermissionsManager.registerPermission(commandWarningsPerm, RegisteredPermValue.OP);

        //Nailed commands
        this.registerCommand(CommandGoto$.MODULE$);
        this.registerCommand(new CommandTeam());
        this.registerCommand(CommandStartGame$.MODULE$);
        this.registerCommand(CommandMap$.MODULE$);
        this.registerCommand(CommandSetWinner$.MODULE$);
        this.registerCommand(CommandReload$.MODULE$);
        this.registerCommand(new CommandTime());
        this.registerCommand(CommandSudo$.MODULE$);
        this.registerCommand(CommandInvsee$.MODULE$);
        this.registerCommand(CommandFirework$.MODULE$);
        this.registerCommand(CommandLobby$.MODULE$);
        this.registerCommand(CommandKickall$.MODULE$);
        this.registerCommand(CommandSaveMappack$.MODULE$);
        this.registerCommand(CommandSafehouse$.MODULE$);
        this.registerCommand(new CommandTps());
        this.registerCommand(CommandFps$.MODULE$);
        this.registerCommand(CommandTerminal$.MODULE$);
        this.registerCommand(CommandRandomSpawnpoint$.MODULE$);
        this.registerCommand(CommandEdit$.MODULE$);
        this.registerCommand(new CommandTP());
        this.registerCommand(CommandGamemode$.MODULE$);
        this.registerCommand(CommandGamerule$.MODULE$);
        this.registerCommand(CommandDifficulty$.MODULE$);
        this.registerCommand(CommandZone$.MODULE$);
        this.registerCommand(CommandHeal$.MODULE$);
        this.registerCommand(new CommandWayPoint());
        this.registerCommand(new CommandMove());
        this.registerCommand(CommandWhereAmI$.MODULE$);
        this.registerCommand(CommandEffect$.MODULE$);
        this.registerCommand(CommandKick$.MODULE$);
        this.registerCommand(CommandFeed$.MODULE$);
        this.registerCommand(CommandSeed$.MODULE$);

        //Normal vanilla commands
        this.registerCommand(new CommandKill());
        this.registerCommand(new CommandXP());
        this.registerCommand(new CommandGive());
        this.registerCommand(new CommandEnchant());
        this.registerCommand(new CommandEmote());
        this.registerCommand(new CommandHelp());
        this.registerCommand(new CommandDebug());
        this.registerCommand(new CommandMessage());
        this.registerCommand(new CommandBroadcast());
        this.registerCommand(new CommandSetSpawnpoint());
        this.registerCommand(new CommandClearInventory());
        this.registerCommand(new CommandTestFor());
        this.registerCommand(new CommandSpreadPlayers());
        this.registerCommand(new CommandPlaySound());
        this.registerCommand(new CommandScoreboard());
        this.registerCommand(new CommandAchievement());
        this.registerCommand(new CommandSummon());
        this.registerCommand(new CommandSetBlock());
        this.registerCommand(new CommandTestForBlock());
        this.registerCommand(new CommandMessageRaw());

        //Server-only vanilla commands
        this.registerCommand(new CommandOp());
        this.registerCommand(new CommandDeOp());
        this.registerCommand(new CommandStop());
        this.registerCommand(new CommandSaveAll());
        this.registerCommand(new CommandSaveOff());
        this.registerCommand(new CommandSaveOn());
        this.registerCommand(new CommandBanIp());
        this.registerCommand(new CommandPardonIp());
        this.registerCommand(new CommandBanPlayer());
        this.registerCommand(new CommandListBans());
        this.registerCommand(new CommandPardonPlayer());
        this.registerCommand(new CommandListPlayers());
        this.registerCommand(new CommandWhitelist());
        this.registerCommand(new CommandSetPlayerTimeout());

        CommandBase.setAdminCommander(this);
    }

    @Override
    public void notifyAdmins(ICommandSender sender, int normalSender, String key, Object... args) {
        boolean notifyPlayers = true;

        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("chat.type.admin", sender.getCommandSenderName(), new ChatComponentTranslation(key, args));
        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
        chatcomponenttranslation.getChatStyle().setItalic(true);

        if(sender instanceof CommandBlockLogic){
            if(!sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("commandBlockOutput")){
                notifyPlayers = false;
            }
        }else{
            MinecraftServer.getServer().addChatMessage(chatcomponenttranslation);
        }

        if(notifyPlayers){
            //noinspection unchecked
            for(EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList){
                if(player != sender && player.getEntityWorld() == sender.getEntityWorld() && PermissionsManager.checkPerm(player, commandWarningsPerm)){
                    player.addChatMessage(chatcomponenttranslation);
                }
            }
        }

        if((normalSender & 1) != 1){
            sender.addChatMessage(new ChatComponentTranslation(key, args));
        }
    }

    @Override
    public int executeCommand(ICommandSender sender, String input) {
        input = input.trim();

        if(input.startsWith("/")){
            input = input.substring(1);
        }

        String[] args = input.split(" ");
        String commandName = args[0];
        args = dropFirstString(args);
        ICommand icommand = (ICommand) getCommands().get(commandName);
        int usernameIndex = this.getUsernameIndex(icommand, args);
        int timesExecuted = 0;
        ChatComponentTranslation chatcomponenttranslation;

        try{
            if(icommand == null){
                throw new CommandNotFoundException();
            }

            boolean hasPermission = false;

            String sname = sender.getCommandSenderName();
            if(sender instanceof CommandBlockLogic){
                sname = "[CommandBlock]";
            }

            if(sender instanceof MinecraftServer){
                hasPermission = true;
            }

            String owner = commandOwners.get(icommand);
            if(!hasPermission){
                if(icommand instanceof SubpermissionCommand){
                    SubpermissionCommand sub = (SubpermissionCommand) icommand;
                    hasPermission = sub.hasPermission(sname, args);
                }else{
                    hasPermission = PermissionsManager.getPerm(sname, owner + ".commands." + icommand.getCommandName()).check();
                }
            }

            if(!hasPermission){
                throw new CommandException("commands.generic.permission");
            }

            CommandEvent event = new CommandEvent(icommand, sender, args);
            if(MinecraftForge.EVENT_BUS.post(event)){
                if(event.exception != null){
                    throw event.exception;
                }
                return 1;
            }

            if(usernameIndex > -1){
                EntityPlayerMP[] matched = PlayerSelector.matchPlayers(sender, args[usernameIndex]);
                String username = args[usernameIndex];

                for(EntityPlayerMP entityplayermp : matched){
                    args[usernameIndex] = entityplayermp.getCommandSenderName();
                    try{
                        icommand.processCommand(sender, args);
                        timesExecuted++;
                    }catch(CommandException commandexception){
                        ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorOjbects());
                        chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
                        sender.addChatMessage(chatcomponenttranslation1);
                    }
                }

                args[usernameIndex] = username;
            }else{
                icommand.processCommand(sender, args);
                timesExecuted++;
            }
        }catch(WrongUsageException e){
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.usage", new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects()));
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        }catch(CommandException e){
            chatcomponenttranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects());
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        }catch(Throwable throwable){
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
            logger.error("Couldn\'t process command", throwable);
        }
        return timesExecuted;
    }

    @Override
    public ICommand registerCommand(ICommand command) {
        String modid = "minecraft";
        ModContainer container = Loader.instance().activeModContainer();
        if(container != null){
            modid = container.getModId().toLowerCase();
        }
        return this.registerCommand(command, modid);
    }

    public ICommand registerCommand(ICommand command, String owner) {
        commandOwners.put(command, owner);
        if(command instanceof SubpermissionCommand){
            SubpermissionCommand sub = (SubpermissionCommand) command;
            sub.registerPermissions(owner);
        }else{
            PermissionsManager.registerPermission(owner + ".commands." + command.getCommandName(), RegisteredPermValue.OP);
        }
        return super.registerCommand(command);
    }

    private static String[] dropFirstString(String[] args) {
        String[] ret = new String[args.length - 1];
        System.arraycopy(args, 1, ret, 0, args.length - 1);
        return ret;
    }

    private int getUsernameIndex(ICommand command, String[] args) {
        if(command == null){
            return -1;
        }else{
            for(int i = 0; i < args.length; ++i){
                if(command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])){
                    return i;
                }
            }
            return -1;
        }
    }

    @Override
    public void registerCommand(Command command) {

    }
}
