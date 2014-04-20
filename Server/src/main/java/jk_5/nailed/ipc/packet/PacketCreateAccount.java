package jk_5.nailed.ipc.packet;

import io.netty.buffer.ByteBuf;
import jk_5.nailed.ipc.PacketUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * No description given
 *
 * @author jk-5
 */
@NoArgsConstructor
@AllArgsConstructor
public class PacketCreateAccount extends IpcPacket {

    public String playerId;
    public String username;
    public String email;
    public String name;
    public String password;

    @Override
    public void encode(ByteBuf buffer){
        PacketUtils.writeString(this.playerId, buffer);
        PacketUtils.writeString(this.username, buffer);
        PacketUtils.writeString(this.email, buffer);
        PacketUtils.writeString(this.name, buffer);
        PacketUtils.writeString(this.password, buffer);
    }

    @Override
    public void decode(ByteBuf buffer){

    }

    @Override
    public void processPacket(){

    }
}
