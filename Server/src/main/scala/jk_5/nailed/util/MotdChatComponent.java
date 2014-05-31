package jk_5.nailed.util;

import net.minecraft.util.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class MotdChatComponent extends ChatComponentText {

    private String motdText;

    public MotdChatComponent(String text) {
        super("");
        this.motdText = text;
    }

    @Override
    public String getChatComponentText_TextValue() {
        return this.motdText;
    }

    public void setText(String text){
        this.motdText = text;
    }

    @Override
    public ChatComponentText createCopy() {
        MotdChatComponent ret = new MotdChatComponent(this.motdText);
        ret.setChatStyle(this.getChatStyle().createShallowCopy());

        //noinspection unchecked
        for(IChatComponent comp : (Iterable<IChatComponent>) this.getSiblings()){
            ret.appendSibling(comp.createCopy());
        }

        return ret;
    }

    @Override
    public String toString() {
        return "MotdChatComponent{" +
                "text='" + motdText + "\', " +
                "siblings=" + this.siblings + ", " +
                "style=" + this.getChatStyle() +
                "}";
    }
}
