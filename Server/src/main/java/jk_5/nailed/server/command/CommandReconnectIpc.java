package jk_5.nailed.server.command;

import io.netty.channel.ChannelFuture;
import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.concurrent.scheduler.NailedRunnable;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.ipc.IpcManager;
import net.minecraft.command.ICommandSender;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandReconnectIpc extends NailedCommand {

    public CommandReconnectIpc(){
        super("reconnectipc");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        NailedAPI.getScheduler().runTaskAsynchronously(new NailedRunnable() {
            @Override
            public void run(){
                ChannelFuture future = IpcManager.instance().close();
                if(future != null){
                    future.syncUninterruptibly();
                }
                IpcManager.instance().start();
            }
        });
    }
}
