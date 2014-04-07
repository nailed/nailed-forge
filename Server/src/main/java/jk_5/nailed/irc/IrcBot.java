package jk_5.nailed.irc;

import com.google.common.collect.Lists;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import jk_5.asyncirc.IrcConnection;
import jk_5.nailed.NailedLog;
import jk_5.nailed.util.config.ConfigTag;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class IrcBot {

    private IrcConnection connection;

    private String name;
    private boolean enabled = false;
    private String nickname;
    private String loginname;
    private String realname;
    private String host;
    private int port;
    private boolean ssl;
    private String serverPassword;
    private String nickservPassword;
    private final List<IrcChannel> channels = Lists.newArrayList();

    public IrcBot(ConfigTag config){
        this.name = config.name;
        this.enabled = config.getTag("enabled").getBooleanValue(true);
        this.nickname = config.getTag("nickname").getValue("Nailed");
        this.loginname = config.getTag("loginname").getValue("Nailed");
        this.realname = config.getTag("realname").getValue("Nailed");
        this.host = config.getTag("host").getValue("");
        this.port = config.getTag("port").getIntValue(6667);
        this.ssl = config.getTag("ssl").getBooleanValue(false);
        this.serverPassword = config.getTag("password").getValue("");
        this.nickservPassword = config.getTag("nickserv").getValue("");
        ConfigTag channelTag = config.getTag("channels").useBraces();
        for(ConfigTag tag : channelTag.getSortedTagList()){
            tag.useBraces();
            this.channels.add(IrcChannel.read(tag));
        }
    }

    public void connect(NioEventLoopGroup executors){
        this.connection = new IrcConnection(this.host, this.port)
                .setLoginName(this.loginname)
                .setNickname(this.nickname)
                .setRealName(this.realname)
                .setServerPassword(this.serverPassword)
                .setNickservPassword(this.nickservPassword)
                .connect(executors)
                .addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture future) throws Exception{
                        NailedLog.info("Connected to server {}", name);
                    }
                });
        this.joinChannels();
    }

    private void joinChannels(){
        for(IrcChannel channel : this.channels){
            channel.setConversation(this.connection.joinChannel(channel.getName()).conversation());
            channel.getConversation().addListener(new IrcListener(channel));
        }
    }

    public void close(){
        this.connection.close();
    }
}
