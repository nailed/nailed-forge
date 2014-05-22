package jk_5.nailed.api.map;

/**
 * No description given
 *
 * @author jk-5
 */
public enum PostGameAction {
    TO_SPAWN("spawn"),
    TO_LOBBY("lobby"),
    NOTHING("nothing");

    private final String type;

    PostGameAction(String type) {
        this.type = type;
    }

    public static PostGameAction fromType(String type) {
        for(PostGameAction a : PostGameAction.values()){
            if(a.type.equalsIgnoreCase(type)){
                return a;
            }
        }
        return NOTHING;
    }
}
