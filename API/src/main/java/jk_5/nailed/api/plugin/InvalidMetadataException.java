package jk_5.nailed.api.plugin;

/**
 * No description given
 *
 * @author jk-5
 */
public class InvalidMetadataException extends Exception {

    public InvalidMetadataException() {
        super("Invalid plugin.json file");
    }

    public InvalidMetadataException(String message) {
        super(message);
    }

    public InvalidMetadataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMetadataException(Throwable cause) {
        super("Invalid plugin.json file", cause);
    }
}
