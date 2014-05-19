package jk_5.nailed.api.plugin;

/**
 * No description given
 *
 * @author jk-5
 */
public class InvalidPluginException extends Exception {

    public InvalidPluginException(Throwable cause) {
        super(cause);
    }

    public InvalidPluginException() {
    }

    public InvalidPluginException(String message) {
        super(message);
    }

    public InvalidPluginException(String message, Throwable cause) {
        super(message, cause);
    }
}
