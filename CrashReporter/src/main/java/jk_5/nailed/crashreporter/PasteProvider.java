package jk_5.nailed.crashreporter;

/**
 * No description given
 *
 * @author jk-5
 */
public interface PasteProvider {

    /**
     * Paste to this pastebin.
     *
     * @param title Title of the paste, optional if unsupported
     * @param text Text to paste
     * @return Pastebin entry URL or other identifier
     * @throws PasteException If the pasting failed
     */
    public String paste(String title, String text) throws PasteException;

    /**
     * Paste failed exception.
     *
     * @author jk-5
     */
    public static class PasteException extends RuntimeException {
        public PasteException(String message) {
            super(message);
        }

        public PasteException(Throwable cause) {
            super(cause);
        }
    }
}
