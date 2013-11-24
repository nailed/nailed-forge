package jk_5.nailed.network;

import codechicken.lib.packet.PacketCustom;
import jk_5.nailed.NailedModContainer;
import lombok.Getter;

/**
 * No description given
 *
 * @author jk-5
 */
public enum Packets {
    TIME_UPDATE(1),
    NOTIFICATION(2);

    @Getter
    private final int ID;

    private Packets(int id){
        this.ID = id;
    }

    public PacketCustom newPacket(){
        return new PacketCustom(NailedModContainer.getInstance(), this.ID);
    }

    public static Packets fromID(int id){
        for(Packets packet : Packets.values()){
            if(packet.getID() == id){
                return packet;
            }
        }
        return null;
    }
}
