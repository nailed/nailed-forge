package jk_5.nailed.server.command;

import net.minecraft.command.*;
import net.minecraft.command.CommandToggleDownfall;
import net.minecraft.command.server.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedCommandManager extends CommandHandler implements IAdminCommand {

    private static final String commandWarningsPerm = "minecraft.commandWarnings";

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
        this.registerCommand(new CommandSetDefaultSpawnpoint());
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
}
