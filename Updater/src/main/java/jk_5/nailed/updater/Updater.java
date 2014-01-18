package jk_5.nailed.updater;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class Updater {

    private static final String SERVER = "http://maven.reening.nl/";
    private static final String VERSIONS_URL = SERVER + "nailed/versions.json";

    private static final Gson gson = new Gson();
    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser parser = new JsonParser();

    public static void main(String args[]){
        UpdatingTweaker.logger.info("Updated: " + checkForUpdates());
    }

    public static boolean checkForUpdates(){
        UpdatingTweaker.logger.info("Checking for updates...");

        JsonObject remote = readRemoteVersionData();
        JsonObject local = readLocalVersionData();
        MapDifference diff = Maps.difference(entrySetToMap(local.entrySet()), entrySetToMap(remote.entrySet()));

        boolean updated = false;

        if(diff.entriesOnlyOnLeft().size() > 0){
            UpdatingTweaker.logger.info("Found files that could be removed locally:");
            Set<Map.Entry<String, JsonObject>> entries = diff.entriesOnlyOnLeft().entrySet();
            for(Map.Entry<String, JsonObject> e : entries){
                UpdatingTweaker.logger.info("Removing " + e.getKey());
                File dest = resolve(e.getValue().get("destination").getAsString());
                if(dest.isFile()) dest.delete();
                File checksum = new File(dest.getAbsolutePath() + ".sha");
                if(checksum.isFile()) checksum.delete();
                updated = true;
                local.remove(e.getKey());
            }
        }
        if(diff.entriesOnlyOnRight().size() > 0){
            UpdatingTweaker.logger.info("Found files that where added. Downloading them...");
            Set<Map.Entry<String, JsonObject>> entries = diff.entriesOnlyOnRight().entrySet();
            for(Map.Entry<String, JsonObject> e : entries){
                boolean u = updateFile(e.getValue(), e.getKey());
                updated |= u;
                if(u){
                    local.add(e.getKey(), e.getValue());
                }
            }
        }
        if(diff.entriesDiffering().size() > 0){
            UpdatingTweaker.logger.info("Found files that are differing from remote. Checking them...");
            Set<Map.Entry<String, MapDifference.ValueDifference<JsonObject>>> entries = diff.entriesDiffering().entrySet();
            for(Map.Entry<String, MapDifference.ValueDifference<JsonObject>> e : entries){
                UpdatingTweaker.logger.info("Checking " + e.getKey());
                int localRev = e.getValue().leftValue().get("rev").getAsInt();
                int remoteRev = e.getValue().rightValue().get("rev").getAsInt();
                UpdatingTweaker.logger.info("  Local rev: " + localRev);
                UpdatingTweaker.logger.info("  Remote rev: " + remoteRev);
                if(remoteRev > localRev){
                    UpdatingTweaker.logger.info("Remote has newer version than we have. Redownloading...");
                    File dest = resolve(e.getValue().leftValue().get("destination").getAsString());
                    if(dest.isFile()) dest.delete();
                    File checksum = new File(dest.getAbsolutePath() + ".sha");
                    if(checksum.isFile()) checksum.delete();
                    updateFile(e.getValue().rightValue(), e.getKey());
                    updated = true;
                    local.remove(e.getKey());
                    local.add(e.getKey(), e.getValue().rightValue());
                }
            }
        }

        if(updated){
            Writer writer = null;
            try{
                writer = new FileWriter(new File("nailedVersions.json"));
                prettyGson.toJson(local, writer);
            }catch(Exception e){
                //NOOP
            }finally{
                IOUtils.closeQuietly(writer);
            }
        }

        return updated;
    }

    private static boolean updateFile(JsonObject object, String name){
        try{
            UpdatingTweaker.logger.info("Downloading " + name);
            File dest = resolve(object.get("destination").getAsString());
            FileUtils.copyURLToFile(new URL(SERVER + object.get("location").getAsString()), dest, 20000, 20000);
            File checksum = new File(dest.getAbsolutePath() + ".sha");
            FileWriter writer = new FileWriter(checksum);
            writer.write(getSHA1(dest));
            writer.close();
            return true;
        }catch(Exception e){
            UpdatingTweaker.logger.error("Error while updating file " + name, e);
        }
        return false;
    }

    private static File resolve(String input){
        String dir = getMinecraftFolder().getAbsolutePath() + "/" + input;
        if(input.startsWith("{MC_GAME_DIR}")){
            dir = input.replace("{MC_GAME_DIR}", stripTrailing(UpdatingTweaker.gameDir.getAbsolutePath()));
        }
        if(input.startsWith("{MC_ASSET_DIR}")){
            dir = input.replace("{MC_ASSET_DIR}", stripTrailing(UpdatingTweaker.assetsDir.getAbsolutePath()));
        }
        dir = dir.replace("{MC_LIB_DIR}", "libraries");
        dir = dir.replace("{MC_VERSION_DIR}", "versions/" + UpdatingTweaker.name);
        dir = dir.replace("{MC_VERSION_NAME}", UpdatingTweaker.name);
        return new File(dir);
    }

    private static String stripTrailing(String in){
        if(in.endsWith("/")){
            return in.substring(0, in.length() - 1);
        }else return in;
    }

    private static JsonObject readRemoteVersionData(){
        Reader reader = null;
        JsonObject ret = null;
        try{
            URL url = new URL(VERSIONS_URL);
            reader = new InputStreamReader(url.openStream());
            ret = parser.parse(reader).getAsJsonObject();
        }catch(Exception e){
            UpdatingTweaker.logger.error("Exception while reading remote version data", e);
            ret = new JsonObject();
        }finally{
            IOUtils.closeQuietly(reader);
        }
        return ret;
    }

    private static JsonObject readLocalVersionData(){
        Reader reader = null;
        JsonObject ret = null;
        try{
            File file = new File("nailedVersions.json");
            if(file.exists()){
                reader = new FileReader(file);
                ret = parser.parse(reader).getAsJsonObject();
            }else{
                ret = new JsonObject();
            }
        }catch(Exception e){
            UpdatingTweaker.logger.error("Exception while reading local version data", e);
            ret = new JsonObject();
        }finally{
            IOUtils.closeQuietly(reader);
        }
        return ret;
    }

    private static  <K, V> Map<K, V> entrySetToMap(Set<Map.Entry<K, V>> entrySet){
        Map<K, V> map = Maps.newHashMap();
        for(Map.Entry<K, V> e : entrySet){
            map.put(e.getKey(), e.getValue());
        }
        return map;
    }

    private static File getMinecraftFolder(){
        String userHomeDir = System.getProperty("user.home", ".");
        String osType = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String mcDir = ".minecraft";
        if(osType.contains("win") && System.getenv("APPDATA") != null){
            return new File(System.getenv("APPDATA"), mcDir);
        }else if(osType.contains("mac")){
            return new File(new File(new File(userHomeDir, "Library"), "Application Support"), "minecraft");
        }else{
            return new File(userHomeDir, mcDir);
        }
    }

    public static String getSHA1(File file){
        return getDigest(file, "SHA-1", 40);
    }

    public static String getDigest(File file, String algorithm, int hashLength){
        DigestInputStream stream = null;
        try{
            stream = new DigestInputStream(new FileInputStream(file), MessageDigest.getInstance(algorithm));
            int read;
            byte[] buffer = new byte[65536];
            do{
                read = stream.read(buffer);
            }while(read > 0);
        }catch(Exception ignored){
            return null;
        }finally{
            IOUtils.closeQuietly(stream);
        }

        return String.format("%1$0" + hashLength + "x", new BigInteger(1, stream.getMessageDigest().digest()));
    }
}
