package jk_5.nailed.util;

import java.util.*;

import com.google.common.collect.*;

import net.minecraft.server.*;

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
    private MotdChatComponent motd = new MotdChatComponent("");
    public static boolean firstTick = false;

    @SubscribeEvent
    public void onEvent(TickEvent.ServerTickEvent event) {
        if(firstTick){
            MinecraftServer.getServer().func_147134_at().func_151315_a(this.motd);
        }
        if(event.phase == TickEvent.Phase.START){
            String comment = this.comments.get(this.rand.nextInt(this.comments.size()));
            this.motd.setText(ChatColor.AQUA + "Nailed " + ChatColor.GOLD + "| " + ChatColor.WHITE + "Quakecraft is up and running again!\n" + ChatColor.GRAY + comment);
        }
    }
}
