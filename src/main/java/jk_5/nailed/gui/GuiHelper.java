package jk_5.nailed.gui;

/**
 * No description given
 *
 * @author jk-5
 */
public class GuiHelper {

    private static final DoNotShowException instance;
    static {
        instance = new DoNotShowException();
        instance.setStackTrace(new StackTraceElement[0]);
    }

    public static void doNotShow(){
        throw instance;
    }

    private static class DoNotShowException extends RuntimeException{}
}
