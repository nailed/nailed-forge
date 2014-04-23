package jk_5.nailed.chat.joinmessage;

import jk_5.nailed.api.player.Player;
import jk_5.nailed.util.Utils;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IReplacement {
    public IChatComponent getComponent(Player player);

    final static DateFormat format = new SimpleDateFormat("EEEE d MMMMM yyyy HH:mm:ss");

    public static class PlayerName implements IReplacement{
        @Override
        public IChatComponent getComponent(Player player){
            return new ChatComponentText(Utils.stripFormattingCodes(player.getWebUser().getFullName()));
        }
    }

    public static class Uptime implements IReplacement{
        @Override
        public IChatComponent getComponent(Player player){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, - (int) ManagementFactory.getRuntimeMXBean().getUptime());
            ChatComponentText comp = new ChatComponentText(Utils.parseTime((int) ManagementFactory.getRuntimeMXBean().getUptime() / 1000));
            ChatComponentText inner = new ChatComponentText("Up since ");
            ChatComponentText time = new ChatComponentText(format.format(calendar.getTime()));
            time.getChatStyle().setColor(EnumChatFormatting.GRAY);
            inner.appendSibling(time);
            HoverEvent event = new HoverEvent(HoverEvent.Action.SHOW_TEXT, inner);
            comp.getChatStyle().setChatHoverEvent(event);
            return comp;
        }
    }
}
