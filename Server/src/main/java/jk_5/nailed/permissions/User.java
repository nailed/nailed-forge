package jk_5.nailed.permissions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;
import net.minecraftforge.permissions.api.UnregisterredPermissionException;

import java.util.List;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class User {

    private final Map<String, Boolean> permissions = Maps.newHashMap();
    private final List<Group> groups = Lists.newArrayList();
    private final String name;

    public User(String name) {
        this.name = name;
    }

    public RegisteredPermValue getPermissionLevel(String node){
        return this.getPermissionLevel(node, ((NailedPermissionFactory) PermissionsManager.getPermFactory()).getPerms().get(node));
    }

    public Group getMainGroup(){
        return this.groups.size() > 0 ? this.groups.get(this.groups.size() - 1) : null;
    }

    private RegisteredPermValue getPermissionLevel(String node, RegisteredPermValue def){
        if(def == null){
            throw new UnregisterredPermissionException(node);
        }
        for(Group group : this.groups){
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

    public boolean hasPermission(String node){
        RegisteredPermValue value = this.getPermissionLevel(node);
        if(value == RegisteredPermValue.FALSE) return false;
        if(value == RegisteredPermValue.TRUE) return true;
        if(value == RegisteredPermValue.OP) return this.isOp();
        if(value == RegisteredPermValue.NONOP) return !this.isOp();
        return false;
    }

    public boolean isOp(){
        return NailedPermissionFactory.isOp(this.name);
    }

    Map<String, Boolean> getPermissions() {
        return this.permissions;
    }

    public List<Group> getGroups() {
        return this.groups;
    }

    public String getName() {
        return this.name;
    }
}
