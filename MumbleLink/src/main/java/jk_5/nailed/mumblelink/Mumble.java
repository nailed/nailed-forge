package jk_5.nailed.mumblelink;

/**
 * No description given
 *
 * @author jk-5
 */
public class Mumble {

    private static boolean inited = false;

    static{
        //TODO: Better loading
        System.loadLibrary("MumbleLink");
    }

    public static native int update(
            float[] fAvatarPosition,            //Max length: 3
            float[] fAvatarFront,               //Max length: 3
            float[] fAvatarTop,                 //Max length: 3
            String name,                        //Max length: 256
            String description,
            float[] fCameraPosition,            //Max length: 3
            float[] fCameraFront,               //Max length: 3
            float[] fCameraTop,                 //Max length: 3
            String identity,                    //Max length: 256
            String context);

    private static native int init();

    public static boolean tryInit(){
        int error = init();
        if(error != 0){
            inited = false;
        }else{
            inited = true;
        }
        return inited;
    }

    public static boolean isInited(){
        return inited;
    }
}
