package jk_5.nailed.irc;

import jk_5.nailed.NailedLog;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.event.PlayerChatEvent;
import jk_5.nailed.event.PlayerJoinEvent;
import jk_5.nailed.event.PlayerLeaveEvent;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatMessageComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import org.jibble.pircbot.PircBot;

/**
 * No description given
 *
 * @author jk-5
 */
public class IrcBot extends PircBot {

    @Getter @Setter private boolean enabled = false;
    private final String host;
    private final int port;
    private String serverPassword;
    private final String channel;
    private String channelPassword;

    public IrcBot(){
        ConfigTag tag = NailedModContainer.getConfig().getTag("irc").useBraces();
        this.enabled = tag.getTag("enabled").getBooleanValue(false);
        this.host = tag.getTag("host").getValue("");
        this.port = tag.getTag("port").getIntValue(6667);
        this.serverPassword = tag.getTag("serverPassword").getValue("");
        this.channel = tag.getTag("channel").getValue("");
        this.channelPassword = tag.getTag("channelPassword").getValue("");
        if(this.serverPassword.length() == 0) this.serverPassword = null;
        if(this.channelPassword.length() == 0) this.channelPassword = null;
        this.setName("Nailed");
        this.setLogin("Nailed");
        this.setVersion("Nailed");
        this.setVerbose(false);

        MinecraftForge.EVENT_BUS.register(this);
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onChat(PlayerChatEvent event){
        this.sendMessage(this.channel, "<" + event.player.getUsername() + "> " + event.message);
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerJoinEvent event){
        this.sendMessage(this.channel, "* " + event.player.getUsername() + " has joined the game");
    }

    @ForgeSubscribe
    @SuppressWarnings("unused")
    public void onPlayerJoin(PlayerLeaveEvent event){
        this.sendMessage(this.channel, "* " + event.player.getUsername() + " has left the game");
    }

    public void connect(){
        if(this.enabled){
            new ConnectThread().start();
        }
    }

    @Override
    protected void onConnect() {
        NailedLog.info("Connected to irc!");
    }

    @Override
    protected void onDisconnect() {
        NailedLog.info("Disconnected from irc!");
    }

    @Override
    protected void onMessage(String channel, String sender, String login, String hostname, String message) {
        ServerConfigurationManager configManager = MinecraftServer.getServer().getConfigurationManager();
        if(message.equals("!list") || message.equals("!players")){
            this.sendMessage(channel, configManager.getCurrentPlayerCount() + " online players: " + configManager.getPlayerListAsString());
        }else{
            configManager.sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " <" + sender + "> " + message));
        }
    }

    @Override
    protected void onPrivateMessage(String sender, String login, String hostname, String message) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + sender + "]" + ChatColor.RESET + " <" + sender + "> " + message));
    }

    @Override
    protected void onAction(String sender, String login, String hostname, String target, String action) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + sender + " " + action));
    }

    @Override
    protected void onJoin(String channel, String sender, String login, String hostname) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + sender + " joined the channel"));
    }

    @Override
    protected void onPart(String channel, String sender, String login, String hostname) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + sender + " left the channel"));
    }

    @Override
    protected void onNickChange(String oldNick, String login, String hostname, String newNick) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + oldNick + " is now known as " + newNick));
    }

    @Override
    protected void onKick(String channel, String kickerNick, String kickerLogin, String kickerHostname, String recipientNick, String reason) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + recipientNick + " was kicked by " + kickerNick + " (" + reason + ")"));
    }

    @Override
    protected void onQuit(String sourceNick, String sourceLogin, String sourceHostname, String reason) {
        MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + sourceNick + " left irc (" + reason + ")"));
    }

    @Override
    protected void onTopic(String channel, String topic, String setBy, long date, boolean changed) {
        if(changed) MinecraftServer.getServer().getConfigurationManager().sendChatMsg(ChatMessageComponent.createFromText(ChatColor.GRAY + "[" + channel + "]" + ChatColor.RESET + " * " + setBy + " set the topic: " + topic));
    }

    private class ConnectThread extends Thread {
        @Override
        public void run() {
            try{
                NailedLog.info("Connecting to irc...");
                connect(host, port, serverPassword);
                joinChannel(channel, channelPassword);
            }catch(Exception e){
                NailedLog.warning("An error was thrown while connecting to irc");
            }
        }
    }
}
