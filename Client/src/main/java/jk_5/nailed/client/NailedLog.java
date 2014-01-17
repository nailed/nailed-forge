package jk_5.nailed.client;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedLog {

    private static final Logger nailedLogger = LogManager.getLogger("Nailed");

    public static void log(Level level, String format, Object... data) {
        nailedLogger.log(level, String.format(format, data));
    }

    public static void log(Level level, Throwable ex, String format, Object... data) {
        nailedLogger.log(level, String.format(format, data), ex);
    }

    public static void severe(Throwable ex, String format, Object... data) {
        log(Level.ERROR, ex, format, data);
    }

    public static void severe(String format, Object... data) {
        log(Level.ERROR, format, data);
    }

    public static void warning(String format, Object... data) {
        log(Level.ERROR, format, data);
    }

    public static void info(String format, Object... data) {
        log(Level.INFO, format, data);
    }
}
