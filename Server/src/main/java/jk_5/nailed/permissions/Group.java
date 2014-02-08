package jk_5.nailed.permissions;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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
@RequiredArgsConstructor
public class Group {

    @Getter(AccessLevel.PACKAGE) private final Map<String, Boolean> permissions = Maps.newHashMap();
    @Getter private final List<Group> inheritions = Lists.newArrayList();
    @Getter private final String name;

    @Getter @Setter @GroupOption("prefix") private String prefix = "";
    @Getter @Setter @GroupOption("suffix") private String suffix = "";
    @Getter @Setter @GroupOption("default") private boolean isDefault = false;

    public RegisteredPermValue hasPermission(String node){
        return this.hasPermission(node, ((NailedPermissionFactory) PermissionsManager.getPermFactory()).getPerms().get(node));
    }

    RegisteredPermValue hasPermission(String node, RegisteredPermValue def){
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
}