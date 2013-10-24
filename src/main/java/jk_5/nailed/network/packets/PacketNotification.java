package jk_5.nailed.network.packets;

import jk_5.nailed.client.render.NotificationRenderer;
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
public class PacketNotification extends NailedPacket {

    private String text;

    public PacketNotification(){}
    public PacketNotification(String text){
        this.text = text;
    }

    @Override
    public void writePacket(DataOutput data) throws IOException {
        data.writeUTF(this.text);
    }

    @Override
    public void readPacket(DataInput data) throws IOException {
        this.text = data.readUTF();
    }

    @Override
    public void processPacket(INetworkManager manager, EntityPlayer player){
        NotificationRenderer.addNotification(this.text);
    }
}
