package jk_5.nailed.updater;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import jk_5.nailed.updater.oldjson.*;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.List;
import java.util.Locale;
import java.util.Set;

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

    private static RemoteDependencyFile remoteDeps;
    private static LocalDependencyFile localDeps;

    public static void run(){
        remoteDeps = JsonFactory.loadRemoteDependencyFile(REMOTE_DEPS);
        localDeps = JsonFactory.loadLocalDependencyFile(LOCAL_DEPS);
        UpdatingTweaker.mainClass = remoteDeps.mainClass;

        Set<RemoteLibrary> download = Sets.newHashSet();
        List<LocalLibrary> newList = Lists.newArrayList();

        for(RemoteLibrary library : remoteDeps.libraries){
            LocalLibrary local = null;
            for(LocalLibrary l : localDeps.libraries){
                if(l.id.equals(library.id)){
                    local = l;
                }
            }
            if(local == null){
                download.add(library); //Download it!
            }else{
                if(library.rev > local.rev){
                    download.add(library); //Outdated. Redownload!
                }
            }
        }

        File mcDir = getMinecraftFolder();
        File librariesDir = new File(mcDir, "libraries");

        for(RemoteLibrary library : download){
            try{
                logger.info("Downloading " + library.id);
                File dest = new File(librariesDir, library.getPath());
                FileUtils.copyURLToFile(new URL(library.getFileUrl()), dest, 20000, 20000);
                newList.add(new LocalLibrary(library.rev, library.id, getSHA1(dest)));
            }catch(MalformedURLException e){
                logger.error("Invalid url while downloading " + library.id, e);
            }catch(IOException e){
                logger.error("Error while downloading " + library.id, e);
            }
        }

        localDeps.libraries = newList;
        JsonFactory.writeLocalDependencyFile(localDeps, LOCAL_DEPS);
    }

    public static void addToClassLoader(LaunchClassLoader classLoader){
        //TODO: add local libs to classloader
        //TODO: save local libs' tweakers locally
        //TODO: inject local libs' tweakers
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
