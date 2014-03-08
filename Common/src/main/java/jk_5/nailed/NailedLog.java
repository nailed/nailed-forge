package jk_5.nailed;

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

    public static void fatal(Throwable ex, String format, Object... data) {
        log(Level.FATAL, ex, format, data);
    }

    public static void fatal(String format, Object... data) {
        log(Level.FATAL, format, data);
    }

    public static void error(Throwable ex, String format, Object... data) {
        log(Level.ERROR, ex, format, data);
    }

    public static void error(String format, Object... data) {
        log(Level.ERROR, format, data);
    }

    public static void warn(String format, Object... data) {
        log(Level.WARN, format, data);
    }

    public static void info(String format, Object... data) {
        log(Level.INFO, format, data);
    }
}
