package jk_5.nailed.map.script.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonParseException;
import cpw.mods.fml.relauncher.ReflectionHelper;
import jk_5.nailed.api.Gamemode;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.stat.IStatType;
import jk_5.nailed.api.map.stat.Stat;
import jk_5.nailed.api.map.team.Team;
import jk_5.nailed.api.map.teleport.TeleportOptions;
import jk_5.nailed.api.map.teleport.Teleporter;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.api.scripting.ILuaObject;
import jk_5.nailed.map.Location;
import jk_5.nailed.map.Spawnpoint;
import jk_5.nailed.map.script.IAPIEnvironment;
import jk_5.nailed.map.script.LuaMachine;
import jk_5.nailed.map.stat.DefaultStat;
import jk_5.nailed.map.stat.types.StatTypeModifiable;
import jk_5.nailed.util.NailedFoodStats;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;

import java.util.HashMap;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class MapApi implements ILuaAPI {

    private final IAPIEnvironment env;
    private Map map;

    @java.beans.ConstructorProperties({"env"})
    public MapApi(IAPIEnvironment env) {
        this.env = env;
    }

    @Override
    public String[] getNames(){
        return new String[]{"map"};
    }

    @Override
    public void startup(){
        this.map = NailedAPI.getMapLoader().getMap(env.getMachine().getMachine().getWorld());
    }

    @Override
    public void advance(double paramDouble){

    }

    @Override
    public void shutdown(){

    }

    @Override
    public String[] getMethodNames(){
        return new String[]{
                "--UNUSED--",
                "sendChatComponent",
                "sendChat",
                "watchUnready",
                "winnerInterrupt",
                "getPlayers",
                "getTeams",
                "forEachPlayer",
                "forEachTeam",
                "getTeam",
                "setDifficulty",
                "sendTimeUpdate",
                "setTime",
                "setWinner",
                "disableStat",
                "enableStat",
                "spreadPlayers",
                "tpAllToLobby",
                "remove",
                "recycle",
                "hasMappack",
                "onStarted",
                "onStopped",
                "stopGame",
                "setMaxFood",
                "setMinFood",
                "setMaxHealth",
                "setMinHealth"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 1: //sendChatComponent
                if(arguments.length == 1 && arguments[0] instanceof String){
                    try{
                        IChatComponent comp = IChatComponent.Serializer.func_150699_a((String) arguments[0]);
                        this.map.broadcastChatMessage(comp);
                    }catch(JsonParseException e){
                        throw new Exception("Chat message is not of json format");
                    }
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 2: //sendChat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.broadcastChatMessage((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 3: //watchUnready
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWatchUnready((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 4: //winnerInterrupt
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().setWinnerInterrupt((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 5: //getPlayers
                List<Player> players = this.map.getPlayers();
                java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                for(int i = 0; i < players.size(); i++){
                    table.put(i + 1, wrapPlayer(this.env, players.get(i)));
                }
                return new Object[]{table};
            case 6: //getTeams
                List<Team> teams = this.map.getTeamManager().getTeams();
                java.util.Map<Integer, ILuaObject> table1 = Maps.newHashMap();
                for(int i = 0; i < teams.size(); i++){
                    table1.put(i + 1, wrapTeam(this.env, teams.get(i)));
                }
                return new Object[]{table1};
            case 7: //forEachPlayer
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    LuaMachine machine = this.env.getMachine().getLuaMachine();
                    List<Player> players1 = this.map.getPlayers();
                    for(int i = 0; i < players1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{wrapPlayer(this.env, players1.get(i))}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
                break;
            case 8: //forEachTeam
                if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                    LuaClosure closure = (LuaClosure) arguments[0];
                    LuaMachine machine = this.env.getMachine().getLuaMachine();
                    List<Team> teams1 = this.map.getTeamManager().getTeams();
                    for(int i = 0; i < teams1.size(); i++){
                        closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{wrapTeam(this.env, teams1.get(i))}, 0)));
                    }
                }else{
                    throw new Exception("Excpected 1 function as argument");
                }
            case 9: //getTeam
                if(arguments.length == 1 && arguments[0] instanceof String){
                    return new Object[]{wrapTeam(this.env, this.map.getTeamManager().getTeam((String) arguments[0]))};
                }else{
                    throw new Exception("Excpected 1 string argument");
                }
            case 10: //setDifficulty
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    // MappackMetadata meta = this.map.getMappack().getMappackMetadata();this is not used, and has a possibility of NPE, so i commented it out. TODO: check if needed
                    World server = this.map.getWorld();
                    int difficulty = ((Double) arguments[0]).intValue();
                    server.difficultySetting = EnumDifficulty.getDifficultyEnum(difficulty);
                    server.setAllowedSpawnTypes(difficulty > 0, true);
                }else{
                    throw new Exception("Excpected 1 int argument");
                }
                break;
            case 11: //sendTimeUpdate
                if(arguments.length == 1 && arguments[0] instanceof String){
                    this.map.getGameManager().setCountdownMessage((String) arguments[0]);
                }else{
                    throw new Exception("Expected 1 string argument");
                }
                break;
            case 12: //setTime
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    this.map.getWorld().setWorldTime(((Double) arguments[0]).intValue() % 24000);
                }else{
                    throw new Exception("Expected 1 int argument");
                }
                break;
            case 13: //setWinner
                if(arguments.length == 1 && arguments[0] instanceof HashMap){
                    try{
                        HashMap<String, LuaFunction> obj = (HashMap<String, LuaFunction>) arguments[0];
                        String type = obj.get("getType").call().checkjstring();
                        if(type.equals("player")){
                            String username = obj.get("getUsername").call().checkjstring();
                            Player player = NailedAPI.getPlayerRegistry().getPlayerByUsername(username);
                            if(player == null){
                                throw new Exception("Player " + username + " does not exist");
                            }
                            if(player.getCurrentMap() != this.map){
                                throw new Exception("Player " + username + " is not in this world");
                            }
                            this.map.getGameManager().setWinner(player);
                        }else if(type.equals("team")){
                            String name = obj.get("getID").call().checkjstring();
                            Team team = this.map.getTeamManager().getTeam(name);
                            if(team == null){
                                throw new Exception("Team " + name + " does not exist");
                            }
                            this.map.getGameManager().setWinner(team);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                        throw new Exception("The object passed is not a team or player");
                    }
                }else{
                    throw new Exception("Expected 1 player/team argument");
                }
                break;
            case 14: //disableStat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    Stat stat = this.map.getStatManager().getStat((String) arguments[0]);
                    if(stat != null && stat instanceof DefaultStat){
                        IStatType type = ((DefaultStat) stat).getType();
                        if(type instanceof StatTypeModifiable){
                            stat.disable();
                        }
                    }
                }
                break;
            case 15: //enableStat
                if(arguments.length == 1 && arguments[0] instanceof String){
                    Stat stat = this.map.getStatManager().getStat((String) arguments[0]);
                    if(stat != null && stat instanceof DefaultStat){
                        IStatType type = ((DefaultStat) stat).getType();
                        if(type instanceof StatTypeModifiable){
                            stat.enable();
                        }
                    }
                }
                break;
            case 16: //spreadPlayers
                for(Player player : this.map.getPlayers()){
                    Location spawn = this.map.getRandomSpawnpoint();
                    if(spawn == null){
                        throw new Exception("No random spawnpoints were found");
                    }
                    player.getEntity().setLocationAndAngles(spawn.getX(), spawn.getY(), spawn.getZ(), spawn.getYaw(), spawn.getPitch());
                }
                break;
            case 17: //tpAllToLobby
                TeleportOptions options = NailedAPI.getMapLoader().getLobby().getSpawnTeleport();
                Teleporter teleporter = NailedAPI.getTeleporter();
                for(Player player : this.map.getPlayers()){
                    teleporter.teleportEntity(player.getEntity(), options);
                }
                break;
            case 18: //remove
                break;
            case 19: //recycle
                break;
            case 20: //hasMappack
                return new Object[]{this.map.getMappack() != null};
            case 21: //onStarted
                this.map.getGameManager().onStarted();
                break;
            case 22: //onStopped
                if(arguments.length == 1 && arguments[0] instanceof Boolean){
                    this.map.getGameManager().onStopped((Boolean) arguments[0]);
                }else{
                    throw new Exception("Expected 1 boolean argument");
                }
                break;
            case 23: //stopGame
                this.map.getGameManager().stopGame();
                break;
            case 24: //setMaxFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player:this.map.getPlayers()){
                        ((NailedFoodStats) player.getEntity().getFoodStats()).setMaxFoodLevel(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
            case 25: //setMinFood
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player:this.map.getPlayers()){
                        ((NailedFoodStats) player.getEntity().getFoodStats()).setMinFoodLevel(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
            case 26:
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player:this.map.getPlayers()){
                        player.setMaxHealth(((Double)arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
            case 27:
                if(arguments.length == 1 && arguments[0] instanceof Double){
                    for(Player player:this.map.getPlayers()){
                        player.setMinHealth(((Double) arguments[0]).intValue());
                    }
                }else{
                    throw new Exception("Expected 1 integer argument");
                }
                break;
        }
        return null;
    }

    public static ILuaObject wrapPlayer(final IAPIEnvironment env, final Player player){
        if(player == null){
            return null;
        }
        return new ILuaObject() {
            @Override
            public String[] getMethodNames(){
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
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getUsername
                        return new Object[]{player.getUsername()};
                    case 1: //getTeam
                        return new Object[]{wrapTeam(env, player.getTeam())};
                    case 2: //clearInventory
                        return new Object[]{player.getEntity().inventory.clearInventory(null, -1)};
                    case 3: //setSpawn
                        if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                            Spawnpoint spawn = new Spawnpoint(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue());
                            player.setSpawnpoint(spawn);
                        }else if(arguments.length == 5 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double){
                            Spawnpoint spawn = new Spawnpoint(((Double) arguments[0]).intValue(), ((Double) arguments[1]).intValue(), ((Double) arguments[2]).intValue(), ((Double) arguments[3]).floatValue(), ((Double) arguments[4]).floatValue());
                            player.setSpawnpoint(spawn);
                        }else{
                            throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                        }
                        break;
                    case 4: //setGamemode
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.setGameMode(Gamemode.fromId(((Double) arguments[0]).intValue()));
                        }else{
                            throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                        }
                        break;
                    case 5: //setHealth
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            EntityPlayerMP entity = player.getEntity();
                            float newHealth = ((Double) arguments[0]).floatValue();
                            if(newHealth > 0) entity.deathTime = 0;
                            entity.setHealth(newHealth);
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 6: //setFood
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            FoodStats foodStats = player.getEntity().getFoodStats();
                            foodStats.addStats(((Double) arguments[0]).intValue() - foodStats.getFoodLevel(), 0);
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 7: //setExperience
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.getEntity().experienceLevel = ((Double) arguments[0]).intValue();
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 8: //getType
                        return new Object[]{"player"};
                    case 9: //freeze
                        if(arguments.length == 1 && arguments[0] instanceof Boolean){
                            boolean doFreeze = (Boolean) arguments[0];
                            EntityPlayerMP entity = player.getEntity();
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
                                player.sendChat(comp);
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
                            player.sendChat((String) arguments[0]);
                        }else{
                            throw new Exception("Expected 1 string argument");
                        }
                        break;
                    case 12: //addPotionEffect
                        EntityPlayerMP entity = player.getEntity();
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
                            player.getEntity().removePotionEffect(((Double) arguments[0]).intValue());
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 14: //sendTimeUpdate
                        if(arguments.length == 1 && arguments[0] instanceof String){
                            player.sendTimeUpdate((String) arguments[0]);
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
                            player.getEntity().inventory.addItemStackToInventory(stack);
                        }else if(arguments.length == 3 && arguments[0] instanceof String && arguments[1] instanceof Double && arguments[2] instanceof Double){
                            int meta = ((Double) arguments[2]).intValue();
                            int amount = ((Double) arguments[1]).intValue();
                            String item = (String) arguments[0];
                            Object i = Item.itemRegistry.getObject(item);
                            if(i == null){
                                throw new Exception("Unknown item " + item);
                            }
                            ItemStack stack = new ItemStack((Item) i, amount, meta);
                            player.getEntity().inventory.addItemStackToInventory(stack);
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
                            player.getEntity().inventory.setInventorySlotContents(slot, stack);
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
                            player.getEntity().inventory.setInventorySlotContents(slot, stack);
                        }else{
                            throw new Exception("Expected 1 int, 1 string and then 1 or 2 int arguments");
                        }
                        break;
                    case 17: // setMinFood
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            ((NailedFoodStats) player.getEntity().getFoodStats()).setMinFoodLevel(((Double)arguments[0]).intValue());
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 18: // setMinHealth
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.setMinHealth(((Double)arguments[0]).intValue());
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 19: // setMaxFood
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            ((NailedFoodStats)player.getEntity().getFoodStats()).setMaxFoodLevel(((Double) arguments[0]).intValue());
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 20: // setMaxHealth
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            player.setMaxHealth(((Double) arguments[0]).intValue());
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                }
                return null;
            }
        };
    }

    public static ILuaObject wrapTeam(final IAPIEnvironment env, final Team team){
        if(team == null){
            return null;
        }
        return new ILuaObject() {
            @Override
            public String[] getMethodNames(){
                return new String[]{
                        "getName",
                        "getPlayers",
                        "forEachPlayer",
                        "setSpawn",
                        "getType",
                        "getID",
                        "setMinFood",
                        "setMinHealth",
                        "setMaxFood",
                        "setMaxHealth"
                };
            }

            @Override
            public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception{
                switch(method){
                    case 0: //getName
                        return new Object[]{team.getName()};
                    case 1: //getPlayers
                        List<Player> players = team.getMembers();
                        java.util.Map<Integer, ILuaObject> table = Maps.newHashMap();
                        for(int i = 0; i < players.size(); i++){
                            table.put(i + 1, wrapPlayer(env, players.get(i)));
                        }
                        return new Object[]{table};
                    case 2: //forEachPlayer
                        if(arguments.length == 1 && arguments[0] instanceof LuaClosure){
                            LuaClosure closure = (LuaClosure) arguments[0];
                            LuaMachine machine = env.getMachine().getLuaMachine();
                            List<Player> players1 = team.getMembers();
                            for(int i = 0; i < players1.size(); i++){
                                closure.invoke(LuaValue.varargsOf(machine.toValues(new Object[]{wrapPlayer(env, players1.get(i))}, 0)));
                            }
                        }else{
                            throw new Exception("Excpected 1 function as argument");
                        }
                        break;
                    case 3: //setSpawn
                        if(arguments.length == 3 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double){
                            Location spawn = new Location((Double) arguments[0], (Double) arguments[1], (Double) arguments[2]);
                            team.setSpawnpoint(spawn);
                        }else if(arguments.length == 5 && arguments[0] instanceof Double && arguments[1] instanceof Double && arguments[2] instanceof Double && arguments[3] instanceof Double && arguments[4] instanceof Double){
                            Location spawn = new Location((Double) arguments[0], (Double) arguments[1], (Double) arguments[2], ((Double) arguments[3]).floatValue(), ((Double) arguments[4]).floatValue());
                            team.setSpawnpoint(spawn);
                        }else{
                            throw new Exception("Expected 3 int arguments, and 2 optional float arguments");
                        }
                        break;
                    case 4: //getType
                        return new Object[]{"team"};
                    case 5: //getID
                        return new Object[]{team.getTeamId()};
                    case 6: // setMinFood
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            for(Player player : team.getMembers()) {
                                ((NailedFoodStats) player.getEntity().getFoodStats()).setMinFoodLevel(((Double) arguments[0]).intValue());
                            }
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 7: // setMinHealth
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            for(Player player : team.getMembers()) {
                                player.setMinHealth(((Double) arguments[0]).intValue());
                            }
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 8: // setMaxFood
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            for(Player player : team.getMembers()) {
                                ((NailedFoodStats) player.getEntity().getFoodStats()).setMaxFoodLevel(((Double) arguments[0]).intValue());
                            }
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                    case 9: // setMaxHealth
                        if(arguments.length == 1 && arguments[0] instanceof Double){
                            for(Player player : team.getMembers()) {
                                player.setMaxHealth(((Double) arguments[0]).intValue());
                            }
                        }else{
                            throw new Exception("Expected 1 int argument");
                        }
                        break;
                }
                return null;
            }
        };
    }
}
