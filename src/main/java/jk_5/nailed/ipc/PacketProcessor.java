package jk_5.nailed.ipc;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import jk_5.nailed.ipc.packet.IpcPacket;

import java.util.EnumSet;

/**
 * No description given
 *
 * @author jk-5
 */
public class PacketProcessor implements IScheduledTickHandler {

    @Override
    public int nextTickSpacing() {
        return 5;
    }

    @Override
    public void tickStart(EnumSet<TickType> tickTypes, Object... objects) {

    }

    @Override
    public void tickEnd(EnumSet<TickType> tickTypes, Object... objects) {
        while(!PacketManager.getProcessQueue().isEmpty()){
            IpcPacket packet = PacketManager.getProcessQueue().poll();
            packet.processPacket();
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel() {
        return "Nailed|IpcPacketProcessor";
    }
}
