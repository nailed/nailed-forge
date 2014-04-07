package jk_5.nailed.api.map.sign;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.Callback;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.Mappack;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.util.ChatColor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

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
        this.linkedMappack = NailedAPI.getMappackLoader().getMappack(content[1]);
        this.signID = content[2];
        this.linkedMap = null;
    }

    @Override
    protected String[] getContent(){
        String[] ret = new String[4];
        if(this.linkedMap == null){
            ret[0] = ChatColor.BLUE + "[Create]";
        }else if(this.linkedMap.getGameManager().isGameRunning()){
            ret[0] = ChatColor.AQUA + "[Spectate]";
        }else{
            ret[0] = ChatColor.GREEN + "[Join]";
        }
        ret[1] = this.signID;
        if(this.linkedMap == null){
            ret[2] = "";
        }else{
            ret[2] = Integer.toString(this.linkedMap.getAmountOfPlayers());
        }
        if(this.linkedMappack != null){
            String name = this.linkedMappack.getMappackMetadata().getName();
            if(name.length() > 16){
                name = name.substring(0, 16);
            }
            ret[3] = name;
        }else{
            ret[3] = "";
        }
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
        final Player player = NailedAPI.getPlayerRegistry().getPlayer(entityPlayer);
        entityPlayer.swingItem();
        if(this.linkedMap == null){
            IChatComponent component = new ChatComponentText("Loading " + this.linkedMappack.getMappackMetadata().getName() + "...");
            component.getChatStyle().setColor(EnumChatFormatting.GREEN);
            player.sendChat(component);
            NailedAPI.getMapLoader().createMapServer(this.linkedMappack, new Callback<Map>() {
                @Override
                public void callback(Map obj){
                    linkedMap = obj;
                    broadcastUpdate();
                    player.teleportToMap(obj);
                }
            });
        }else{
            player.teleportToMap(this.linkedMap);
        }
    }
}
