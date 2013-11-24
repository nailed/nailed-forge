package jk_5.nailed.gradle.tasks;

import com.google.gson.*;
import jk_5.nailed.gradle.Constants;
import jk_5.nailed.gradle.delayed.DelayedString;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;

/**
 * No description given
 *
 * @author jk-5
 */
public class DeployLauncherProfileTask extends DefaultTask {

    @TaskAction
    public void doTask() throws IOException {
        URL url = new URL(new DelayedString(this.getProject(), Constants.FML_JSON_URL).call());
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(5000);
        Reader reader = new InputStreamReader(conn.getInputStream());
        JsonElement parsed = new JsonParser().parse(reader);
        JsonArray libs = parsed.getAsJsonObject().getAsJsonObject("versionInfo").getAsJsonArray("libraries");
        JsonArray newArray = new JsonArray();
        for(JsonElement element : libs){
            JsonObject object = element.getAsJsonObject();
            if(!object.get("name").getAsString().equals("@artifact@")){
                JsonObject newObject = new JsonObject();
                newObject.addProperty("name", object.get("name").getAsString());
                if(object.has("url")) newObject.addProperty("url", object.get("url").getAsString());
                newArray.add(newObject);
            }
        }
        System.out.println(new Gson().toJson(newArray));
        reader.close();
    }
}
