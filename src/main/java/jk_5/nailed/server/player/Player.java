package jk_5.nailed.server.player;

import jk_5.nailed.network.packets.NailedPacket;
import jk_5.nailed.network.packets.PacketTimeTracker;
import jk_5.nailed.util.ChatColor;
import jk_5.nailed.util.Utils;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

/**
 * No description given
 *
 * @author jk-5
 */
public class Player {

    private final String username;

    public Player(String username){
        this.username = username;
    }

    public void setTimeLeft(int seconds){
        this.sendPacket(new PacketTimeTracker("Time left: " + ChatColor.GREEN + Utils.secondsToShortTimeString(seconds)));
    }

    public void sendPacket(NailedPacket packet){
        this.getEntity().playerNetServerHandler.sendPacketToPlayer(packet.getPacket());
    }

    public EntityPlayerMP getEntity(){
        return MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(this.username);
    }
}