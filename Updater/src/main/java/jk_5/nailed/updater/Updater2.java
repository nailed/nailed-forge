package jk_5.nailed.updater;

import jk_5.nailed.updater.json.JsonFactory;
import jk_5.nailed.updater.json.LocalDependencyFile;
import jk_5.nailed.updater.json.LocalLibrary;
import jk_5.nailed.updater.json.dependencies.DependencyFile;
import jk_5.nailed.updater.json.dependencies.Library;
import jk_5.nailed.updater.json.launcher.LauncherProfile;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * No description given
 *
 * @author jk-5
 */
public class Updater2 {

    private static final String SERVER = "http://maven.reening.nl/";
    private static final String REMOTE_DEPS = SERVER + "nailed/dependencies.json";
    private static final File LOCAL_DEPS = new File("nailedDependencies.json");
    public static final Logger logger = LogManager.getLogger("Nailed-Updater");

    private static DependencyFile remoteDeps;
    private static LocalDependencyFile localDeps;

    private static final File mcDir = getMinecraftFolder();
    private static final File librariesDir = new File(mcDir, "libraries");
    private static final LaunchClassLoader classLoader = Launch.classLoader;

    public static void main(String[] args){
        run();
    }

    public static void run(){
        File versionDir = new File(new File(mcDir, "versions"), UpdatingTweaker.name);
        File launchProfile = new File(versionDir, UpdatingTweaker.name + ".json");

        remoteDeps = JsonFactory.loadRemoteDependencyFile(REMOTE_DEPS);
        localDeps = JsonFactory.loadLocalDependencyFile(LOCAL_DEPS);
        UpdatingTweaker.mainClass = remoteDeps.mainClass;

        List<Library> download = new ArrayList<Library>();
        List<LocalLibrary> newList = new ArrayList<LocalLibrary>();

        for(Library library : remoteDeps.libraries){
            LocalLibrary local = null;
            for(LocalLibrary l : localDeps.libraries){
                if(l.id.equals(library.id)){
                    local = l;
                }
            }
            if(library.applies()){
                if(local == null){
                    download.add(library); //Download it!
                }else{
                    if(!library.getArtifact().getVersion().equals(local.getArtifact().getVersion())){
                        download.add(library); //Outdated. Redownload!
                    }
                }
                newList.add(new LocalLibrary(library.id, library.name, getSHA1(new File(librariesDir, library.getPath())), library.tweaker, library.classLoaderExclusions, library.transformerExclusions, library.launcher));
            }
        }

        for(Library library : download){
            try{
                File dest = new File(librariesDir, library.getPath());
                logger.info("Downloading " + library.id);
                FileUtils.copyURLToFile(new URL(library.getUrl()), dest, 20000, 20000);
            }catch(MalformedURLException e){
                logger.error("Invalid url while downloading " + library.id, e);
            }catch(IOException e){
                logger.error("Error while downloading " + library.id, e);
            }
        }

        localDeps.libraries = newList;
        JsonFactory.writeLocalDependencyFile(localDeps, LOCAL_DEPS);

        logger.info("Generating launcher profile");
        LauncherProfile profile = new LauncherProfile();
        profile.id = remoteDeps.profile.name;
        profile.mainClass = remoteDeps.profile.mainClass;
        profile.minimumLauncherVersion = 13;
        profile.releaseTime = remoteDeps.profile.releaseTime;
        profile.sync = false;
        profile.time = remoteDeps.date;
        profile.type = remoteDeps.profile.type;
        StringBuilder argumentsBuilder = new StringBuilder(remoteDeps.profile.arguments);
        profile.libraries = new ArrayList<Library>();
        //noinspection unchecked
        List<String> tweakers = (List<String>) Launch.blackboard.get("TweakClasses");
        for(Library library : remoteDeps.libraries){
            if(library.applies()){
                if(library.launcher){
                    profile.libraries.add(library);
                    if(library.tweaker != null && !library.tweaker.isEmpty()){
                        //Add the tweaker to the launcher profile
                        argumentsBuilder.append(" --tweakClass ");
                        argumentsBuilder.append(library.tweaker);
                    }
                }else{
                    if(library.tweaker != null && !library.tweaker.isEmpty()){
                        //Add the tweaker to the classloader
                        tweakers.add(library.tweaker);
                    }
                }
            }
        }
        profile.minecraftArguments = argumentsBuilder.toString();

        for(LocalLibrary library : localDeps.libraries){
            if(!library.launcher){
                try{
                    logger.info("Loading library " + new File(librariesDir, library.getPath()).getAbsolutePath());
                    classLoader.addURL(new File(librariesDir, library.getPath()).toURI().toURL());
                }catch(MalformedURLException e){
                    try{
                        classLoader.addURL(new URL(new File(librariesDir, library.getPath()).getAbsolutePath()));
                    }catch(MalformedURLException e1){
                        logger.fatal("Error while adding library to classloader");
                        throw new RuntimeException(e1);
                    }
                }
            }
            if(library.classLoaderExclusions != null){
                for(String string : library.classLoaderExclusions){
                    classLoader.addClassLoaderExclusion(string);
                }
            }
            if(library.transformerExclusions != null){
                for(String string : library.transformerExclusions){
                    classLoader.addTransformerExclusion(string);
                }
            }
        }

        launchProfile.renameTo(new File(launchProfile.getAbsolutePath() + ".bak"));
        FileWriter writer = null;
        try{
            writer = new FileWriter(launchProfile);
            JsonFactory.gson.toJson(profile, writer);
        }catch(IOException e){
            //Why?
        }finally{
            FileUtils.close(writer);
        }
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
            FileUtils.close(stream);
        }

        return String.format("%1$0" + hashLength + "x", new BigInteger(1, stream.getMessageDigest().digest()));
    }
}
