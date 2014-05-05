package jk_5.nailed.api.map.sign;

import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;

import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
@RequiredArgsConstructor
public abstract class Sign {

    private final Map map;
    @Getter private final int x;
    @Getter private final int y;
    @Getter private final int z;
    private final String[] originalContent;

    private final Set<EntityPlayerMP> watchers = Sets.newHashSet();

    public TileEntitySign getTileEntity(){
        TileEntity tile = this.map.getWorld().getTileEntity(this.x, this.y, this.z);
        if(tile instanceof TileEntitySign){
            return (TileEntitySign) tile;
        }else{
            return null;
        }
    }

    public S33PacketUpdateSign getUpdatePacket(){
        String[] lines = this.getContent();
        if(lines[0].length() > 15) lines[0] = lines[0].substring(0, 15);
        if(lines[1].length() > 15) lines[1] = lines[1].substring(0, 15);
        if(lines[2].length() > 15) lines[2] = lines[2].substring(0, 15);
        if(lines[3].length() > 15) lines[3] = lines[3].substring(0, 15);
        return new S33PacketUpdateSign(this.x, this.y, this.z, lines);
    }

    public void sendUpdate(Player player){
        player.sendPacket(this.getUpdatePacket());
    }

    public void broadcastUpdate(){
        S33PacketUpdateSign packet = this.getUpdatePacket();
        for(EntityPlayerMP player : this.watchers){
            player.playerNetServerHandler.sendPacket(packet);
        }
    }

    protected abstract String[] getContent();

    public void addWatcher(EntityPlayerMP player){
        this.watchers.add(player);
        player.playerNetServerHandler.sendPacket(this.getUpdatePacket());
    }

    public void removeWatcher(EntityPlayerMP player){
        this.watchers.remove(player);
    }

    public void onPlayerLeftMap(Map map, Player player){

    }

    public void onPlayerJoinMap(Map map, Player player){

    }

    public void onPlayerInteract(EntityPlayer entityPlayer){

    }
}
