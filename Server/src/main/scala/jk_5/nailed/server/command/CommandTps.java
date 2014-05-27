package jk_5.nailed.server.command;

import java.text.*;

import net.minecraft.command.*;
import net.minecraft.server.*;
import net.minecraft.util.*;

import net.minecraftforge.common.*;

import jk_5.nailed.api.map.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTps extends NailedCommand {

    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

    public CommandTps() {
        super("tps");
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        MinecraftServer server = MinecraftServer.getServer();
        int dim = 0;
        boolean summary = true;
        if(args.length > 1){
            dim = CommandBase.parseInt(sender, args[1]);
            summary = false;
        }
        if(summary){
            double meanTickTime = mean(server.tickTimeArray) * 1.0E-6D;
            double meanTPS = Math.min(1000.0 / meanTickTime, 20);
            sender.addChatMessage(this.getComponent("Overall", meanTickTime, meanTPS));
            for(Integer dimId : DimensionManager.getIDs()){
                double worldTickTime = mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
                double worldTPS = Math.min(1000.0 / worldTickTime, 20);
                sender.addChatMessage(this.getComponent("Dim " + dimId, worldTickTime, worldTPS));
            }
        }else{
            double worldTickTime = mean(server.worldTickTimes.get(dim)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0 / worldTickTime, 20);
            sender.addChatMessage(this.getComponent("Dim " + dim, worldTickTime, worldTPS));
        }
    }

    private IChatComponent getComponent(String prefix, double tickTime, double tps) {
        ChatComponentText ret = new ChatComponentText(prefix + ": ");
        ChatComponentText com1 = new ChatComponentText("TPS: " + timeFormatter.format(tps));
        if(tps != 20){
            com1.getChatStyle().setColor(EnumChatFormatting.RED);
        }
        ret.appendSibling(com1);
        com1 = new ChatComponentText(" Tick Time: " + timeFormatter.format(tickTime) + "ms");
        if(tickTime > 45){
            com1.getChatStyle().setColor(EnumChatFormatting.RED);
        }else if(tickTime > 35){
            com1.getChatStyle().setColor(EnumChatFormatting.GOLD);
        }
        ret.appendSibling(com1);
        double percent = (tps / 20) * 100;
        com1 = new ChatComponentText(" (" + timeFormatter.format(percent) + "%)");
        ret.appendSibling(com1);
        return ret;
    }

    private static long mean(long[] values) {
        long sum = 0L;
        for(long v : values){
            sum += v;
        }
        return sum / values.length;
    }
}
