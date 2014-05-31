package jk_5.nailed.util;

import java.util.*;

import com.google.common.collect.*;

import io.netty.channel.*;

import net.minecraft.network.*;
import net.minecraft.network.status.server.*;
import net.minecraft.server.*;
import net.minecraft.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MotdManager {

    private static final List<String> comments = ImmutableList.of(
            "Well, that escalated quickly!",
            "Let\'s go!",
            "Oh well...",
            "Hello world!",
            "That\'s me!",
            "Oh god why!?",
            "Oh i hate the teams!",
            "FUCK THIS SHIT!",
            "I hate you!",
            "Kill them all!",
            "Blow it up!",
            "Fix yo laggz bro!",
            "Where\'s the enderpearl?",
            "It\'s opensource!",
            "Gimme starfall!",
            ChatColor.MAGIC + "FUNKY SHIT!",
            "Now 99% bug-free!",
            "Using netty!",
            "Booo!",
            "1.7.2 now!"
    );
    private static final Random rand = new Random();

    public static IChatComponent motdComponent(){
        String comment = comments.get(rand.nextInt(comments.size()));
        return new ChatComponentText(ChatColor.AQUA + "Nailed " + ChatColor.GOLD + "| " + ChatColor.WHITE + "Quakecraft is up and running again!\n" + ChatColor.GRAY + comment);
    }

    public static void sendMotd(Channel channel, String host) {
        ServerStatusResponse response = new ServerStatusResponse();
        response.func_151319_a(MinecraftServer.getServer().func_147134_at().func_151318_b());
        response.func_151321_a(new ServerStatusResponse.MinecraftProtocolVersionIdentifier("Nailed-1.7.2", protocolVersion()));
        response.func_151315_a(motdComponent());
        response.func_151320_a(MinecraftServer.getServer().func_147134_at().func_151316_d());
        channel.writeAndFlush(new S00PacketServerInfo(response));
    }

    private static int protocolVersion(){
        return MinecraftServer.getServer().func_147134_at().func_151322_c().func_151304_b();
    }
}
