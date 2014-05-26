package jk_5.nailed.chat.joinmessage;

import java.lang.management.*;
import java.text.*;
import java.util.*;

import net.minecraft.event.*;
import net.minecraft.util.*;

import jk_5.nailed.api.player.*;
import jk_5.nailed.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IReplacement {

    DateFormat format = new SimpleDateFormat("EEEE d MMMMM yyyy HH:mm:ss");

    IChatComponent getComponent(Player player);

    public static class PlayerName implements IReplacement {

        @Override
        public IChatComponent getComponent(Player player) {
            return new ChatComponentText(Utils.stripFormattingCodes(player.getWebUser().getFullName()));
        }
    }

    public static class Uptime implements IReplacement {

        @Override
        public IChatComponent getComponent(Player player) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MILLISECOND, -(int) ManagementFactory.getRuntimeMXBean().getUptime());
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
