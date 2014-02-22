package jk_5.nailed.chat.joinmessage;

import jk_5.nailed.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IReplacement {
    public IChatComponent getComponent(EntityPlayer player);

    public static class PlayerName implements IReplacement{
        @Override
        public IChatComponent getComponent(EntityPlayer player){
            return new ChatComponentText(Utils.stripFormattingCodes(player.getDisplayName()));
        }
    }
}
