package jk_5.nailed.updater;

import com.google.common.collect.Sets;
import jk_5.nailed.updater.json.Library;
import jk_5.nailed.updater.json.LibraryList;
import jk_5.nailed.updater.json.RestartLevel;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class Updater {

    private static final Logger logger = LogManager.getLogger("Nailed-Updater");
    private static final String SERVER = "http://maven.reening.nl/";
    private static final String VERSIONS_URL = SERVER + "nailed/versions-1.json";
    private static final File VERSIONS_FILE = new File("nailedVersions.json");

    @Getter private static RestartLevel restart = RestartLevel.NOTHING;

    @SuppressWarnings("ConstantConditions")
    public static boolean checkForUpdates(){
        logger.info("Checking for updates...");

        //DownloadMonitor monitor = new DownloadMonitor();
        //int progress = 0;

        LibraryList remote = LibraryList.readFromUrl(VERSIONS_URL);
        LibraryList local = LibraryList.readFromFile(VERSIONS_FILE);

        //monitor.setNote("Cleaning up the mods folder...");
        File modsFolder = resolve("{MC_GAME_DIR}/mods/");
        if(!modsFolder.exists()) modsFolder.mkdir();
        for(File file : modsFolder.listFiles()){
            if(file.getName().contains("Nailed") || file.getName().contains("nailed")){
                logger.info("Found nailed related file " + file.getName() + " in mods folder. Removing it!");
                file.delete();
            }
        }

        Set<Library> download = Sets.newHashSet();

        for(Library library : remote.libraries){
            Library loc = null;
            for(Library l : local.libraries){
                if(l.name.equals(library.name)){
                    loc = l;
                }
            }
            if(loc == null){
                logger.info("New remote library " + library.name + " will be downloaded");
                download.add(library); //Download it!
            }else{
                if(library.rev > loc.rev){
                    logger.info("Library " + library.name + " is outdated");
                    logger.info("  Local rev: " + loc.rev);
                    logger.info("  Remote rev: " + library.rev);
                    download.add(library); //Outdated. Redownload!
                }
            }
        }

        boolean updated = false;

        for(Library library : download){
            boolean u = updateFile(library);
            updated |= u;
            if(u){
                Iterator<Library> it = local.libraries.iterator();
                while(it.hasNext()){
                    Library lib = it.next();
                    if(lib.name.equals(library.name)){
                        it.remove();
                    }
                }
                local.libraries.add(library);
                if(library.restart == RestartLevel.GAME && restart == RestartLevel.NOTHING){
                    restart = RestartLevel.GAME;
                }else if(library.restart == RestartLevel.LAUNCHER && (restart == RestartLevel.NOTHING || restart == RestartLevel.GAME)){
                    restart = RestartLevel.LAUNCHER;
                }
            }
            //monitor.setProgress(progress++);
        }

        if(updated){
            logger.info("Writing local versions file...");
            local.writeToFile(VERSIONS_FILE);
        }

        logger.info("Moving artifacts to the mod folder");
        for(Library library : remote.libraries){
            if(library.mod){
                try{
                    logger.info("Moving " + library.name + " to the mods folder");
                    File location = resolve(library.destination);
                    File dest = resolve("{MC_GAME_DIR}/mods/" + location.getName());
                    FileUtils.copyFile(location, dest);
                    dest.deleteOnExit();
                }catch(IOException e){
                    logger.error("Error while moving file " + library.name, e);
                }
            }
        }

        //monitor.close();
        return updated;
    }

    private static boolean updateFile(Library library){
        try{
            logger.info("Downloading " + library.name);
            File dest = resolve(library.destination);
            FileUtils.copyURLToFile(new URL(library.location), dest, 20000, 20000);
            File checksum = new File(dest.getAbsolutePath() + ".sha");
            FileWriter writer = new FileWriter(checksum);
            writer.write(getSHA1(dest));
            writer.close();
            return true;
        }catch(Exception e){
            logger.error("Error while updating file " + library.name, e);
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
