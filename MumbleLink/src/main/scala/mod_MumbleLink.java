import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSource;
import java.util.ArrayList;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class mod_MumbleLink {

    public static boolean loaded = false;

    public static native int updateLinkedMumble(
            float[] fAvatarPosition,
            float[] fAvatarFront,
            float[] fAvatarTop,
            String name,
            String description,
            float[] fCameraPosition,
            float[] fCameraFront,
            float[] fCameraTop,
            String identity,
            String context);

    private static native int initMumble();

    public static boolean load(String name) {
        boolean loaded = false;

        CodeSource source = mod_MumbleLink.class.getProtectionDomain().getCodeSource();
        if(source.getLocation().getProtocol().equals("jar")){
            try{
                for(String path : libCandidates(name)){
                    File lib = null;
                    try{
                        System.out.println("Attempting " + path);
                        lib = extract(name, mod_MumbleLink.class.getResourceAsStream(path));
                        System.load(lib.getAbsolutePath());
                        loaded = true;
                        System.out.println("Attempt succeeded!");
                        break;
                    }catch(Throwable ignored){
                        System.out.println("Attempt failed");
                    }finally{
                        if(lib != null) lib.delete();
                    }
                }
            }catch(Throwable e){
                e.printStackTrace();
                loaded = false;
            }
        }else{
            System.loadLibrary("mod_MumbleLink_x64");
            loaded = true;
        }

        return loaded;
    }

    private static List<String> libCandidates(String name) {
        List<String> candidates = new ArrayList<String>();
        String base = "/assets/nailedmumblelink/natives/";

        if(is64bit()){
            candidates.add(base + "lib" + name + "_x64.so");
            candidates.add(base + "lib" + name + "_x64.dylib");
            candidates.add(base + name + "_x64.dll");
        }else{
            candidates.add(base + "lib" + name + ".so");
            candidates.add(base + "lib" + name + ".dylib");
            candidates.add(base + name + ".dll");
        }

        return candidates;
    }

    private static File extract(String name, InputStream is) throws IOException{
        byte[] buf = new byte[4096];
        int len;

        File lib = File.createTempFile(name, "lib");
        FileOutputStream os = new FileOutputStream(lib);

        try {
            while ((len = is.read(buf)) > 0) {
                os.write(buf, 0, len);
            }
        } catch (IOException e) {
            lib.delete();
            throw e;
        } finally {
            os.close();
            is.close();
        }

        return lib;
    }

    public static boolean tryInit(){
        loaded = initMumble() == 0;
        return loaded;
    }

    static {
        if(load("mod_MumbleLink")){
            NailedMumbleLink.logger().info("Successfully loaded native MumbleLink module");
        }else{
            NailedMumbleLink.logger().info("Not able to load native MumbleLink module");
        }
    }

    public static boolean is64bit(){
        String[] args = new String[] {"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        for(String arg : args){
            String prop = System.getProperty(arg);
            if(prop != null && prop.contains("64")){
                return true;
            }
        }
        return false;
    }
}
