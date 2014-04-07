package jk_5.nailed;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.MessageFactory;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedLog {

    private static final Logger nailedLogger = LogManager.getLogger("Nailed");

    public static void catching(Level level, Throwable t){
        nailedLogger.catching(level, t);
    }

    public static boolean isDebugEnabled(Marker marker){
        return nailedLogger.isDebugEnabled(marker);
    }

    public static <T extends Throwable> T throwing(Level level, T t){
        return nailedLogger.throwing(level, t);
    }

    public static void error(Message msg, Throwable t){
        nailedLogger.error(msg, t);
    }

    public static void info(Marker marker, Object message, Throwable t){
        nailedLogger.info(marker, message, t);
    }

    public static boolean isErrorEnabled(Marker marker){
        return nailedLogger.isErrorEnabled(marker);
    }

    public static void warn(Marker marker, String message, Throwable t){
        nailedLogger.warn(marker, message, t);
    }

    public static void warn(Object message){
        nailedLogger.warn(message);
    }

    public static void trace(Marker marker, String message, Throwable t){
        nailedLogger.trace(marker, message, t);
    }

    public static void error(Marker marker, String message, Throwable t){
        nailedLogger.error(marker, message, t);
    }

    public static void log(Level level, Marker marker, String message){
        nailedLogger.log(level, marker, message);
    }

    public static void fatal(String message){
        nailedLogger.fatal(message);
    }

    public static void trace(Message msg, Throwable t){
        nailedLogger.trace(msg, t);
    }

    public static void fatal(Marker marker, Message msg, Throwable t){
        nailedLogger.fatal(marker, msg, t);
    }

    public static void info(Object message){
        nailedLogger.info(message);
    }

    public static void trace(Marker marker, Message msg){
        nailedLogger.trace(marker, msg);
    }

    public static void fatal(Message msg, Throwable t){
        nailedLogger.fatal(msg, t);
    }

    public static void entry(){
        nailedLogger.entry();
    }

    public static void entry(Object... params){
        nailedLogger.entry(params);
    }

    public static void error(Object message){
        nailedLogger.error(message);
    }

    public static void error(Marker marker, Object message, Throwable t){
        nailedLogger.error(marker, message, t);
    }

    public static void info(Marker marker, Message msg, Throwable t){
        nailedLogger.info(marker, msg, t);
    }

    public static void log(Level level, Message msg){
        nailedLogger.log(level, msg);
    }

    public static <T extends Throwable> T throwing(T t){
        return nailedLogger.throwing(t);
    }

    public static void log(Level level, Marker marker, Object message){
        nailedLogger.log(level, marker, message);
    }

    public static void debug(Marker marker, Object message, Throwable t){
        nailedLogger.debug(marker, message, t);
    }

    public static boolean isInfoEnabled(){
        return nailedLogger.isInfoEnabled();
    }

    public static void info(Object message, Throwable t){
        nailedLogger.info(message, t);
    }

    public static void warn(Message msg, Throwable t){
        nailedLogger.warn(msg, t);
    }

    public static void trace(Object message, Throwable t){
        nailedLogger.trace(message, t);
    }

    public static void error(Object message, Throwable t){
        nailedLogger.error(message, t);
    }

    public static void fatal(Marker marker, String message, Object... params){
        nailedLogger.fatal(marker, message, params);
    }

    public static void fatal(Marker marker, String message){
        nailedLogger.fatal(marker, message);
    }

    public static void log(Level level, Marker marker, Object message, Throwable t){
        nailedLogger.log(level, marker, message, t);
    }

    public static String getName(){
        return nailedLogger.getName();
    }

    public static void debug(String message){
        nailedLogger.debug(message);
    }

    public static void fatal(String message, Object... params){
        nailedLogger.fatal(message, params);
    }

    public static void debug(String message, Object... params){
        nailedLogger.debug(message, params);
    }

    public static void fatal(Message msg){
        nailedLogger.fatal(msg);
    }

    public static void debug(Marker marker, Message msg){
        nailedLogger.debug(marker, msg);
    }

    public static void log(Level level, Marker marker, Message msg){
        nailedLogger.log(level, marker, msg);
    }

    public static void error(Marker marker, String message){
        nailedLogger.error(marker, message);
    }

    public static boolean isErrorEnabled(){
        return nailedLogger.isErrorEnabled();
    }

    public static void log(Level level, Marker marker, Message msg, Throwable t){
        nailedLogger.log(level, marker, msg, t);
    }

    public static <R> R exit(R result){
        return nailedLogger.exit(result);
    }

    public static void log(Level level, Object message, Throwable t){
        nailedLogger.log(level, message, t);
    }

    public static boolean isWarnEnabled(Marker marker){
        return nailedLogger.isWarnEnabled(marker);
    }

    public static void catching(Throwable t){
        nailedLogger.catching(t);
    }

    public static void log(Level level, String message, Throwable t){
        nailedLogger.log(level, message, t);
    }

    public static void warn(String message, Object... params){
        nailedLogger.warn(message, params);
    }

    public static void log(Level level, Object message){
        nailedLogger.log(level, message);
    }

    public static void warn(Marker marker, Message msg, Throwable t){
        nailedLogger.warn(marker, msg, t);
    }

    public static void printf(Level level, String format, Object... params){
        nailedLogger.printf(level, format, params);
    }

    public static void error(Marker marker, Message msg){
        nailedLogger.error(marker, msg);
    }

    public static void trace(Marker marker, Object message, Throwable t){
        nailedLogger.trace(marker, message, t);
    }

    public static void info(Message msg){
        nailedLogger.info(msg);
    }

    public static void trace(Message msg){
        nailedLogger.trace(msg);
    }

    public static boolean isTraceEnabled(Marker marker){
        return nailedLogger.isTraceEnabled(marker);
    }

    public static void trace(Marker marker, Message msg, Throwable t){
        nailedLogger.trace(marker, msg, t);
    }

    public static void error(String message, Throwable t){
        nailedLogger.error(message, t);
    }

    public static void warn(Object message, Throwable t){
        nailedLogger.warn(message, t);
    }

    public static void error(Marker marker, Object message){
        nailedLogger.error(marker, message);
    }

    public static void debug(Message msg){
        nailedLogger.debug(msg);
    }

    public static void info(Marker marker, Object message){
        nailedLogger.info(marker, message);
    }

    public static void warn(Marker marker, Object message, Throwable t){
        nailedLogger.warn(marker, message, t);
    }

    public static void trace(Object message){
        nailedLogger.trace(message);
    }

    public static void fatal(Marker marker, Object message, Throwable t){
        nailedLogger.fatal(marker, message, t);
    }

    public static void info(Marker marker, Message msg){
        nailedLogger.info(marker, msg);
    }

    public static void warn(Marker marker, Object message){
        nailedLogger.warn(marker, message);
    }

    public static void info(Marker marker, String message){
        nailedLogger.info(marker, message);
    }

    public static boolean isDebugEnabled(){
        return nailedLogger.isDebugEnabled();
    }

    public static void fatal(String message, Throwable t){
        nailedLogger.fatal(message, t);
    }

    public static boolean isFatalEnabled(Marker marker){
        return nailedLogger.isFatalEnabled(marker);
    }

    public static void debug(Object message, Throwable t){
        nailedLogger.debug(message, t);
    }

    public static void log(Level level, Message msg, Throwable t){
        nailedLogger.log(level, msg, t);
    }

    public static boolean isWarnEnabled(){
        return nailedLogger.isWarnEnabled();
    }

    public static void warn(Marker marker, String message, Object... params){
        nailedLogger.warn(marker, message, params);
    }

    public static void debug(Marker marker, String message, Throwable t){
        nailedLogger.debug(marker, message, t);
    }

    public static void warn(Message msg){
        nailedLogger.warn(msg);
    }

    public static void info(String message, Object... params){
        nailedLogger.info(message, params);
    }

    public static void fatal(Marker marker, Message msg){
        nailedLogger.fatal(marker, msg);
    }

    public static void debug(Marker marker, Object message){
        nailedLogger.debug(marker, message);
    }

    public static void exit(){
        nailedLogger.exit();
    }

    public static void info(Marker marker, String message, Object... params){
        nailedLogger.info(marker, message, params);
    }

    public static void error(Marker marker, Message msg, Throwable t){
        nailedLogger.error(marker, msg, t);
    }

    public static void debug(Object message){
        nailedLogger.debug(message);
    }

    public static void warn(String message, Throwable t){
        nailedLogger.warn(message, t);
    }

    public static void info(Message msg, Throwable t){
        nailedLogger.info(msg, t);
    }

    public static boolean isEnabled(Level level){
        return nailedLogger.isEnabled(level);
    }

    public static void fatal(Object message, Throwable t){
        nailedLogger.fatal(message, t);
    }

    public static void debug(Marker marker, String message){
        nailedLogger.debug(marker, message);
    }

    public static MessageFactory getMessageFactory(){
        return nailedLogger.getMessageFactory();
    }

    public static boolean isInfoEnabled(Marker marker){
        return nailedLogger.isInfoEnabled(marker);
    }

    public static void printf(Level level, Marker marker, String format, Object... params){
        nailedLogger.printf(level, marker, format, params);
    }

    public static void log(Level level, String message){
        nailedLogger.log(level, message);
    }

    public static void fatal(Marker marker, Object message){
        nailedLogger.fatal(marker, message);
    }

    public static void trace(String message){
        nailedLogger.trace(message);
    }

    public static void error(String message, Object... params){
        nailedLogger.error(message, params);
    }

    public static void warn(Marker marker, String message){
        nailedLogger.warn(marker, message);
    }

    public static void debug(Message msg, Throwable t){
        nailedLogger.debug(msg, t);
    }

    public static void trace(String message, Throwable t){
        nailedLogger.trace(message, t);
    }

    public static void debug(Marker marker, Message msg, Throwable t){
        nailedLogger.debug(marker, msg, t);
    }

    public static void warn(Marker marker, Message msg){
        nailedLogger.warn(marker, msg);
    }

    public static void log(Level level, String message, Object... params){
        nailedLogger.log(level, message, params);
    }

    public static void error(Marker marker, String message, Object... params){
        nailedLogger.error(marker, message, params);
    }

    public static void fatal(Object message){
        nailedLogger.fatal(message);
    }

    public static void debug(String message, Throwable t){
        nailedLogger.debug(message, t);
    }

    public static void error(String message){
        nailedLogger.error(message);
    }

    public static void debug(Marker marker, String message, Object... params){
        nailedLogger.debug(marker, message, params);
    }

    public static void trace(Marker marker, String message){
        nailedLogger.trace(marker, message);
    }

    public static void trace(Marker marker, Object message){
        nailedLogger.trace(marker, message);
    }

    public static void trace(String message, Object... params){
        nailedLogger.trace(message, params);
    }

    public static boolean isFatalEnabled(){
        return nailedLogger.isFatalEnabled();
    }

    public static void info(String message, Throwable t){
        nailedLogger.info(message, t);
    }

    public static void fatal(Marker marker, String message, Throwable t){
        nailedLogger.fatal(marker, message, t);
    }

    public static void error(Message msg){
        nailedLogger.error(msg);
    }

    public static void info(String message){
        nailedLogger.info(message);
    }

    public static void trace(Marker marker, String message, Object... params){
        nailedLogger.trace(marker, message, params);
    }

    public static void info(Marker marker, String message, Throwable t){
        nailedLogger.info(marker, message, t);
    }

    public static void log(Level level, Marker marker, String message, Throwable t){
        nailedLogger.log(level, marker, message, t);
    }

    public static void log(Level level, Marker marker, String message, Object... params){
        nailedLogger.log(level, marker, message, params);
    }

    public static boolean isTraceEnabled(){
        return nailedLogger.isTraceEnabled();
    }

    public static boolean isEnabled(Level level, Marker marker){
        return nailedLogger.isEnabled(level, marker);
    }

    public static void warn(String message){
        nailedLogger.warn(message);
    }
}
