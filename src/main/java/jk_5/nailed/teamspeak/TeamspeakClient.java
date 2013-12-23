package jk_5.nailed.teamspeak;

import com.google.common.base.Splitter;
import jk_5.nailed.NailedModContainer;
import jk_5.nailed.players.Player;
import jk_5.nailed.players.PlayerRegistry;
import jk_5.nailed.players.Team;
import jk_5.nailed.teamspeak.api.de.stefan1200.jts3serverquery.JTS3ServerQuery;
import jk_5.nailed.teamspeak.api.de.stefan1200.jts3serverquery.TeamspeakActionListener;
import jk_5.nailed.util.config.ConfigTag;
import lombok.Getter;
import lombok.Setter;
import net.minecraftforge.common.MinecraftForge;

import java.util.HashMap;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class TeamspeakClient extends Thread implements TeamspeakActionListener {

    @Getter @Setter private boolean enabled = false;
    private final String host;
    private final int port;
    private final String username;
    private final String password;

    private JTS3ServerQuery api;

    private int lobbyID;
    private int parentID;

    public TeamspeakClient(){
        ConfigTag tag = NailedModContainer.getConfig().getTag("teamspeak").useBraces();
        this.enabled = tag.getTag("enabled").getBooleanValue(false);
        this.host = tag.getTag("host").getValue("");
        this.port = tag.getTag("port").getIntValue(10011);
        this.username = tag.getTag("username").getValue("serveradmin");
        this.password = tag.getTag("password").getValue("");

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void connect(){
        if(this.enabled) this.start();
    }

    @Override
    public void run(){
        this.api = new JTS3ServerQuery();
        this.api.connectTS3Query(this.host, this.port);
        this.api.loginTS3(this.username, this.password);
        this.api.selectVirtualServer(1);
        this.api.setDisplayName("Nailed");
        this.api.setTeamspeakActionListener(this);
        this.api.addEventNotify(JTS3ServerQuery.EVENT_MODE_SERVER, 0);

        this.refreshChannelIDs();
        this.refreshClients();

        while(this.enabled){
            try{
                Thread.sleep(1000);
            }catch(Exception e){

            }
        }
    }

    public void refreshChannelIDs(){
        for(String channel : Splitter.on('|').split(this.api.doCommand("channellist -topic").get("response"))){
            int id = -1;
            for(String e : Splitter.on(' ').split(channel)){
                String[] eS = e.split("=");
                if(eS.length == 2){
                    if(eS[0].equals("cid")){
                        id = Integer.parseInt(eS[1]);
                    }else if(eS[0].equals("channel_topic")){
                        if(eS[0].equals("channel_topic") && eS[1].equals("[nailed.lobby]")){
                            lobbyID = id;
                        }else if(eS[0].equals("channel_topic") && eS[1].equals("[nailed.parent]")){
                            parentID = id;
                        }
                    }
                }
            }
        }
    }

    public void refreshClients(){
        for(String client : Splitter.on('|').split(this.api.doCommand("clientlist").get("response"))){
            int id = -1;
            String nickname = "";
            for(String e : Splitter.on(' ').split(client)){
                String[] eS = e.split("=");
                if(eS.length == 2){
                    if(eS[0].equals("clid")){
                        id = Integer.parseInt(eS[1]);
                    }else if(eS[0].equals("client_nickname")){
                        nickname = eS[1];
                    }
                }
            }
            Player player = PlayerRegistry.instance().getPlayer(nickname);
            if(player != null) player.setTeamSpeakClientID(id);
        }
    }

    public static void main(String[] args) throws Exception{
        int parentID = -1;
        int lobbyID = -1;

        JTS3ServerQuery ts = new JTS3ServerQuery();
        ts.connectTS3Query("192.168.178.20", 10011);
        ts.loginTS3("serveradmin", System.getProperty("ts3password"));
        ts.selectVirtualServer(1);
        ts.setDisplayName("Nailed");
        for(String channel : Splitter.on('|').split(ts.doCommand("channellist -topic").get("response"))){
            int id = -1;
            for(String e : Splitter.on(' ').split(channel)){
                String[] eS = e.split("=");
                if(eS.length == 2){
                    if(eS[0].equals("cid")){
                        id = Integer.parseInt(eS[1]);
                    }else if(eS[0].equals("channel_topic")){
                        if(eS[0].equals("channel_topic") && eS[1].equals("[nailed.lobby]")){
                            lobbyID = id;
                        }else if(eS[0].equals("channel_topic") && eS[1].equals("[nailed.parent]")){
                            parentID = id;
                        }
                    }
                }
            }
        }
        System.out.println("LobbyID = " + lobbyID);
        System.out.println("ParentID = " + parentID);

        int chanid = Integer.parseInt(ts.doCommand("channelcreate channel_name=Nailed.3.teamblue channel_topic=[nailed.teamChannel.3.teamblue] channel_order=0 cpid=" + parentID).get("response").split("=")[1]);
        System.out.println(chanid);

        for(String client : Splitter.on('|').split(ts.doCommand("clientlist").get("response"))){
            int id = -1;
            String nickname = "";
            for(String e : Splitter.on(' ').split(client)){
                String[] eS = e.split("=");
                if(eS.length == 2){
                    if(eS[0].equals("clid")){
                        id = Integer.parseInt(eS[1]);
                    }else if(eS[0].equals("client_nickname")){
                        nickname = eS[1];
                    }
                }
            }

            System.out.println(nickname + " = " + id);
            ts.moveClient(id, chanid, null);
        }

        Thread.sleep(30000);
    }

    public static void print(Map<String, String> map){
        for(Map.Entry<String, String> e : map.entrySet()){
            System.out.println(e.getKey() + " = " + e.getValue());
        }
    }

    public void createChannelFor(Team team){
        String name = "Nailed." + team.getMap().getID() + "." + team.getTeamId();
        //int chanid = Integer.parseInt(this.api.doCommand("channelcreate channel_name=" + name + " channel_order=0 cpid=" + this.parentID).get("response").split("=")[1]);
        //team.setTeamSpeakChannelID(chanid);
    }

    public void removeChannel(Team team){
        if(team.getTeamSpeakChannelID() == -1) return;
        //this.api.doCommand("channeldelete cid=" + team.getTeamSpeakChannelID() + " force=1");
        //team.setTeamSpeakChannelID(-1);
    }

    public void movePlayersIntoChannel(Team team){
        if(team.getTeamSpeakChannelID() == -1) return;
        for(Player player : team.getMembers()){
            if(player.getTeamSpeakClientID() != -1){
                this.api.moveClient(player.getTeamSpeakClientID(), team.getTeamSpeakChannelID(), null);
            }
        }
    }

    public void movePlayersToLobby(Team team){
        if(this.lobbyID == -1) return;
        for(Player player : team.getMembers()){
            if(player.getTeamSpeakClientID() != -1){
                this.api.moveClient(player.getTeamSpeakClientID(), this.lobbyID, null);
            }
        }
    }

    @Override
    public void teamspeakActionPerformed(String eventType, HashMap<String, String> eventInfo){
        if(eventType.equalsIgnoreCase("notifycliententerview")){
            Player player = PlayerRegistry.instance().getPlayer(eventInfo.get("client_nickname"));
            if(player != null){
                player.setTeamSpeakClientID(Integer.parseInt(eventInfo.get("clid")));
            }
        }else if(eventType.equalsIgnoreCase("notifyclientleftview")){
            int clid = Integer.parseInt(eventInfo.get("clid"));
            for(Player player : PlayerRegistry.instance().getPlayers()){
                if(player.getTeamSpeakClientID() == clid){
                    player.setTeamSpeakClientID(-1);
                }
            }
        }
    }
}
