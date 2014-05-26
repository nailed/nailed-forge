package jk_5.nailed.permissions;

import java.util.*;

import com.google.common.collect.*;

import net.minecraftforge.permissions.api.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class Group {

    private final Map<String, Boolean> permissions = Maps.newHashMap();
    private final List<Group> inheritions = Lists.newArrayList();
    private final String name;

    @GroupOption("prefix")
    private String prefix = "";
    @GroupOption("suffix")
    private String suffix = "";
    @GroupOption("default")
    private boolean isDefault = false;

    public Group(String name) {
        this.name = name;
    }

    public RegisteredPermValue hasPermission(String node) {
        return this.hasPermission(node, ((NailedPermissionFactory) PermissionsManager.getPermFactory()).getPerms().get(node));
    }

    RegisteredPermValue hasPermission(String node, RegisteredPermValue def) {
        if(def == null){
            throw new UnregisterredPermissionException(node);
        }
        for(Group group : this.inheritions){
            def = group.hasPermission(node, def);
        }
        Boolean allowed = this.permissions.get(node);
        if(allowed != null){
            if(allowed){
                return RegisteredPermValue.TRUE;
            }else{
                return RegisteredPermValue.FALSE;
            }
        }
        return def;
    }

    Map<String, Boolean> getPermissions() {
        return this.permissions;
    }

    public List<Group> getInheritions() {
        return this.inheritions;
    }

    public String getName() {
        return this.name;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault = isDefault;
    }
}
