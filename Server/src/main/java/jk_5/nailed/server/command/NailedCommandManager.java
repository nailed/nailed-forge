package jk_5.nailed.server.command;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.command.*;
import net.minecraft.command.CommandToggleDownfall;
import net.minecraft.command.server.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CommandEvent;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedCommandManager extends CommandHandler implements IAdminCommand {

    private static final String commandWarningsPerm = "minecraft.commandWarnings";
    private static final Logger logger = LogManager.getLogger();
    private static final Map<ICommand, String> commandOwners = Maps.newHashMap();

    public NailedCommandManager(){
        PermissionsManager.registerPermission(commandWarningsPerm, RegisteredPermValue.OP);

        this.registerCommand(new CommandGoto());
        this.registerCommand(new CommandTeam());
        this.registerCommand(new CommandStartGame());
        this.registerCommand(new CommandIrc());
        this.registerCommand(new CommandMap());
        this.registerCommand(new CommandSetWinner());
        this.registerCommand(new CommandReloadMappacks());
        this.registerCommand(new CommandTime());
        this.registerCommand(new CommandSudo());
        this.registerCommand(new CommandInvsee());
        this.registerCommand(new CommandFirework());
        this.registerCommand(new CommandLobby());
        this.registerCommand(new CommandReloadMap());
        this.registerCommand(new CommandKickall());
        this.registerCommand(new CommandSaveMappack());
        this.registerCommand(new CommandSafehouse());
        this.registerCommand(new CommandTps());
        this.registerCommand(new CommandFps());
        this.registerCommand(new CommandCB());
        this.registerCommand(new CommandReloadPermissions());
        this.registerCommand(new CommandTerminal());
        this.registerCommand(new CommandRandomSpawnpoint());
        this.registerCommand(new CommandEdit());
        this.registerCommand(new CommandRegisterAchievement());
        this.registerCommand(new CommandReconnectIpc());
        this.registerCommand(new CommandTP());
        this.registerCommand(new CommandToggleDownfall());
        this.registerCommand(new CommandGamemode());
        this.registerCommand(new CommandGamerule());
        this.registerCommand(new CommandDifficulty());

        this.registerCommand(new CommandKill());
        this.registerCommand(new CommandWeather());
        this.registerCommand(new CommandXP());
        this.registerCommand(new CommandGive());
        this.registerCommand(new CommandEffect());
        this.registerCommand(new CommandEnchant());
        this.registerCommand(new CommandEmote());
        this.registerCommand(new CommandShowSeed());
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
        this.registerCommand(new CommandServerKick());
        this.registerCommand(new CommandListPlayers());
        this.registerCommand(new CommandWhitelist());
        this.registerCommand(new CommandSetPlayerTimeout());

        CommandBase.setAdminCommander(this);
    }

    @Override
    public void notifyAdmins(ICommandSender sender, int normalSender, String key, Object... args){
        boolean notifyPlayers = true;

        ChatComponentTranslation chatcomponenttranslation = new ChatComponentTranslation("chat.type.admin", sender.getCommandSenderName(), new ChatComponentTranslation(key, args));
        chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
        chatcomponenttranslation.getChatStyle().setItalic(true);

        if (sender instanceof CommandBlockLogic){
            if(!sender.getEntityWorld().getGameRules().getGameRuleBooleanValue("commandBlockOutput")){
                notifyPlayers = false;
            }
        }else{
            MinecraftServer.getServer().addChatMessage(chatcomponenttranslation);
        }

        if(notifyPlayers){
            //noinspection unchecked
            for(EntityPlayerMP player : (List<EntityPlayerMP>) MinecraftServer.getServer().getConfigurationManager().playerEntityList){
                if (player != sender && player.getEntityWorld() == sender.getEntityWorld() && PermissionsManager.checkPerm(player, commandWarningsPerm)){
                    player.addChatMessage(chatcomponenttranslation);
                }
            }
        }

        if((normalSender & 1) != 1){
            sender.addChatMessage(new ChatComponentTranslation(key, args));
        }
    }

    @Override
    public int executeCommand(ICommandSender sender, String input){
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
            if(!hasPermission && PermissionsManager.getPerm(sname, owner + ".commands." + icommand.getCommandName()).check()){
                hasPermission = true;
            }

            if(!hasPermission){
                throw new CommandException("commands.generic.permission");
            }

            CommandEvent event = new CommandEvent(icommand, sender, args);
            if (MinecraftForge.EVENT_BUS.post(event)){
                if (event.exception != null){
                    throw event.exception;
                }
                return 1;
            }

            if (usernameIndex > -1){
                EntityPlayerMP[] matched = PlayerSelector.matchPlayers(sender, args[usernameIndex]);
                String username = args[usernameIndex];

                for(EntityPlayerMP entityplayermp : matched){
                    args[usernameIndex] = entityplayermp.getCommandSenderName();
                    try{
                        icommand.processCommand(sender, args);
                        timesExecuted ++;
                    }catch(CommandException commandexception){
                        ChatComponentTranslation chatcomponenttranslation1 = new ChatComponentTranslation(commandexception.getMessage(), commandexception.getErrorOjbects());
                        chatcomponenttranslation1.getChatStyle().setColor(EnumChatFormatting.RED);
                        sender.addChatMessage(chatcomponenttranslation1);
                    }
                }

                args[usernameIndex] = username;
            }else{
                icommand.processCommand(sender, args);
                timesExecuted ++;
            }
        }catch (WrongUsageException e){
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.usage", new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects()));
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        }catch(CommandException e){
            chatcomponenttranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorOjbects());
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
        }catch (Throwable throwable){
            chatcomponenttranslation = new ChatComponentTranslation("commands.generic.exception");
            chatcomponenttranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            sender.addChatMessage(chatcomponenttranslation);
            logger.error("Couldn\'t process command", throwable);
        }
        return timesExecuted;
    }

    @Override
    public ICommand registerCommand(ICommand command){
        String modid = "minecraft";
        ModContainer container = Loader.instance().activeModContainer();
        if(container != null){
            modid = container.getModId().toLowerCase();
        }
        commandOwners.put(command, modid);
        PermissionsManager.registerPermission(modid + ".commands." + command.getCommandName(), RegisteredPermValue.OP);
        return super.registerCommand(command);
    }

    private static String[] dropFirstString(String[] args){
        String[] ret = new String[args.length - 1];
        System.arraycopy(args, 1, ret, 0, args.length - 1);
        return ret;
    }

    private int getUsernameIndex(ICommand command, String[] args){
        if(command == null){
            return -1;
        }else{
            for (int i = 0; i < args.length; ++i){
                if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])){
                    return i;
                }
            }
            return -1;
        }
    }
}