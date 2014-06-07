package jk_5.nailed.api.map.sign;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by matthias on 7-6-14.
 */
public class SignCommand extends Sign{

    private final String command;

    public SignCommand(Map map, int x, int y, int z, String[] content) {
        super(map, x, y, z, content);
        this.command = content[1] + content[2] + content[3];
    }

    @Override
    protected String[] getContent() {
        String[] ret = new String[4];
        ret[0] = "";
        ret[1] = this.command.split(" ")[0];
        return ret;
    }

    @Override
    public void onPlayerInteract(EntityPlayer entityPlayer) {
        final Player player = NailedAPI.getPlayerRegistry().getPlayer(entityPlayer);
        entityPlayer.swingItem();
        player.executeCommand(command);
    }
}
