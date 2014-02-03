package jk_5.nailed.map.sign;

import jk_5.nailed.map.Map;
import jk_5.nailed.map.MapLoader;
import jk_5.nailed.map.mappack.Mappack;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.util.ChatColor;
import net.minecraft.entity.player.EntityPlayer;

/**
 * No description given
 *
 * @author jk-5
 */
public class SignMappack extends Sign {

    private final Mappack linkedMappack;
    private final String signID;
    private Map linkedMap;

    public SignMappack(Map map, int x, int y, int z, String[] content){
        super(map, x, y, z, content);
        this.linkedMappack = MapLoader.instance().getMappack(content[1]);
        this.signID = content[2];
        this.linkedMap = null;
    }

    @Override
    protected String[] getContent(){
        String[] ret = new String[4];
        if(this.linkedMap == null){
            ret[0] = ChatColor.BLUE + "[Create]";
        }else if(this.linkedMap.getGameController().isRunning()){
            ret[0] = ChatColor.AQUA + "[Spectate]";
        }else{
            ret[0] = ChatColor.GREEN + "[Join]";
        }
        ret[1] = this.signID;
        if(this.linkedMap == null){
            ret[2] = "";
        }else{
            ret[2] = Integer.toString(this.linkedMap.getJoinedPlayers());
        }
        ret[3] = this.linkedMappack.getMappackMetadata().getName();
        return ret;
    }

    @Override
    public void onPlayerLeftMap(Map map, Player player){
        super.onPlayerLeftMap(map, player);
        this.broadcastUpdate();
    }

    @Override
    public void onPlayerJoinMap(Map map, Player player){
        super.onPlayerJoinMap(map, player);
        this.broadcastUpdate();
    }

    @Override
    public void onPlayerInteract(EntityPlayer entityPlayer){
        Player player = PlayerRegistry.instance().getPlayer(entityPlayer);
        entityPlayer.swingItem();
        if(this.linkedMap == null){
            Map map = MapLoader.instance().newMapServerFor(this.linkedMappack);
            this.linkedMap = map;
            this.broadcastUpdate();
            player.teleportToMap(map);
        }else{
            player.teleportToMap(this.linkedMap);
        }
    }
}
