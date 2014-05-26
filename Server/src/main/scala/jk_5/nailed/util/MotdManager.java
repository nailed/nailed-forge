package jk_5.nailed.util;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.network.*;
import net.minecraft.server.*;
import net.minecraft.util.*;

import cpw.mods.fml.common.eventhandler.*;
import cpw.mods.fml.common.gameevent.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MotdManager {

    private final List<String> comments = ImmutableList.of(
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
    private final Random rand = new Random();
    private final ServerStatusResponse data = MinecraftServer.getServer().func_147134_at();
    private ChatComponentText motd;

    @SubscribeEvent
    public void onEvent(TickEvent.ServerTickEvent event) {
        if(event.phase == TickEvent.Phase.START){
            if(this.motd == null){
                this.motd = (ChatComponentText) data.func_151317_a();
            }
            String comment = this.comments.get(this.rand.nextInt(this.comments.size()));
            this.motd.text = ChatColor.AQUA + "Nailed " + ChatColor.GOLD + "| " + ChatColor.WHITE + "Quakecraft is up and running again!\n" + ChatColor.GRAY + comment;
        }
    }
}
