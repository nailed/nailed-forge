package jk_5.nailed.network.packets;

import jk_5.nailed.client.EventHandlerClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketTimeTracker extends NailedPacket {

    private String format;

    public PacketTimeTracker(){}
    public PacketTimeTracker(String format){
        this.format = format;
    }

    @Override
    public void writePacket(DataOutput data) throws IOException{
        data.writeUTF(this.format);
    }

    @Override
    public void readPacket(DataInput data) throws IOException {
        this.format = data.readUTF();
    }

    @Override
    public void processPacket(INetworkManager manager, EntityPlayer player){
        EventHandlerClient.format = this.format;
    }
}
