package jk_5.nailed.players;

import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldSettings;

import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.ReflectionHelper;

import net.minecraftforge.permissions.api.PermissionsManager;

import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.camera.IMovement;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.map.MappackMetadata;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.player.ClientType;
import jk_5.nailed.api.player.IncompatibleClientException;
import jk_5.nailed.api.player.NailedWebUser;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.api.scripting.ILuaObject;
import jk_5.nailed.chat.joinmessage.JoinMessageSender;
import jk_5.nailed.ipc.IpcManager;
import jk_5.nailed.map.Location;
import jk_5.nailed.map.NailedMap;
import jk_5.nailed.map.RenderPoint;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;
import jk_5.nailed.permissions.Group;
import jk_5.nailed.permissions.NailedPermissionFactory;
import jk_5.nailed.permissions.User;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.NailedFoodStats;
import jk_5.nailed.web.auth.WebUser;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlayer implements Player, ILuaObject {

    private final GameProfile gameProfile;
    private Map currentMap;
    private boolean online = false;
    private int fps;
    private Location spawnpoint;
    private NetHandlerPlayServer netHandler;
    private boolean editModeEnabled = false;
    private int maxHealth = 20;
    private int minHealth = 0;
    private List<Player> playersVisible = Lists.newArrayList();
    private ClientType clientType = ClientType.VANILLA; // standard = vanilla, no problems with idconflicts;
    private List<RenderPoint[]> renderList;

    private NailedWebUser webUser;

    public NailedPlayer(GameProfile gameProfile) {
        this.gameProfile = gameProfile;
        this.webUser = new WebUser(null, this.getUsername(), this.getUsername(), "", false);
    }

    public void sendChat(String message) {
        this.sendChat(new ChatComponentText(message));
    }

    public void sendChat(IChatComponent message) {
        EntityPlayerMP entity = this.getEntity();
        if(entity != null){
            this.getEntity().addChatComponentMessage(message);
        }
    }

    public void sendPacket(Packet packet) {
        this.netHandler.sendPacket(packet);
    }

    public EntityPlayerMP getEntity() {
        return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.getUsername());
    }

    public boolean isOp() {
        return MinecraftServer.getServer().getConfigurationManager().isPlayerOpped(this.getUsername());
    }

    public String getChatPrefix() {
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

    public Team getTeam() {
        return this.getCurrentMap().getTeamManager().getPlayerTeam(this);
    }

    public void setTeam(Team team) {
        this.getCurrentMap().getTeamManager().setPlayerTeam(this, team);
    }

    public String getUsername() {
        return this.gameProfile.getName();
    }

    public String getId() {
        return this.gameProfile.getId();
    }

    public Map getCurrentMap() {
        if(this.currentMap == null){
            this.currentMap = NailedAPI.getMapLoader().getLobby();
        }
        return this.currentMap;
    }

    public void onLogin() {
        this.online = true;
        this.netHandler = this.getEntity().playerNetServerHandler;
        this.editModeEnabled = false;
        if(!(!this.webUser.isAuthenticated() && IpcManager.instance().isConnected())){
            JoinMessageSender.onPlayerJoin(this);
        }
    }

    public void onLogout() {
        this.online = false;
        this.netHandler = null;
    }

    public void onChangedDimension() {
        this.sendTimeUpdate("");
        this.sendEditModePacket();
    }

    public void onRespawn() {
        this.getEntity().setSpawnChunk(null, false);
        if(this.getCurrentMap() instanceof NailedMap){
            ((NailedMap) this.getCurrentMap()).getMachine().queueEvent("respawn", this);
        }
    }

    public void teleportToMap(Map map) {
        NailedAPI.getTeleporter().teleportEntity(this.getEntity(), map.getSpawnTeleport());
    }

    public Location getLocation() {
        EntityPlayer player = this.getEntity();
        return new Location(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
    }

    public void setLocation(Location l) {
        EntityPlayer player = this.getEntity();
        player.posX = l.getX();
        player.posY = l.getY();
        player.posZ = l.getZ();
        player.cameraPitch = l.getPitch();
        player.cameraYaw = l.getYaw();
    }

    public Gamemode getGameMode() {
        return Gamemode.fromId(this.getEntity().theItemInWorldManager.getGameType().getID());
    }

    public void setGameMode(Gamemode mode) {
        EntityPlayerMP entity = this.getEntity();
        entity.theItemInWorldManager.setGameType(WorldSettings.GameType.getByID(mode.getId()));
        this.sendPacket(new S2BPacketChangeGameState(3, mode.getId()));
    }

    @Override
    public String getWinnerName() {
        return this.getChatPrefix();
    }

    @Override
    public String getWinnerColoredName() {
        return this.getWinnerName();
    }

    public User getPermissionInfo() {
        if(PermissionsManager.getPermFactory() instanceof NailedPermissionFactory){
            return ((NailedPermissionFactory) PermissionsManager.getPermFactory()).getUserInfo(this.getUsername());
        }
        return null;
    }

    @Override
    public boolean hasPermission(String node) {
        return PermissionsManager.checkPerm(this.getEntity(), node);
    }

    @Override
    public void sendTimeUpdate(String msg) {
        if(this.clientType == ClientType.NAILED){
            NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.TimeUpdate(msg), this.getEntity());
        }else{
            if(!msg.isEmpty()){
                this.sendChat(msg);
            }
        }
    }

    @Override
    public void setEditModeEnabled(boolean editModeEnabled) throws IncompatibleClientException {
        //if(this.playerClient != PlayerClient.NAILED) throw new IncompatibleClientException("Edit mode on non-nailed client", this);
        this.editModeEnabled = editModeEnabled;
        IChatComponent component = new ChatComponentText("Edit mode is " + (this.editModeEnabled ? "enabled" : "disabled"));
        component.getChatStyle().setColor(EnumChatFormatting.GREEN);
        this.sendChat(component);
        this.sendEditModePacket();
    }

    public void sendEditModePacket() {
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
    public Location getSpawnpoint() {
        return this.spawnpoint;
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
    public void setSpawnpoint(Location spawnpoint) {
        this.spawnpoint = spawnpoint;
    }

    @Override
    public void setWebUser(NailedWebUser webUser) {
        this.webUser = webUser;
    }

    @Override
    public void teleportToLobby() {
        this.teleportToMap(NailedAPI.getMapLoader().getLobby());
    }

    @Override
    public void setMaxHealth(int max) {
        this.maxHealth = max;
    }

    @Override
    public int getMaxHealth() {
        return this.maxHealth;
    }

    @Override
    public void setMinHealth(int min) {
        this.minHealth = min;
    }

    @Override
    public int getMinHealth() {
        return this.minHealth;
    }

    @Override
    public List<Player> getPlayersVisible() {
        return this.playersVisible;
    }

    @Override
    public void addPlayerVisible(Player player) {
        if(this.playersVisible.contains(player)){
            return;
        }
        this.playersVisible.add(player);
    }

    @Override
    public void replacePlayerVisible(Player player, List<Player> players, Random random) {
        players.removeAll(this.playersVisible);
        this.playersVisible.remove(player);
        this.playersVisible.add(players.get(random.nextInt() % players.size()));
    }

    @Override
    public ClientType getClientType() {
        return this.clientType;
    }

    @Override
    public void removePlayerVisible(Player player) {
        if(this.playersVisible.contains(player)){
            this.playersVisible.remove(player);
        }
    }

    @Override
    public int getNumPlayersVisible() {
        return this.playersVisible.size();
    }

    @Override
    public void setPlayersVisible(List<Player> list) {
        if(list != null){
            this.playersVisible = list;
        }
    }

    @Override
    public void setClientType(ClientType client) {
        this.clientType = client;
    }

    @Override
    public void setMoving(IMovement movement) {
        this.getEntity().isAirBorne = true;
        this.setGameMode(Gamemode.CREATIVE);
        NailedAPI.getMovementHandler().addPlayerMovement(this, movement);
    }

    @Override
    public void kick(String reason) {
        this.netHandler.kickPlayerFromServer(reason);
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "getUsername",
                "getTeam",
                "clearInventory",
                "setSpawn",
                "setGamemode",
                "setHealth",
                "setFood",
                "setExperience",
                "getType",
                "freeze",
                "sendChatComponent",
                "sendChat",
                "addPotionEffect",
                "removePotionEffect",
                "sendTimeUpdate",
                "giveItem",
                "setInventoryItem",
                "setMinFood",
                "setMinHealth",
                "setMaxFood",
                "setMaxHealth"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
        switch(method){
            case 0: //getUsername
                return new Object[]{this.getUsername()};
            case 1: //getTeam
                return new Object[]{this.getTeam()};
            case 2: //clearInventory
                return new Object[]{this.getEntity().inventory.clearInventory(null, -1)};
            case 3: //setSpawn
                if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                    Location spawn = new Location(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue());
                    this.setSpawnpoint(spawn);
                }else if(arguments.length == 5 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double){
                    Location spawn = new Location(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue(), ((Double) arguments[3]).floatValue(), ((Double) arguments[4]).floatValue());
                    this.setSpawnpoint(spawn);
                }else{
                    throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                }
                break;
            case 4: //setGamemode
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.setGameMode(Gamemode.fromId(((Double) arguments[0]).intValue()));
                }else{
                    throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                }
                break;
            case 5: //setHealth
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    EntityPlayerMP entity = this.getEntity();
                    float newHealth = ((Double) arguments[0]).floatValue();
                    if(newHealth > 0){
                        entity.deathTime = 0;
                    }
                    entity.setHealth(newHealth);
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 6: //setFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    FoodStats foodStats = this.getEntity().getFoodStats();
                    foodStats.addStats(((Double) arguments[0]).intValue() - foodStats.getFoodLevel(), 0);
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 7: //setExperience
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.getEntity().experienceLevel = ((Double) arguments[0]).intValue();
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 8: //getType
                return new Object[]{"player"};
            case 9: //freeze
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    boolean doFreeze = (Boolean) arguments[0];
                    EntityPlayerMP entity = this.getEntity();
                    ReflectionHelper.setPrivateValue(PlayerCapabilities.class, entity.capabilities, doFreeze ? 0f : 0.1f, "walkSpeed");
                    ReflectionHelper.setPrivateValue(PlayerCapabilities.class, entity.capabilities, doFreeze ? 0f : 0.05f, "flySpeed");
                    entity.sendPlayerAbilities();
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 10: //sendChatComponent
                if(arguments.length == 1 && arguments[0] instanceof String){
                    try{
                        IChatComponent comp = IChatComponent.Serializer.func_150699_a((String) arguments[0]);
                        this.sendChat(comp);
                    }catch(JsonParseException e){
                        e.printStackTrace();
                        throw new Exception("Chat message is not of json format");
                    }
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 11: //sendChat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.sendChat((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 12: //addPotionEffect
                EntityPlayerMP entity = this.getEntity();
                if(arguments.length == 2 && arguments[0] instanceof Double && arguments[1] instanceof Double){
                    int id = ((Double) arguments[0]).intValue();
                    int duration = ((Double) arguments[1]).intValue();
                    entity.addPotionEffect(new PotionEffect(id, duration, 0, true));
                }else if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                    int id = ((Double) arguments[0]).intValue();
                    int duration = ((Double) arguments[1]).intValue();
                    int amplifier = ((Double) arguments[2]).intValue();
                    entity.addPotionEffect(new PotionEffect(id, duration, amplifier, true));
                }else{
                    throw new Exception("Expected 2 or 3 int arguments");
                }
                break;
            case 13: //removePotionEffect
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.getEntity().removePotionEffect(((Double) arguments[0]).intValue());
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 14: //sendTimeUpdate
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.sendTimeUpdate((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 15: //giveItem
                if(arguments.length == 2 && arguments[0] instanceof String && arguments[1] instanceof Double){
                    int amount = ((Double) arguments[1]).intValue();
                    String item = (String) arguments[0];
                    Object i = Item.itemRegistry.getObject(item);
                    if(i == null){
                        throw new Exception("Unknown item " + item);
                    }
                    ItemStack stack = new ItemStack((Item) i, amount);
                    this.getEntity().inventory.addItemStackToInventory(stack);
                }else if(arguments.length == 3 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double){
                    int meta = ((Double) arguments[2]).intValue();
                    int amount = ((Double) arguments[1]).intValue();
                    String item = (String) arguments[0];
                    Object i = Item.itemRegistry.getObject(item);
                    if(i == null){
                        throw new Exception("Unknown item " + item);
                    }
                    ItemStack stack = new ItemStack((Item) i, amount, meta);
                    this.getEntity().inventory.addItemStackToInventory(stack);
                }else{
                    throw new Exception("Expected 1 string and 1 or 2 int arguments");
                }
                break;
            case 16: //setInventoryItem
                if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof String && arguments[2] instanceof Double && arguments[3] instanceof Double){
                    int amount = ((Double) arguments[2]).intValue();
                    String item = (String) arguments[1];
                    int slot = ((Double) arguments[0]).intValue();
                    Object i = Item.itemRegistry.getObject(item);
                    if(i == null){
                        throw new Exception("Unknown item " + item);
                    }
                    ItemStack stack = new ItemStack((Item) i, amount);
                    this.getEntity().inventory.setInventorySlotContents(slot, stack);
                }else if(arguments.length == 4 && arguments[0] instanceof Double && arguments[1] instanceof String && arguments[2] instanceof Double && arguments[3] instanceof Double){
                    int meta = ((Double) arguments[3]).intValue();
                    int amount = ((Double) arguments[2]).intValue();
                    String item = (String) arguments[1];
                    int slot = ((Double) arguments[0]).intValue();
                    Object i = Item.itemRegistry.getObject(item);
                    if(i == null){
                        throw new Exception("Unknown item " + item);
                    }
                    ItemStack stack = new ItemStack((Item) i, amount, meta);
                    this.getEntity().inventory.setInventorySlotContents(slot, stack);
                }else{
                    throw new Exception("Expected 1 int, 1 string and then 1 or 2 int arguments");
                }
                break;
            case 17: // setMinFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    ((NailedFoodStats) this.getEntity().getFoodStats()).setMinFoodLevel(((Double) arguments[0]).intValue());
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 18: // setMinHealth
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.setMinHealth(((Double) arguments[0]).intValue());
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 19: // setMaxFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    ((NailedFoodStats) this.getEntity().getFoodStats()).setMaxFoodLevel(((Double) arguments[0]).intValue());
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 20: // setMaxHealth
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.setMaxHealth(((Double) arguments[0]).intValue());
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
        }
        return null;
    }

    private void setRenderList(List<RenderPoint[]> list){
        this.clearRenderList();
        this.addRenderList(list);
    }

    private void addRenderList(List<RenderPoint[]> list){
        this.renderList.addAll(list);
    }

    private void addRenderArray(RenderPoint[] array){
        this.renderList.add(array);
        this.sendRenderListPacket();
    }

    private void clearRenderList(){
        this.renderList.clear();
        this.sendRenderListPacket();
    }

    private void sendRenderListPacket(){
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.RenderList(0, this.renderList), this.getEntity());
    }
}
