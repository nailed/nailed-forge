package jk_5.nailed.updater;

import com.google.common.collect.Sets;
import jk_5.nailed.updater.json.Library;
import jk_5.nailed.updater.json.LibraryList;
import jk_5.nailed.updater.json.RestartLevel;
import lombok.Getter;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * No description given
 *
 * @author jk-5
 */
public class Updater {

    private static final Logger logger = LogManager.getLogger("Nailed-Updater");
    private static final String SERVER = "http://maven.reening.nl/";
    private static final String VERSIONS_URL = SERVER + "nailed/versions-2.json";
    private static final File VERSIONS_FILE = new File("nailedVersions.json");
    private static final Executor downloadThreadPool = Executors.newCachedThreadPool(new ThreadFactory() {
        private int id = 0;
        @Override
        @SuppressWarnings("NullableProblems")
        public Thread newThread(Runnable r){
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("DownloadThread-#" + id++);
            return t;
        }
    });

    @Getter private static RestartLevel restart = RestartLevel.NOTHING;

    @SuppressWarnings("ConstantConditions")
    public static boolean checkForUpdates(){
        logger.info("Checking for updates...");

        final DownloadMonitor monitor = new DownloadMonitor();
        final AtomicInteger progress = new AtomicInteger(0);

        monitor.setNote("Reading remote versions");
        LibraryList remote = LibraryList.readFromUrl(VERSIONS_URL);
        monitor.setNote("Reading local versions");
        final LibraryList local = LibraryList.readFromFile(VERSIONS_FILE);

        monitor.setNote("Cleaning up the mods folder...");
        File modsFolder = resolve("{MC_GAME_DIR}/mods/");
        if(!modsFolder.exists()) modsFolder.mkdir();
        monitor.setProgress(0);
        monitor.setMaximum(modsFolder.listFiles().length);
        for(File file : modsFolder.listFiles()){
            if(file.getName().contains("Nailed") || file.getName().contains("nailed")){
                logger.info("Found nailed related file " + file.getName() + " in mods folder. Removing it!");
                file.delete();
            }
            monitor.setProgress(progress.getAndIncrement());
        }

        Set<Library> download = Sets.newHashSet();

        monitor.setNote("Scanning artifact versions");
        monitor.setProgress(0);
        monitor.setMaximum(remote.libraries.size());
        progress.set(0);
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
            monitor.setProgress(progress.getAndIncrement());
        }

        final AtomicBoolean updated = new AtomicBoolean(false);
        final CountDownLatch latch = new CountDownLatch(download.size());

        monitor.setProgress(0);
        monitor.setMaximum(download.size());
        progress.set(0);
        monitor.setNote("Downloading updates");
        for(final Library library : download){
            downloadThreadPool.execute(new Runnable() {
                @Override
                public void run(){
                    logger.info("Starting update for " + library.name);
                    long startTime = System.currentTimeMillis();
                    boolean u = false;
                    try{
                        File dest = resolve(library.destination);
                        FileUtils.copyURLToFile(new URL(library.location), dest, 20000, 20000);
                        u = true;
                    }catch(Exception e){
                        logger.error("Error while updating file " + library.name, e);
                    }
                    if(u){
                        updated.set(true);
                        synchronized(local){
                            Iterator<Library> it = local.libraries.iterator();
                            while(it.hasNext()){
                                Library lib = it.next();
                                if(lib.name.equals(library.name)){
                                    it.remove();
                                }
                            }
                            local.libraries.add(library);
                        }
                        if(library.restart == RestartLevel.GAME && restart == RestartLevel.NOTHING){
                            restart = RestartLevel.GAME;
                        }else if(library.restart == RestartLevel.LAUNCHER && (restart == RestartLevel.NOTHING || restart == RestartLevel.GAME)){
                            restart = RestartLevel.LAUNCHER;
                        }
                    }
                    logger.info("Finished updating " + library.name + " (Took " + (System.currentTimeMillis() - startTime) + "ms)");
                    monitor.setProgress(progress.getAndIncrement());
                    latch.countDown();
                }
            });
        }

        try{
            latch.await();
        }catch(InterruptedException e){
            logger.error("Error while waiting for the updates to happen", e);
        }

        monitor.setProgress(0);
        monitor.setMaximum(0);
        progress.set(0);
        monitor.setNote("Writing local versions file");

        if(updated.get()){
            logger.info("Writing local versions file...");
            local.writeToFile(VERSIONS_FILE);
        }

        if(restart == RestartLevel.NOTHING){
            logger.info("Moving artifacts to the mod folder");
            for(Library library : remote.libraries){
                if(library.mod){
                    try{
                        monitor.setNote("Moving " + library.name + " to the mods folder");
                        logger.info("  Moving " + library.name + " to the mods folder");
                        File location = resolve(library.destination);
                        File dest = resolve("{MC_GAME_DIR}/mods/" + location.getName());
                        FileUtils.copyFile(location, dest);
                        dest.deleteOnExit();
                    }catch(IOException e){
                        logger.error("Error while moving file " + library.name, e);
                    }
                    monitor.setProgress(progress.getAndIncrement());
                }
            }
        }

        monitor.close();
        return updated.get();
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
}
