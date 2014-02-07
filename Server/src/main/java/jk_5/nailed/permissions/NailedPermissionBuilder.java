package jk_5.nailed.permissions;

import lombok.RequiredArgsConstructor;
import net.minecraftforge.permissions.api.PermBuilder;
import net.minecraftforge.permissions.api.context.IContext;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public class NailedPermissionBuilder implements PermBuilder<NailedPermissionBuilder> {

    private final NailedPermissionFactory factory;

    private IContext user;
    private IContext target;
    private String username;
    private String node;
    private User userObj;

    @Override
    public boolean check(){
        if(this.userObj == null){
            this.userObj = this.factory.getUserInfo(this.username);
        }
        return this.userObj.hasPermission(this.node);
    }

    @Override
    public NailedPermissionBuilder setUserName(String name){
        this.username = name;
        return this;
    }

    @Override
    public NailedPermissionBuilder setPermNode(String node){
        this.node = node;
        return this;
    }

    @Override
    public NailedPermissionBuilder setTargetContext(IContext context){
        this.target = context;
        return this;
    }

    @Override
    public NailedPermissionBuilder setUserContext(IContext context){
        this.user = context;
        return this;
    }
}
