package jk_5.nailed.util;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import jk_5.nailed.api.ChatColor;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;

import java.util.List;
import java.util.Random;

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
    public void onEvent(TickEvent.ServerTickEvent event){
        if(event.phase == TickEvent.Phase.START){
            if(this.motd == null){
                this.motd = (ChatComponentText) data.func_151317_a();
            }
            String comment = this.comments.get(this.rand.nextInt(this.comments.size()));
            this.motd.text = ChatColor.AQUA + "Nailed " + ChatColor.GOLD + "| " + ChatColor.WHITE + "New Game: " + ChatColor.YELLOW + ChatColor.BOLD + "Together we Cry \n" + ChatColor.GRAY + comment;
        }
    }
}
