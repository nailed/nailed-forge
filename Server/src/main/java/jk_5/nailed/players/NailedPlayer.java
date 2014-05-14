package jk_5.nailed.players;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.NailedWebUser;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.chat.joinmessage.JoinMessageSender;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.map.Location;
import jk_5.nailed.map.NailedMap;
import jk_5.nailed.map.Spawnpoint;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.permissions.Group;
import jk_5.nailed.permissions.NailedPermissionFactory;
import jk_5.nailed.permissions.User;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.web.auth.WebUser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.permissions.api.PermissionsManager;

import java.util.List;
import java.util.Random;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlayer implements Player {

    private final GameProfile gameProfile;
    private Map currentMap;
    private boolean online = false;
    private int fps;
    private Spawnpoint spawnpoint;
    private int pdaID = -1;
    private NetHandlerPlayServer netHandler;
    private boolean editModeEnabled = false;
    private int maxHealth = 20;
    private int minHealth = 0;
    private List<Player> playersVisible = Lists.newArrayList();
    private boolean isNailed = true;

    private NailedWebUser webUser;

    public NailedPlayer(GameProfile gameProfile){
        this.gameProfile = gameProfile;
        this.webUser = new WebUser(null, this.getUsername(), this.getUsername(), "", false);
    }

    public void sendNotification(String message){
        this.sendNotification(message, null);
    }

    public void sendNotification(String message, ResourceLocation icon){
        this.sendNotification(message, icon, 0xFFFFFF);
    }

    public void sendNotification(String message, ResourceLocation icon, int iconColor){
        EntityPlayerMP entity = this.getEntity();
        if(entity != null){
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.Notification(message, icon, iconColor), entity);
        }
    }

    public void sendChat(String message){
        this.sendChat(new ChatComponentText(message));
    }

    public void sendChat(IChatComponent message){
        EntityPlayerMP entity = this.getEntity();
        if(entity != null) this.getEntity().addChatComponentMessage(message);
    }

    public void sendPacket(Packet packet){
        this.netHandler.sendPacket(packet);
    }

    public EntityPlayerMP getEntity(){
        return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.getUsername());
    }

    public boolean isOp(){
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(this.getUsername());
    }

    public String getChatPrefix(){
        User info = this.getPermissionInfo();
        if(info != null){
            Group group = info.getMainGroup();
            if(group == null){
                return this.getTeam().getColor() + this.getUsername() + ChatColor.RESET;
            }else{
                return this.getTeam().getColor() + group.getPrefix() + this.getUsername() + group.getSuffix() + ChatColor.RESET;
            }
        }else{
            return this.getTeam().getColor() + this.getUsername() + ChatColor.RESET;
        }
    }

    public Team getTeam(){
        return this.getCurrentMap().getTeamManager().getPlayerTeam(this);
    }

    public void setTeam(Team team){
        this.getCurrentMap().getTeamManager().setPlayerTeam(this, team);
    }

    public String getUsername(){
        return this.gameProfile.getName();
    }

    public String getId(){
        return this.gameProfile.getId();
    }

    public Map getCurrentMap(){
        if(this.currentMap == null) this.currentMap = NailedAPI.getMapLoader().getLobby();
        return this.currentMap;
    }

    public void onLogin() {
        this.online = true;
        this.netHandler = this.getEntity().playerNetServerHandler;
        if(this.editModeEnabled){
            this.sendEditModePacket();
        }
        if(!(!this.webUser.isAuthenticated() && IpcManager.instance().isConnected())){
            JoinMessageSender.onPlayerJoin(this);
        }
    }

    public void onLogout() {
        this.online = false;
        this.netHandler = null;
    }

    public void onChangedDimension() {
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.TimeUpdate(""), this.getEntity());
        this.sendEditModePacket();
    }

    public void onRespawn() {
        this.getEntity().setSpawnChunk(null, false);
        if(this.getCurrentMap() instanceof NailedMap){
            ((NailedMap) this.getCurrentMap()).getMachine().queueEvent("respawn", this);
        }
    }

    public void teleportToMap(Map map){
        NailedAPI.getTeleporter().teleportEntity(this.getEntity(), map.getSpawnTeleport());
    }

    public Location getLocation(){
        EntityPlayer player = this.getEntity();
        return new Location(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
    }

    public Gamemode getGameMode(){
        return Gamemode.fromId(this.getEntity().theItemInWorldManager.getGameType().getID());
    }

    public void setGameMode(Gamemode mode){
        EntityPlayerMP entity = this.getEntity();
        entity.theItemInWorldManager.setGameType(WorldSettings.GameType.getByID(mode.getId()));
        this.sendPacket(new S2BPacketChangeGameState(3, mode.getId()));
    }

    @Override
    public String getWinnerName(){
        return this.getChatPrefix();
    }

    @Override
    public String getWinnerColoredName(){
        return this.getWinnerName();
    }

    public User getPermissionInfo(){
        if(PermissionsManager.getPermFactory() instanceof NailedPermissionFactory){
            return ((NailedPermissionFactory) PermissionsManager.getPermFactory()).getUserInfo(this.getUsername());
        }
        return null;
    }

    @Override
    public boolean hasPermission(String node){
        return PermissionsManager.checkPerm(this.getEntity(), node);
    }

    @Override
    public void sendTimeUpdate(String msg){
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.TimeUpdate(msg), this.getEntity());
    }

    @Override
    public void setEditModeEnabled(boolean editModeEnabled){
        this.editModeEnabled = editModeEnabled;
        IChatComponent component = new ChatComponentText("Edit mode is " + (this.editModeEnabled ? "enabled" : "disabled"));
        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
        this.sendChat(component);
        this.sendEditModePacket();
    }

    public void sendEditModePacket(){
        if(this.editModeEnabled){
            ByteBuf buffer = Unpooled.buffer();
            Mappack mappack = this.currentMap.getMappack();
            if(mappack != null){
                MappackMetadata meta = mappack.getMappackMetadata();
                ByteBufUtils.writeUTF8String(buffer, meta.getName());
                //TODO: fix sending of location names
                meta.getSpawnPoint().write(buffer);
                buffer.writeInt(meta.getRandomSpawnpoints().size());
                for(Location spawnpoint : meta.getRandomSpawnpoints()){
                    spawnpoint.write(buffer);
                }
                NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.EditMode(true, buffer), this.getEntity());
            }
        }else{
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.EditMode(false, null), this.getEntity());
        }
    }

    @Override
    public GameProfile getGameProfile() {
        return this.gameProfile;
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    @Override
    public int getFps() {
        return this.fps;
    }

    @Override
    public Spawnpoint getSpawnpoint() {
        return this.spawnpoint;
    }

    @Override
    public int getPdaID() {
        return this.pdaID;
    }

    @Override
    public NetHandlerPlayServer getNetHandler() {
        return this.netHandler;
    }

    @Override
    public boolean isEditModeEnabled() {
        return this.editModeEnabled;
    }

    @Override
    public NailedWebUser getWebUser() {
        return this.webUser;
    }

    @Override
    public void setCurrentMap(Map currentMap) {
        this.currentMap = currentMap;
    }

    @Override
    public void setFps(int fps) {
        this.fps = fps;
    }

    @Override
    public void setSpawnpoint(Spawnpoint spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    @Override
    public void setPdaID(int pdaID) {
        this.pdaID = pdaID;
    }

    @Override
    public void setWebUser(NailedWebUser webUser) {
        this.webUser = webUser;
    }

    public void teleportToLobby(){ this.teleportToMap(NailedAPI.getMapLoader().getLobby());}

    public void setMaxHealth(int max){ this.maxHealth = max; }

    public int getMaxHealth(){ return this.maxHealth; }

    public void setMinHealth(int min){ this.minHealth = min; }

    public int getMinHealth(){ return this.minHealth; }

    public List<Player> getPlayersVisible(){
        return this.playersVisible;
    }

    public void addPlayerVisible(Player player){
        if (this.playersVisible.contains(player)) return;
        this.playersVisible.add(player);
    }

    public void replacePlayerVisible(Player player, List<Player> players, Random random){
        players.remove(this.playersVisible);
        this.playersVisible.remove(player);
        this.playersVisible.add(players.get(random.nextInt() % players.size()));
    }

    public void removePlayerVisible(Player player){
        if (this.playersVisible.contains(player)) this.playersVisible.remove(player);
    }

    public int getNumPlayersVisible(){
        return this.playersVisible.size();
    }

    public void setPlayersVisible(List<Player> list){
        if (list != null) this.playersVisible = list;
    }

    public boolean isNailed(){
        return this.isNailed;
    }

    public void setNailed(boolean isNailed){
        this.isNailed = isNailed;
    }
}
