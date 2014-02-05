package jk_5.nailed.api;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public enum Gamemode {
    SURVIVAL(0),
    CREATIVE(1),
    ADVENTURE(2);

    @Getter private final int id;

    public static Gamemode fromId(int id){
        for(Gamemode mode : Gamemode.values()){
            if(mode.getId() == id){
                return mode;
            }
        }
        return null;
    }
}
