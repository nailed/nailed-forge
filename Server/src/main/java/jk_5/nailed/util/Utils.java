package jk_5.nailed.util;

import jk_5.nailed.api.ChatColor;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * No description given
 *
 * @author jk-5
 */
public class Utils {

    private static final Pattern formattingCodePattern = Pattern.compile("(?i)" + String.valueOf('\u00a7') + "[0-9A-FK-OR]");

    public static String secondsToShortTimeString(long secs){
        long hours = secs / 3600;
        long minutes = (secs % 3600) / 60;
        long seconds = secs % 60;
        StringBuilder builder = new StringBuilder();
        boolean append = false;
        if(hours != 0 || append){
            builder.append(hours);
            builder.append(":");
            append = true;
        }
        if(minutes != 0 || append){
            builder.append(minutes);
            builder.append(":");
            append = true;
        }
        if(seconds != 0 || append){
            if(seconds < 10 && append) builder.append("0");
            builder.append(seconds);
            if(!append){
                builder.append(ChatColor.RESET);
                builder.append(" Second");
                if(seconds != 1) builder.append("s");
            }
        }
        if(seconds == 0 && !append) builder.append("0 Seconds");
        return builder.toString();
    }

    public static String secondsToLongTimeString(long secs){
        if (secs < 60) return String.format("%d second%s", secs, (secs == 1) ? "" : "s");
        long hours = secs / 3600;
        long minutes = (secs % 3600) / 60;
        long seconds = secs % 60;
        StringBuilder builder = new StringBuilder();
        boolean hasText = false;
        if(hours > 0){
            builder.append(hours);
            builder.append(" hour");
            if(hours > 1) builder.append("s");
            hasText = true;
        }
        if(minutes > 0){
            if (hasText){
                if(secs > 0) builder.append(", ");
                else builder.append(" and ");
            }
            builder.append(minutes);
            builder.append(" minute");
            if(minutes > 1) builder.append("s");
            hasText = true;
        }
        if(seconds > 0){
            if (hasText) builder.append(" and ");
            builder.append(seconds);
            builder.append(" second");
            if(seconds > 1) builder.append("s");
            hasText = true;
        }
        if(!hasText) builder.append("0 seconds");
        return builder.toString();
    }

    public static String formatColors(String message){
        char[] b = message.toCharArray();
        for(int i = 0; i < b.length - 1; i++){
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1){
                b[i] = '\u00a7';
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }
        return new String(b);
    }

    public static String replaceAllIgnoreCase(String text, String search, String replacement){
        if(search.equals(replacement)){
            return text;
        }
        StringBuilder buffer = new StringBuilder(text);
        String lowerSearch = search.toLowerCase();
        int i;
        int prev = 0;
        while((i = buffer.toString().toLowerCase().indexOf(lowerSearch, prev)) > -1){
            buffer.replace(i, i + search.length(), replacement);
            prev = i + replacement.length();
        }
        return buffer.toString();
    }

    public static String parseTime(int seconds){
        StringBuilder builder = new StringBuilder();
        int weeks = seconds / (86400 * 7);
        seconds = seconds % (86400 * 7);
        int days = seconds / 86400;
        seconds = seconds % 86400;
        int hours = seconds / 3600;
        seconds = seconds % 3600;
        int minutes = seconds / 60;
        seconds = seconds % 60;

        if(weeks > 0){
            builder.append(weeks);
            builder.append(" week");
            if(weeks != 1) builder.append("s");
            builder.append(" ");
        }
        if(days > 0){
            builder.append(days);
            builder.append(" day");
            if(days != 1) builder.append("s");
            builder.append(" ");
        }
        if(hours > 0){
            builder.append(hours);
            builder.append(" hour");
            if(hours != 1) builder.append("s");
            builder.append(" ");
        }
        if(minutes > 0){
            builder.append(minutes);
            builder.append(" minute");
            if(minutes != 1) builder.append("s");
            builder.append(" ");
        }
        builder.append(seconds);
        builder.append(" second");
        if(seconds != 1) builder.append("s");
        return builder.toString().trim();
    }

    public static String leadingZero(int number){
        StringBuilder builder = new StringBuilder();
        if(number > 10) builder.append("0");
        builder.append(number);
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    public static IChatComponent minifyChatComponent(IChatComponent component){
        boolean isRoot = component.getChatStyle().parentStyle == ChatStyle.rootStyle || component.getChatStyle().parentStyle == null;
        if(isRoot){
            //Default color is white. When white is specified, remove it, it's unneeded
            ChatStyle style = component.getChatStyle();
            if(style.color == EnumChatFormatting.WHITE || style.color == EnumChatFormatting.RESET){
                style.color = null;
            }
        }else{
            ChatStyle style = component.getChatStyle();
            if(style != null){
                ChatStyle parent = style.parentStyle;
                if(parent == null) parent = ChatStyle.rootStyle;
                if(parent.parentStyle == null) parent.parentStyle = ChatStyle.rootStyle;
                if(style.color == parent.getColor()){
                    style.color = null; //Don't specify the color ourselves when it's the same as the parent
                }
                if((style.color == EnumChatFormatting.WHITE || style.color == EnumChatFormatting.RESET) && parent.getColor() == null){
                    style.color = null;
                }
                if(style.bold != null && style.bold == parent.getBold()){
                    style.bold = null;
                }
                if(style.underlined != null && style.underlined == parent.getUnderlined()){
                    style.underlined = null;
                }
                if(style.strikethrough != null && style.strikethrough == parent.getStrikethrough()){
                    style.strikethrough = null;
                }
                if(style.italic != null && style.italic == parent.getItalic()){
                    style.italic = null;
                }
                if(style.obfuscated != null && style.obfuscated == parent.getObfuscated()){
                    style.obfuscated = null;
                }
            }
        }
        Iterator<IChatComponent> it = component.getSiblings().iterator();
        while(it.hasNext()){
            IChatComponent comp = it.next();
            if(comp.getUnformattedTextForChat().isEmpty()){
                it.remove();
            }
            minifyChatComponent(comp);
        }
        if(component.getChatStyle().getChatHoverEvent() != null){
            minifyChatComponent(component.getChatStyle().getChatHoverEvent().getValue());
        }
        return component;
    }

    public static String stripFormattingCodes(String input){
        return formattingCodePattern.matcher(input).replaceAll("");
    }
}
