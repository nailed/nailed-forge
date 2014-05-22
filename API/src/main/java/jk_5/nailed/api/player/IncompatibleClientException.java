package jk_5.nailed.api.player;

/**
 * Created by matthias on 14-5-14.
 */
public class IncompatibleClientException extends RuntimeException {

    public final String node;
    public Player player;

    public IncompatibleClientException(String node, Player player) {
        super("Incompatible Client encountered: " + node);
        this.node = node;
        this.player = player;
        player.sendChat("Your client does not support this command.");
        player.sendChat("Try the Nailed client, for instance");
    }
}
