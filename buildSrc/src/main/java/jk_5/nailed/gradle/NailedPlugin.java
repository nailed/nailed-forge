package jk_5.nailed.gradle;

import com.beust.jcommander.internal.Maps;
import jk_5.nailed.gradle.tasks.DeployLauncherProfileTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getExtensions().create("nailedLauncher", LauncherExtension.class, project);
    }

    public void registerLauncherTasks(Project project){
        DeployLauncherProfileTask task = makeTask(project, "deployLauncherProfile", DeployLauncherProfileTask.class);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Task> T makeTask(Project proj, String name, Class<T> type){
        Map<String, Object> map = Maps.newHashMap();
        map.put("name", name);
        map.put("type", type);
        return (T) proj.task(map, name);
    }
}
