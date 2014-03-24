package jk_5.nailed.irc;

import com.google.common.collect.Lists;
import io.netty.channel.nio.NioEventLoopGroup;
import jk_5.nailed.util.config.ConfigTag;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class IrcConnector {

    private final List<IrcBot> bots = Lists.newArrayList();
    private NioEventLoopGroup loop = new NioEventLoopGroup();

    public IrcConnector(){

    }

    public void readConfig(ConfigTag tag){
        ConfigTag bots = tag.getTag("bots").useBraces();
        for(ConfigTag t : bots.getSortedTagList()){
            IrcBot bot = new IrcBot(t.useBraces());
        }
    }

    public void connect(){
        for(IrcBot bot : this.bots){
            bot.connect(this.loop);
        }
    }

    public void close(){
        //TODO: close all bots
        this.loop.shutdownGracefully();
    }
}
