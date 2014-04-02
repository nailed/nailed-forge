package jk_5.nailed.irc;

import com.google.common.collect.ImmutableMap;
import jk_5.asyncirc.User;
import jk_5.nailed.util.Utils;
import net.minecraft.event.HoverEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import org.jibble.pircbot.Colors;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class IrcHelper {

    private static ServerConfigurationManager configManager = MinecraftServer.getServer().getConfigurationManager();

    private static final Map<String, EnumChatFormatting> colors = ImmutableMap.<String, EnumChatFormatting>builder()
            .put(Colors.BLACK, EnumChatFormatting.WHITE)
            .put(Colors.BLUE, EnumChatFormatting.BLUE)
                    //.put(Colors.BOLD, EnumChatFormatting.BOLD)
            .put(Colors.BROWN, EnumChatFormatting.GOLD)
            .put(Colors.CYAN, EnumChatFormatting.AQUA)
            .put(Colors.DARK_BLUE, EnumChatFormatting.DARK_BLUE)
            .put(Colors.DARK_GRAY, EnumChatFormatting.DARK_GRAY)
            .put(Colors.DARK_GREEN, EnumChatFormatting.DARK_GREEN)
            .put(Colors.GREEN, EnumChatFormatting.GREEN)
            .put(Colors.LIGHT_GRAY, EnumChatFormatting.GRAY)
            .put(Colors.MAGENTA, EnumChatFormatting.LIGHT_PURPLE)
            .put(Colors.NORMAL, EnumChatFormatting.RESET)
            .put(Colors.OLIVE, EnumChatFormatting.DARK_GREEN)
            .put(Colors.PURPLE, EnumChatFormatting.DARK_PURPLE)
            .put(Colors.RED, EnumChatFormatting.RED)
            .put(Colors.TEAL, EnumChatFormatting.DARK_AQUA)
                    //.put(Colors.UNDERLINE, EnumChatFormatting.UNDERLINE)
            .put(Colors.WHITE, EnumChatFormatting.BLACK)
            .put(Colors.YELLOW, EnumChatFormatting.YELLOW).build();

    private static void append(IChatComponent component, String message){
        IChatComponent comp;
        String msg = "";
        EnumChatFormatting color = null;
        boolean bold = false, underline = false, italic = false;
        for(int i = 0; i < message.length(); i++){
            char current = message.charAt(i);
            if(current == '\u0003'){ //Every IRC colorcode starts with this magic char
                if(msg.length() > 0){ //Write everything we read
                    comp = new ChatComponentText(msg);
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    component.appendSibling(comp);
                }
                //if(msg.length() < i + 1) break;
                String code = current + "" + message.charAt(i + 1) + "" + message.charAt(i + 2);
                msg = "";
                color = colors.get(code);
                i += 2;
            }else if(current == '\u000F'){ //Reset
                if(msg.length() > 0){ //Write everything we read
                    comp = new ChatComponentText(msg);
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    component.appendSibling(comp);
                }
                msg = "";
                color = null;
                underline = italic = bold = false;
            }else if(current == '\u0002'){ //Bold
                if(msg.length() > 0){ //Write everything we read
                    comp = new ChatComponentText(msg);
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    component.appendSibling(comp);
                }
                msg = "";
                color = null;
                bold = true;
            }else if(current == '\u001F'){ //Underline
                if(msg.length() > 0){ //Write everything we read
                    comp = new ChatComponentText(msg);
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    component.appendSibling(comp);
                }
                msg = "";
                color = null;
                underline = true;
            }else if(current == '\u0016'){ //Reverse / italic (We use italic)
                if(msg.length() > 0){ //Write everything we read
                    comp = new ChatComponentText(msg);
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    component.appendSibling(comp);
                }
                msg = "";
                color = null;
                italic = true;
            }else{
                msg += current;
            }
        }
        if(msg.length() > 0){
            comp = new ChatComponentText(msg);
            comp.getChatStyle().setColor(color);
            comp.getChatStyle().setBold(bold);
            comp.getChatStyle().setItalic(italic);
            comp.getChatStyle().setUnderlined(underline);
            component.appendSibling(comp);
        }
        //for(IChatComponent c : (List<IChatComponent>) component.getSiblings()){
        //http(s)?://(([A-Za-z0-9]+)\.)+([A-Za-z0-9]{2,4})(/)?
        //}
    }

    public static void sendIrcChatToGame(IrcChannel channel, User sender, String message){
        ChatComponentText component = new ChatComponentText("");
        IChatComponent comp = new ChatComponentText("[" + channel.getName() + "]");
        comp.getChatStyle().setColor(EnumChatFormatting.GRAY);
        component.appendSibling(comp);
        component.appendText(" <");
        comp = new ChatComponentText(sender.getNickName());                                                     //TODO: loginname   //TODO: hostname
        comp.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(sender.getNickName() + "@")));
        component.appendSibling(comp);
        message = "> " + message;
        append(component, message);
        Utils.minifyChatComponent(component);
        configManager.sendChatMsg(component);
    }
}
