package jk_5.nailed.network.packets;

import jk_5.nailed.server.ProxyCommon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraftforge.common.DimensionManager;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketRegisterDimension extends NailedPacket {

    private int dimension;

    public PacketRegisterDimension(){}
    public PacketRegisterDimension(int dim){
        this.dimension = dim;
    }

    @Override
    public void writePacket(DataOutput data) throws IOException {
        data.writeInt(this.dimension);
    }

    @Override
    public void readPacket(DataInput data) throws IOException {
        this.dimension = data.readInt();
    }

    @Override
    public void processPacket(INetworkManager manager, EntityPlayer player){
        if(!DimensionManager.isDimensionRegistered(this.dimension)){
            DimensionManager.registerDimension(this.dimension, ProxyCommon.providerID);
        }
    }
}
