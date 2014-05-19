package jk_5.nailed.api.plugin;

/**
 * No description given
 *
 * @author jk-5
 */
public class UnknownDependencyException extends Exception {

    public UnknownDependencyException() {
    }

    public UnknownDependencyException(String message) {
        super(message);
    }

    public UnknownDependencyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownDependencyException(Throwable cause) {
        super(cause);
    }
}
