package jk_5.nailed.crashreporter;

/**
 * No description given
 *
 * @author jk-5
 */
public interface NotificationHandler {

    public void notify(String title, String text, String url) throws NotifyException;

    public static class NotifyException extends RuntimeException {
        public NotifyException(String message) {
            super(message);
        }

        public NotifyException(Throwable cause) {
            super(cause);
        }
    }
}
