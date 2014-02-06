package jk_5.nailed.permissions;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import cpw.mods.fml.common.FMLCommonHandler;
import jk_5.nailed.NailedLog;
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
import net.minecraftforge.permissions.api.context.*;

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
    private final Multimap<String, String> allowedPerms = TreeMultimap.create();
    private final Set<Group> groups = Sets.newHashSet();
    private final Set<String> perms = Sets.newTreeSet();

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
        return new NailedPermissionBuilder();
    }

    @Override
    public NailedPermissionBuilder builder(String username, String permNode){
        return new NailedPermissionBuilder().setUserName(username).setPermNode(permNode);
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
            this.perms.add(perm.key);
        }
    }

    public void readConfig(){
        this.groups.clear();
        BufferedReader reader;
        File groupsFile = new File(configDir, "groups.cfg");
        try{
            Group group = null;
            boolean readingPerms = false;
            reader = new BufferedReader(new FileReader(groupsFile));
            int lineNumber = 0;
            while(true){
                String line = reader.readLine();
                lineNumber++;
                if(line == null){
                    break;
                }else if(line.startsWith("#") || line.trim().isEmpty()){
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
                    }else if(line.contains("Permissions") && line.contains("{")){
                        readingPerms = true;
                    }else if(line.contains("}") && readingPerms){
                        readingPerms = false;
                    }else if(readingPerms){
                        String node = line.trim();
                        if(node.endsWith("*")){
                            String base = node.substring(0, node.indexOf('*'));
                            for(String perm : this.perms){
                                if(perm.startsWith(base)){
                                    group.addPermission(perm);
                                }
                            }
                        }else if(node.startsWith("-") && group.getPermissions().contains(node.substring(1))){
                            group.getPermissions().remove(node.substring(1));
                        }else{
                            group.addPermission(line.trim());
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
        }

        NailedLog.info("Permission parse info:");
        for(Group group : this.groups){
            NailedLog.info("-- Group " + group.getName());
            NailedLog.info("-- " + group.getName() + " -- " + group.getPrefix());
            NailedLog.info("-- " + group.getName() + " -- " + group.getSuffix());
            NailedLog.info("-- " + group.getName() + " -- " + group.isDefault());
            NailedLog.info("-- " + group.getName() + " -- Permissions:");
            for(String perm : group.getPermissions()){
                NailedLog.info("-- " + group.getName() + " ----- " + perm);
            }
        }
    }

    private boolean isRegistered(String node){
        return this.perms.contains(node);
    }

    private static boolean isOp(String username){
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
