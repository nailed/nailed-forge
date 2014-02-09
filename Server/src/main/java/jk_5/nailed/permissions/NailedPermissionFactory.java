package jk_5.nailed.permissions;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.player.Player;
import lombok.Getter;
import net.minecraft.dispenser.ILocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.permissions.api.PermBuilderFactory;
import net.minecraftforge.permissions.api.PermReg;
import net.minecraftforge.permissions.api.RegisteredPermValue;
import net.minecraftforge.permissions.api.context.*;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPermissionFactory implements PermBuilderFactory<NailedPermissionBuilder> {

    private static final File configDir = new File("permissions");
    private static final IContext GLOBAL = new IContext() {};
    private static final Map<String, Field> groupOptions = Maps.newHashMap();
    private final Set<Group> groups = Sets.newHashSet();
    private final Map<String, User> users = Maps.newHashMap();
    private Group defaultGroup;
    @Getter private final Map<String, RegisteredPermValue> perms = Maps.newHashMap();

    static {
        configDir.mkdirs();
        for(Field field : Group.class.getDeclaredFields()){
            for(Annotation annotation : field.getDeclaredAnnotations()){
                if(annotation.annotationType() == GroupOption.class){
                    GroupOption option = (GroupOption) annotation;
                    field.setAccessible(true);
                    groupOptions.put(option.value().toLowerCase(), field);
                }
            }
        }
    }

    @Override
    public NailedPermissionBuilder builder(){
        return new NailedPermissionBuilder(this);
    }

    @Override
    public NailedPermissionBuilder builder(String username, String permNode){
        return new NailedPermissionBuilder(this).setUserName(username).setPermNode(permNode);
    }

    @Override
    public IContext getDefaultContext(EntityPlayer player){
        return new PlayerContext(player);
    }

    @Override
    public IContext getDefaultContext(TileEntity te){
        return new TileEntityContext(te);
    }

    @Override
    public IContext getDefaultContext(ILocation loc){
        return new Point(loc);
    }

    @Override
    public IContext getDefaultContext(Entity entity){
        return new EntityContext(entity);
    }

    @Override
    public IContext getDefaultContext(World world){
        return new WorldContext(world);
    }

    @Override
    public IContext getGlobalContext(){
        return GLOBAL;
    }

    @Override
    public IContext getDefaultContext(Object object){
        if(object instanceof EntityLivingBase){
            return new EntityLivingContext((EntityLivingBase) object);
        }else{
            return GLOBAL;
        }
    }

    @Override
    public void registerPermissions(List<PermReg> perms){
        for(PermReg perm : perms){
            if(this.isRegistered(perm.key)){
                continue;
            }
            this.perms.put(perm.key, perm.role);
        }
    }

    public void readConfig(){
        this.groups.clear();
        this.users.clear();
        this.defaultGroup = null;
        Multimap<Group, String> inheritions = ArrayListMultimap.create();
        BufferedReader reader = null;
        File groupsFile = new File(configDir, "groups.cfg");
        try{
            Group group = null;
            boolean readingPerms = false;
            boolean readingInheritions = false;
            reader = new BufferedReader(new FileReader(groupsFile));
            int lineNumber = 0;
            while(true){
                String line = reader.readLine();
                lineNumber++;
                if(line == null){
                    break;
                }else if(line.trim().startsWith("#") || line.trim().isEmpty()){
                }else if(group == null && line.contains("{")){
                    String name = line.substring(0, line.indexOf('{')).trim();
                    group = new Group(name);
                }else if(group != null){
                    if(line.contains("=")){
                        String key = line.substring(0, line.indexOf('=')).trim();
                        String value = line.substring(line.indexOf('=') + 1).trim();
                        boolean isBoolean = false;
                        boolean val = false;
                        if(value.startsWith("\"") && value.endsWith("\"")){
                            value = value.substring(1, value.length() - 1);
                        }else{
                            if(value.equalsIgnoreCase("true")){
                                isBoolean = true;
                                val = true;
                            }else if(value.equalsIgnoreCase("false")){
                                isBoolean = true;
                                val = false;
                            }
                        }
                        Field field = groupOptions.get(key.toLowerCase());
                        if(field == null){
                            throw new ConfigParseException("Invalid group property \"" + key + "\" on line " + lineNumber + " in " + groupsFile.getAbsolutePath());
                        }
                        if(isBoolean){
                            field.set(group, val);
                        }else{
                            field.set(group, value);
                        }
                        if(key.equalsIgnoreCase("default") && isBoolean && val){
                            this.defaultGroup = group;
                        }
                    }else if(line.contains("Permissions") && line.contains("{")){
                        readingPerms = true;
                    }else if(line.contains("}") && readingPerms){
                        readingPerms = false;
                    }else if(line.contains("Inherits") && line.contains("{")){
                        readingInheritions = true;
                    }else if(line.contains("}") && readingInheritions){
                        readingInheritions = false;
                    }else if(readingInheritions){
                        inheritions.put(group, line.trim());
                    }else if(readingPerms){
                        String node = line.trim();
                        boolean allowed = !node.startsWith("-");
                        if(!allowed){
                            node = node.substring(1);
                        }
                        if(node.endsWith("*")){
                            String base = node.substring(0, node.indexOf('*'));
                            for(Map.Entry<String, RegisteredPermValue> perm : this.perms.entrySet()){
                                if(perm.getKey().startsWith(base)){
                                    group.getPermissions().put(perm.getKey(), allowed);
                                }
                            }
                        }else{
                            group.getPermissions().put(node, allowed);
                        }
                    }else if(line.contains("}")){
                        this.groups.add(group);
                        group = null;
                    }
                }
            }
        }catch(ConfigParseException e){
            NailedLog.error(e.getMessage());
        }catch(Exception e){
            NailedLog.error(e, "Error while parsing groups.cfg file");
        }finally{
            IOUtils.closeQuietly(reader);
        }

        //Now all the groups are read, try to resolve all the inheritions
        Map<String, Group> groupNames = Maps.newHashMap();
        for(Group group : this.groups){
            groupNames.put(group.getName().toLowerCase(), group);
        }
        for(Group group : this.groups){
            for(String name : inheritions.get(group)){
                group.getInheritions().add(groupNames.get(name.toLowerCase()));
            }
        }

        File usersFile = new File(configDir, "users.cfg");
        try{
            reader = new BufferedReader(new FileReader(usersFile));
            User user = null;
            int lineNumber = 0;
            boolean readingPerms = false;
            boolean readingGroups = false;
            while(true){
                String line = reader.readLine();
                lineNumber++;
                if(line == null){
                    break;
                }else if(line.trim().startsWith("#") || line.trim().isEmpty()){
                }else if(user == null && line.contains("{")){
                    String name = line.substring(0, line.indexOf('{')).trim();
                    user = new User(name);
                }else if(user != null){
                    if(line.contains("Permissions") && line.contains("{")){
                        readingPerms = true;
                    }else if(line.contains("}") && readingPerms){
                        readingPerms = false;
                    }else if(line.contains("Groups") && line.contains("{")){
                        readingGroups = true;
                    }else if(line.contains("}") && readingGroups){
                        readingGroups = false;
                    }else if(readingGroups){
                        Group group = groupNames.get(line.trim().toLowerCase());
                        if(group == null) throw new ConfigParseException("Group \"" + line.trim() + "\" does not exist, on line " + lineNumber + " in " + usersFile.getAbsolutePath());
                        user.getGroups().add(group);
                    }else if(readingPerms){
                        String node = line.trim();
                        boolean allowed = !node.startsWith("-");
                        if(!allowed){
                            node = node.substring(1);
                        }
                        if(node.endsWith("*")){
                            String base = node.substring(0, node.indexOf('*'));
                            for(Map.Entry<String, RegisteredPermValue> perm : this.perms.entrySet()){
                                if(perm.getKey().startsWith(base)){
                                    user.getPermissions().put(perm.getKey(), allowed);
                                }
                            }
                        }else{
                            user.getPermissions().put(node, allowed);
                        }
                    }else if(line.contains("}")){
                        this.users.put(user.getName(), user);
                        user = null;
                    }
                }
            }
        }catch(ConfigParseException e){
            NailedLog.error(e.getMessage());
        }catch(Exception e){
            NailedLog.error(e, "Error while parsing groups.cfg file");
        }finally{
            IOUtils.closeQuietly(reader);
        }

        //Now set users that don't have a group to the default group, if we have one
        if(this.defaultGroup != null){
            for(User user : this.users.values()){
                if(user.getGroups().isEmpty()){
                    user.getGroups().add(this.defaultGroup);
                }
            }
        }

        for(Player player : NailedAPI.getPlayerRegistry().getPlayers()){
            player.getEntity().refreshDisplayName();
        }
    }

    public User getUserInfo(String username){
        User ret = this.users.get(username);
        if(ret == null){ //This user is not listed in the config file. Create one with the default group
            ret = new User(username);
            if(this.defaultGroup != null){
                ret.getGroups().add(this.defaultGroup);
            }
            this.users.put(username, ret);
        }
        return ret;
    }

    private boolean isRegistered(String node){
        return this.perms.containsKey(node);
    }

    public static boolean isOp(String username){
        MinecraftServer server = FMLCommonHandler.instance().getSidedDelegate().getServer();

        if (server.isSinglePlayer()){
            if (server instanceof IntegratedServer){
                return server.getServerOwner().equalsIgnoreCase(username);
            }else{
                return server.getConfigurationManager().getOps().contains(username);
            }
        }

        return server.getConfigurationManager().getOps().contains(username);
    }
}
