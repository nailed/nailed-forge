package jk_5.nailed.players;

import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;

/**
 * No description given
 *
 * @author jk-5
 */
@Getter
@Setter
public class PlayerData implements jk_5.nailed.api.player.PlayerData {

    private int timesOnline = 0;

    @Override
    public void read(JsonObject data){
        if(data.has("timesOnline")){
            this.timesOnline = data.get("timesOnline").getAsInt();
        }
    }

    @Override
    public void write(JsonObject data){
        data.addProperty("timesOnline", this.timesOnline);
    }
}
