package jk_5.nailed.chat.joinmessage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import jk_5.nailed.NailedLog;
import jk_5.nailed.util.Utils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

/**
 * No description given
 * 
 * @author jk-5
 */
public class JoinMessageSender {
    
    private final List<String> message = Lists.newArrayList();
    private final Map<String, IReplacement> replacements = Maps.newHashMap();

    public void readConfig(File configDir){
        this.replacements.clear();
        this.replacements.put("playername", new IReplacement.PlayerName());
        this.replacements.put("uptime", new IReplacement.Uptime());

        this.message.clear();
        File configFile = new File(configDir, "joinmessage.cfg");
        if(configFile.exists()){
            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new FileReader(configFile));
                while(reader.ready()){
                    String line = reader.readLine().trim();
                    if(!line.startsWith("#") && !line.isEmpty()){
                        this.message.add(line);
                    }
                }
            }catch(Exception e){
                NailedLog.error(e, "Error while reading join message config file");
            }finally{
                IOUtils.closeQuietly(reader);
            }
        }else{
            PrintWriter writer = null;
            try{
                configFile.createNewFile();
                writer = new PrintWriter(configFile);
                
                writer.println("# This file contains the message that is sent to the player on login.");
                writer.println("# Lines starting with a # are comments and are ignored.");
                writer.println("# If you want to color the text, use & and a color code after that");
                writer.println("# For a list of color codes, see http://minecraft.gamepedia.com/Formatting_codes");
                writer.println("# We also added some other handy codes you can use:");
                writer.println("# ${playername} will be replaced by the name of the player logging in");
                writer.println("#");
                writer.println("# If you want more codes, message me (jk-5) on irc.esper.net, or open an issue on https://github.com/nailed/nailed-forge/issues");
                writer.println("");
                writer.println("&6Welcome &3${playername}&6 to Nailed");
                writer.println("&6Uptime: &7${uptime}");
            }catch(Exception e){
                NailedLog.error(e, "Error while writing default join message config file");
            }finally{
                IOUtils.closeQuietly(writer);
            }
            this.readConfig(configDir);
        }
    }
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(PermissionsManager.checkPerm(event.player, "nailed.joinmessage")){
            for(String line : this.message){
                event.player.addChatComponentMessage(this.format(line, event.player));
            }
        }
    }

    public void registerPermissions(){
        PermissionsManager.registerPermission("nailed.joinmessage", RegisteredPermValue.TRUE);
    }

    private IChatComponent format(String line, EntityPlayer player){
        IChatComponent component = new ChatComponentText("");
        line = Utils.formatColors(line);
        ChatStyle parentStyle = component.getChatStyle();
        parentStyle.setColor(EnumChatFormatting.WHITE);
        char chars[] = line.toCharArray();
        StringBuilder buffer = new StringBuilder("");
        EnumChatFormatting color = null;
        boolean bold = false;
        boolean italic = false;
        boolean underline = false;
        boolean strike = false;
        boolean random = false;
        for(int i = 0; i < chars.length - 1; i++){
            if(chars[i] == '$' && chars[i + 1] == '{'){
                //We found a replacement code!
                int endIndex = line.indexOf('}', i + 1);
                if(endIndex == -1){
                    buffer.append(chars[i]);
                    continue;
                }
                String key = line.substring(i + 2, endIndex);
                IReplacement replacement = this.replacements.get(key);
                IChatComponent comp = replacement.getComponent(player);
                comp.getChatStyle().setColor(color);
                comp.getChatStyle().setBold(bold);
                comp.getChatStyle().setItalic(italic);
                comp.getChatStyle().setUnderlined(underline);
                comp.getChatStyle().setStrikethrough(strike);
                comp.getChatStyle().setObfuscated(random);
                component.appendSibling(comp);
                i = endIndex;// - 1;
            }else if(chars[i] == '\u00a7' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(chars[i + 1]) > -1){
                //We found a legacy formatting code!
                char colorCode = Character.toLowerCase(chars[i + 1]);
                if("0123456789abcdef".indexOf(colorCode) > -1){
                    //It's a color code
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    color = (EnumChatFormatting) EnumChatFormatting.formattingCodeMapping.get(colorCode);
                    bold = italic = underline = strike = random = false; //All formatting codes will get reset after a color code
                }else if(colorCode == 'k'){
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    random = true;
                }else if(colorCode == 'l'){
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    bold = true;
                }else if(colorCode == 'm'){
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    strike = true;
                }else if(colorCode == 'n'){
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    underline = true;
                }else if(colorCode == 'o'){
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    italic = true;
                }else if(colorCode == 'r'){
                    ChatComponentText comp = new ChatComponentText(buffer.toString());
                    buffer = new StringBuilder("");
                    comp.getChatStyle().setColor(color);
                    comp.getChatStyle().setBold(bold);
                    comp.getChatStyle().setItalic(italic);
                    comp.getChatStyle().setUnderlined(underline);
                    comp.getChatStyle().setStrikethrough(strike);
                    comp.getChatStyle().setObfuscated(random);
                    component.appendSibling(comp);
                    color = EnumChatFormatting.WHITE;
                    bold = italic = underline = strike = random = false;
                }else{
                    buffer.append(chars[i]);
                    buffer.append(chars[i + 1]);
                }
                i++;
            }else{
                //It's just text. Just add the char to the buffer
                buffer.append(chars[i]);
                if(chars.length - 2 == i){
                    buffer.append(chars[i + 1]);
                }
            }
        }
        if(buffer.length() > 0){
            ChatComponentText comp = new ChatComponentText(buffer.toString());
            comp.getChatStyle().setColor(color);
            comp.getChatStyle().setBold(bold);
            comp.getChatStyle().setItalic(italic);
            comp.getChatStyle().setUnderlined(underline);
            comp.getChatStyle().setStrikethrough(strike);
            comp.getChatStyle().setObfuscated(random);
            component.appendSibling(comp);
        }
        Utils.minifyChatComponent(component);
        return component;
    }
}
