package jk_5.nailed.gradle;

import argo.jdom.JsonNode;
import argo.jdom.JsonRootNode;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import jk_5.nailed.gradle.common.BasePlugin;
import jk_5.nailed.gradle.delayed.DelayedBase;
import jk_5.nailed.gradle.extension.LauncherExtension;
import jk_5.nailed.gradle.extension.NailedExtension;
import jk_5.nailed.gradle.tasks.DeployLauncherProfileTask;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.dsl.DependencyHandler;

import java.io.File;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedPlugin extends BasePlugin implements DelayedBase.IDelayedResolver {

    private boolean jsonApplied = false;

    @Override
    public void applyPlugin() {

        this.applyExternalPlugin("forge");
        this.applyExternalPlugin("maven");

        this.getProject().getExtensions().create(Constants.NAILED_EXTENSION, NailedExtension.class, this.getProject());
        this.getProject().getExtensions().create(Constants.NAILED_LAUNCHER_EXTENSION, LauncherExtension.class, this.getProject());

        this.registerLauncherTasks();
    }

    @Override
    public void afterEvaluate() {
        super.afterEvaluate();

        if (delayedFile(Constants.NAILED_JSON).call().exists()){
            //this.readAndApplyJson(delayedFile(Constants.NAILED_JSON).call());
        }
    }

    public void registerLauncherTasks(){
        DeployLauncherProfileTask task = this.makeTask("deployLauncherProfile", DeployLauncherProfileTask.class);
    }

    @Override
    public String resolve(String pattern, Project project) {
        pattern = pattern.replace("{JSON_FILE}", project.getExtensions().getByType(NailedExtension.class).getJsonLocation());
        return pattern;
    }

    public void readAndApplyJson(File file){
        if(this.jsonApplied || true) return;

        List<String> libs = Lists.newArrayList();

        try{
            Reader reader = Files.newReader(file, Charset.defaultCharset());
            JsonRootNode root = Constants.PARSER.parse(reader);

            for (JsonNode node : root.getArrayNode("libraries")){
                String dep = node.getStringValue("name");
                if (!dep.contains("_fixed")) libs.add(dep);
            }
            reader.close();

            DependencyHandler handler = this.getProject().getDependencies();
            if (this.getProject().getConfigurations().getByName(Constants.DEPENDENCY_CONFIG).getState() == Configuration.State.UNRESOLVED){
                for (String dep : libs){
                    handler.add(Constants.DEPENDENCY_CONFIG, dep);
                }
            }
            this.jsonApplied = true;
        }catch (Exception e){
            throw Throwables.propagate(e);
        }
    }
}
