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
        return new S33PacketUpdateSign(this.x, this.y, this.z, this.getContent());
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
