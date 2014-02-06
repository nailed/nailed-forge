package jk_5.nailed.permissions;

import com.google.common.collect.Sets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
@Getter
@RequiredArgsConstructor
public class Group {

    private final Set<String> permissions = Sets.newTreeSet();
    private final String name;

    @Setter @GroupOption("prefix") private String prefix = "";
    @Setter @GroupOption("suffix") private String suffix = "";
    @Setter @GroupOption("default") private boolean isDefault = false;

    public void addPermission(String node){
        if(this.permissions.contains(node)) return;
        this.permissions.add(node);
    }
}
