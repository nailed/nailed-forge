package jk_5.nailed.util;

import jk_5.nailed.api.ChatColor;

/**
 * No description given
 *
 * @author jk-5
 */
public class Utils {

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
}
