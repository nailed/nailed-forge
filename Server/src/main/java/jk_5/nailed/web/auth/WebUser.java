package jk_5.nailed.web.auth;

import jk_5.nailed.api.player.NailedWebUser;

/**
 * No description given
 *
 * @author jk-5
 */
public class WebUser implements NailedWebUser {

    private String id;
    private String username;
    private String fullName;
    private String email;
    private boolean authenticated;

    public WebUser(String id, String username, String fullName, String email, boolean authenticated) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.email = email;
        this.authenticated = authenticated;
    }

    public WebUser() {
    }

    public String getId() {
        return this.id;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getFullName() {
        return this.fullName;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public boolean isAuthenticated() {
        return this.authenticated;
    }
}
