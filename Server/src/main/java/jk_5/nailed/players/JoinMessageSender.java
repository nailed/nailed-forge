package jk_5.nailed.players;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import jk_5.nailed.NailedLog;

import net.minecraftforge.permissions.api.PermissionsManager;
import net.minecraftforge.permissions.api.RegisteredPermValue;

import org.apache.commons.io.IOUtils;

/**
 * No description given
 * 
 * @author jk-5
 */
public class JoinMessageSender {
    
    private final List<String> message = Lists.newArrayList();
    
    public JoinMessageSender(){
        PermissionsManager.registerPermission("nailed.joinmessage", RegisteredPermValue.TRUE);
    }
    
    public void readConfig(File configDir){
        this.message.clear();
        File configFile = new File(configDir, "joinmessage.cfg");
        if(configFile.exists()){
            BufferedReader reader = null;
            try{
                reader = new BufferedReader(new FileReader(configFile));
                while(reader.ready()){
                    String line = reader.readLine().trim();
                    if(!line.startsWith("#" && !line.isEmpty()){
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
                writer.println("# {playername} will be replaced by the name of the player logging in");
                writer.println("#");
                writer.println("# If you want more codes, message me (jk-5) on irc.esper.net, or open an issue on https://github.com/nailed/nailed-forge/issues");
                writer.println("");
                writer.println("Welcome {playername} to Nailed");
            }catch(Exception e){
                NailedLog.error(e, "Error while writing default join message config file");
            }finally{
                IOUtils.closeQuietly(reader);
            }
            this.readConfig(configDir);
        }
    }
    
    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        if(PermissionsManager.checkPerm(event.player, "nailed.joinmessage")){
            for(String line : this.message){
                event.player.addChatComponentMessage(new ChatComponentText(line.replace('&', '\u00A7')));
            }
        }
    }
}
