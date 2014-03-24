package jk_5.nailed.irc;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.nio.NioEventLoopGroup;
import jk_5.asyncirc.IrcConnection;
import jk_5.nailed.NailedLog;
import jk_5.nailed.util.config.ConfigTag;

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
            tag.getTag("password").getValue("");
            tag.getTag("autojoin").getBooleanValue(true);
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
                        NailedLog.info("Connected to server " + name);
                    }
                });
    }

    private void joinChannels(){
        connection.joinChannel("#server").awaitUninterruptibly().conversation().sendMessage("HEY FAGGOTS!");
    }
}
