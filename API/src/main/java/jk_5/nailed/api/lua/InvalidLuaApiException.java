package jk_5.nailed.api.lua;

/**
 * No description given
 *
 * @author jk-5
 */
public class InvalidLuaApiException extends RuntimeException {

    public InvalidLuaApiException(String message) {
        super(message);
    }

    public InvalidLuaApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
