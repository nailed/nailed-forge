package jk_5.nailed.api.player;

/**
 * No description given
 *
 * @author jk-5
 */
public interface NailedWebUser {

    String getUsername();
    String getFullName();
    String getEmail();
    boolean isAuthenticated();
}
