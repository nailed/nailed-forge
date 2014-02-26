/*package jk_5.nailed.updater;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.gson.*;
import lombok.Getter;
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
 *//*
public class Updater {

    private static final String SERVER = "http://maven.reening.nl/";
    private static final String VERSIONS_URL = SERVER + "nailed/versions.json";

    private static final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();
    private static final JsonParser parser = new JsonParser();

    @Getter
    private static int restartLevel = 0; //0 = no restart, 1 = game, 2 = launcher
    private static boolean cleanModsFolder = true;

    public static void main(String args[]){
        System.out.println("Updated: " + checkForUpdates());
    }

    public static boolean checkForUpdates(){
        System.out.println("Checking for updates...");

        DownloadMonitor monitor = new DownloadMonitor();
        int progress = 0;

        JsonObject remote = readRemoteVersionData();
        JsonObject local = readLocalVersionData();

        if(cleanModsFolder){
            monitor.setNote("Cleaning up the mods folder...");
            File modsFolder = resolve("{MC_GAME_DIR}/mods/");
            if(!modsFolder.exists()) modsFolder.mkdir();
            for(File file : modsFolder.listFiles()){
                file.delete();
            }
            for(Map.Entry<String, JsonElement> e : local.entrySet()){
                if(e.getValue().getAsJsonObject().get("destination").getAsString().contains("{MC_GAME_DIR}/mods/")){
                    e.getValue().getAsJsonObject().remove("rev");
                    e.getValue().getAsJsonObject().addProperty("rev", -1);
                }
            }
        }

        MapDifference diff = Maps.difference(entrySetToMap(local.entrySet()), entrySetToMap(remote.entrySet()));

        monitor.setMaximum(diff.entriesOnlyOnLeft().size() + diff.entriesOnlyOnRight().size() + diff.entriesDiffering().size());

        boolean updated = false;

        if(diff.entriesOnlyOnLeft().size() > 0){
            System.out.println("Found files that could be removed locally:");
            monitor.setNote("Checking removable files");
            Set<Map.Entry<String, JsonObject>> entries = diff.entriesOnlyOnLeft().entrySet();
            for(Map.Entry<String, JsonObject> e : entries){
                monitor.setNote("Removing " + e.getKey());
                System.out.println("Removing " + e.getKey());
                File dest = resolve(e.getValue().get("destination").getAsString());
                if(dest.isFile()) dest.delete();
                File checksum = new File(dest.getAbsolutePath() + ".sha");
                if(checksum.isFile()) checksum.delete();
                updated = true;
                if(e.getValue().has("restart")){
                    String restart = e.getValue().get("restart").getAsString();
                    if(restart.equals("game") && restartLevel <= 1){
                        restartLevel = 1;
                    }else if(restart.equals("launcher") && restartLevel <= 2){
                        restartLevel = 2;
                    }
                }
                local.remove(e.getKey());
                monitor.setProgress(progress++);
            }
        }
        if(diff.entriesOnlyOnRight().size() > 0){
            System.out.println("Found files that where added. Downloading them...");
            monitor.setNote("Checking added files");
            Set<Map.Entry<String, JsonObject>> entries = diff.entriesOnlyOnRight().entrySet();
            for(Map.Entry<String, JsonObject> e : entries){
                monitor.setNote("Downloading " + e.getKey());
                boolean u = updateFile(e.getValue(), e.getKey());
                updated |= u;
                if(u){
                    local.add(e.getKey(), e.getValue());
                    if(e.getValue().has("restart")){
                        String restart = e.getValue().get("restart").getAsString();
                        if(restart.equals("game") && restartLevel <= 1){
                            restartLevel = 1;
                        }else if(restart.equals("launcher") && restartLevel <= 2){
                            restartLevel = 2;
                        }
                    }
                }
                monitor.setProgress(progress++);
            }
        }
        if(diff.entriesDiffering().size() > 0){
            System.out.println("Found files that are differing from remote. Checking them...");
            monitor.setNote("Checking updates");
            Set<Map.Entry<String, MapDifference.ValueDifference<JsonObject>>> entries = diff.entriesDiffering().entrySet();
            for(Map.Entry<String, MapDifference.ValueDifference<JsonObject>> e : entries){
                monitor.setNote("Checking " + e.getKey());
                System.out.println("Checking " + e.getKey());
                int localRev = e.getValue().leftValue().get("rev").getAsInt();
                int remoteRev = e.getValue().rightValue().get("rev").getAsInt();
                System.out.println("  Local rev: " + localRev);
                System.out.println("  Remote rev: " + remoteRev);
                if(remoteRev > localRev){
                    monitor.setNote("Downloading " + e.getKey());
                    System.out.println("Remote has newer version than we have. Redownloading...");
                    File dest = resolve(e.getValue().leftValue().get("destination").getAsString());
                    if(dest.isFile()) dest.delete();
                    File checksum = new File(dest.getAbsolutePath() + ".sha");
                    if(checksum.isFile()) checksum.delete();
                    updateFile(e.getValue().rightValue(), e.getKey());
                    updated = true;
                    local.remove(e.getKey());
                    local.add(e.getKey(), e.getValue().rightValue());
                    if(e.getValue().rightValue().has("restart")){
                        String restart = e.getValue().rightValue().get("restart").getAsString();
                        if(restart.equals("game") && restartLevel <= 1){
                            restartLevel = 1;
                        }else if(restart.equals("launcher") && restartLevel <= 2){
                            restartLevel = 2;
                        }
                    }
                }
                monitor.setProgress(progress++);
            }
        }

        if(updated){
            monitor.setNote("Writing local versions file");
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

        monitor.close();

        return updated;
    }

    private static boolean updateFile(JsonObject object, String name){
        try{
            System.out.println("Downloading " + name);
            File dest = resolve(object.get("destination").getAsString());
            FileUtils.copyURLToFile(new URL(SERVER + object.get("location").getAsString()), dest, 20000, 20000);
            File checksum = new File(dest.getAbsolutePath() + ".sha");
            FileWriter writer = new FileWriter(checksum);
            writer.write(getSHA1(dest));
            writer.close();
            return true;
        }catch(Exception e){
            System.err.println("Error while updating file " + name);
            e.printStackTrace();
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
            System.err.println("Exception while reading remote version data");
            e.printStackTrace();
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
            System.err.println("Exception while reading local version data");
            e.printStackTrace();
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
}*/
