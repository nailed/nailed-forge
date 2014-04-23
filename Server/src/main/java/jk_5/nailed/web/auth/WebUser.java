package jk_5.nailed.web.auth;

import jk_5.nailed.api.player.NailedWebUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class WebUser implements NailedWebUser {

    @Getter private String id;
    @Getter private String username;
    @Getter private String fullName;
    @Getter private String email;
    @Getter private boolean authenticated;
}
