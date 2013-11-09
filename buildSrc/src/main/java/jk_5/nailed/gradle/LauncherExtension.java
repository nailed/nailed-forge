package jk_5.nailed.gradle;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Project;

/**
 * No description given
 *
 * @author jk-5
 */
public class LauncherExtension {

    private final Project project;

    @Getter
    @Setter
    private String version = "null";

    public LauncherExtension(Project project){
        this.project = project;
    }
}
