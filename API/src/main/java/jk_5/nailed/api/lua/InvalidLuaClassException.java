package jk_5.nailed.api.lua;

/**
 * No description given
 *
 * @author jk-5
 */
public class InvalidLuaClassException extends RuntimeException {

    public InvalidLuaClassException(String message) {
        super(message);
    }

    public InvalidLuaClassException(String message, Throwable cause) {
        super(message, cause);
    }
}
