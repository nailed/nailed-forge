package jk_5.nailed.map.sign;

import com.google.common.collect.Sets;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.sign.Sign;
import jk_5.nailed.api.map.sign.SignMappack;
import jk_5.nailed.api.player.Player;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.ChunkWatchEvent;

import java.util.Collection;
import java.util.Set;

/**
 * No description given
 *
 * @author jk-5
 */
public class SignCommandHandler implements jk_5.nailed.api.map.sign.SignCommandHandler {

    private final Map map;
    private final Set<Sign> signs = Sets.newHashSet();

    @java.beans.ConstructorProperties({"map"})
    public SignCommandHandler(Map map) {
        this.map = map;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onChunkLoad(ChunkEvent.Load event){
        for(TileEntity tile : (Collection<TileEntity>) event.getChunk().chunkTileEntityMap.values()){
            if(tile instanceof TileEntitySign){
                TileEntitySign sign = (TileEntitySign) tile;
                Sign signData = this.getSignData(sign);
                if(signData != null){
                    this.addSign(signData);
                }
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onChunkUnload(ChunkEvent.Unload event){
        for(TileEntity tile : (Collection<TileEntity>) event.getChunk().chunkTileEntityMap.values()){
            if(tile instanceof TileEntitySign){
                TileEntitySign sign = (TileEntitySign) tile;
                this.removeSign(this.getSign(sign.xCoord, sign.yCoord, sign.zCoord));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onWatch(ChunkWatchEvent.Watch event){
        World world = event.player.worldObj;
        Chunk chunk = world.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
        for(TileEntity tile : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()){
            if(tile instanceof TileEntitySign){
                TileEntitySign signTile = (TileEntitySign) tile;
                Sign sign = this.getSign(signTile.xCoord, signTile.yCoord, signTile.zCoord);
                if(sign == null) continue;
                sign.addWatcher(event.player);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onUnwatch(ChunkWatchEvent.UnWatch event){
        World world = event.player.worldObj;
        Chunk chunk = world.getChunkFromChunkCoords(event.chunk.chunkXPos, event.chunk.chunkZPos);
        for(TileEntity tile : (Collection<TileEntity>) chunk.chunkTileEntityMap.values()){
            if(tile instanceof TileEntitySign){
                TileEntitySign signTile = (TileEntitySign) tile;
                Sign sign = this.getSign(signTile.xCoord, signTile.yCoord, signTile.zCoord);
                if(sign == null) continue;
                sign.removeWatcher(event.player);
            }
        }
    }

    private Sign getSignData(TileEntitySign sign){
        return this.getSignData(sign.signText, sign.xCoord, sign.yCoord, sign.zCoord);
    }

    private Sign getSignData(String[] lines, int x, int y, int z){
        Sign sign = null;
        if(lines[0].equalsIgnoreCase("$mappack")){
            sign = new SignMappack(this.map, x, y, z, lines);
        }
        if(sign != null){
            for(Player player : this.map.getPlayers()){
                sign.addWatcher(player.getEntity()); //TODO: check if player watches this chunk
            }
        }
        return sign;
    }

    @Override
    public void onSignAdded(String[] lines, int x, int y, int z){
        if(this.getSign(x, y, z) == null){
            Sign sign = this.getSignData(lines, x, y, z);
            if(sign != null){
                this.addSign(sign);
            }
        }
    }

    private void addSign(Sign sign){
        this.signs.add(sign);
        sign.broadcastUpdate();
    }

    private void removeSign(Sign sign){
        this.signs.remove(sign);
    }

    @Override
    public Sign getSign(int x, int y, int z){
        for(Sign sign : this.signs){
            if(sign.getX() == x && sign.getY() == y && sign.getZ() == z){
                return sign;
            }
        }
        return null;
    }

    @Override
    public void onPlayerLeftMap(Map oldMap, Player player){
        for(Sign sign : this.signs){
            sign.onPlayerLeftMap(oldMap, player);
        }
    }

    @Override
    public void onPlayerJoinMap(Map newMap, Player player){
        for(Sign sign : this.signs){
            sign.onPlayerJoinMap(newMap, player);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent event){
        if(event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK){
            Sign sign = this.getSign(event.x, event.y, event.z);
            if(sign != null){
                sign.onPlayerInteract(event.entityPlayer);
            }
        }
    }
}
