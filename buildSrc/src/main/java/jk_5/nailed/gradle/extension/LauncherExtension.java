package jk_5.nailed.gradle.extension;

import lombok.Getter;
import lombok.Setter;
import org.gradle.api.Project;
import org.gradle.api.internal.artifacts.configurations.DefaultConfiguration;

/**
 * No description given
 *
 * @author jk-5
 */
public class LauncherExtension {

    public static LauncherExtension getInstance(Project project){
        return project.getExtensions().getByType(LauncherExtension.class);
    }

    private final Project project;

    @Getter
    @Setter
    private String version = "null";

    public LauncherExtension(Project project){
        this.project = project;
    }
}
