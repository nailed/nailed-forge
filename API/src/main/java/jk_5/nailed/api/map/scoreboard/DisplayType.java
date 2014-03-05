package jk_5.nailed.api.map.scoreboard;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@Getter
@RequiredArgsConstructor
public enum DisplayType {
    PLAYER_LIST(0),
    SIDEBAR(1),
    BELOW_NAME(2);

    private final int id;
}
