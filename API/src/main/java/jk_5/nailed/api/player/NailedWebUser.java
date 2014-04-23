package jk_5.nailed.api.player;

/**
 * No description given
 *
 * @author jk-5
 */
public interface NailedWebUser {

    public String getUsername();
    public String getFullName();
    public String getEmail();
    public boolean isAuthenticated();
}
