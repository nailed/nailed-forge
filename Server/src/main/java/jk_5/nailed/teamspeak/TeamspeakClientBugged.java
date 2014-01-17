/*package jk_5.nailed.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.event.DefaultListener;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.google.common.collect.Maps;

import java.util.HashMap;

public class TeamspeakClientBugged {

    private final TS3Api api;
    private Channel lobbyChannel;

    public TeamspeakClientBugged() throws Exception {
        this.api = new TS3Query("192.168.178.20", TS3Query.DEFAULT_PORT, TS3Query.FloodRate.DEFAULT).connect().getApi();
        this.api.login("serveradmin", System.getProperty("ts3password"));
        this.api.selectVirtualServerById(1);
        this.api.setNickname("Nailed");
        this.api.addTS3Listeners(new DefaultListener());
        this.api.registerAllEvents();
        for(Channel channel : this.api.getChannels()){
            if(channel.getTopic().equals("[nailed.lobby]")){
                this.lobbyChannel = channel;
            }
        }
        System.out.println("Channel: " + this.lobbyChannel.getId());
        System.out.println("Parent: " + this.lobbyChannel.getParentChannelId());
        System.out.println("Order: " + this.lobbyChannel.getOrder());
        HashMap<ChannelProperty, String> properties = Maps.newHashMap();
        properties.put(ChannelProperty.CHANNEL_TOPIC, "[nailed.teamChannel1]");
        properties.put(ChannelProperty.CHANNEL_ORDER, "0");
        properties.put(ChannelProperty.PID, this.lobbyChannel.getId() + "");
        this.api.createChannel("Nailed.3.teamred", properties);
        Channel chan = this.api.getChannelByName("Nailed.3.teamred");
        System.out.println("Channel: " + chan.getId());
        System.out.println("Parent: " + chan.getParentChannelId());
        System.out.println("Order: " + chan.getOrder());
    }

    public static void main(String[] args) throws Exception {
        new TeamspeakClientBugged();
    }
}*/
