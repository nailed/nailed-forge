package jk_5.nailed.server.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.DimensionManager;

import java.text.DecimalFormat;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandTps extends NailedCommand {

    private static final DecimalFormat timeFormatter = new DecimalFormat("########0.000");

    @Override
    public String getCommandName(){
        return "tps";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender){
        return true;
    }

    @Override
    public void process(ICommandSender sender, String[] args){
        MinecraftServer server = MinecraftServer.getServer();
        int dim = 0;
        boolean summary = true;
        if (args.length > 1){
            dim = parseInt(sender, args[1]);
            summary = false;
        }
        if (summary){
            double meanTickTime = mean(server.tickTimeArray) * 1.0E-6D;
            double meanTPS = Math.min(1000.0/meanTickTime, 20);
            sender.func_145747_a(this.getComponent("Overall", meanTickTime, meanTPS));
            for (Integer dimId : DimensionManager.getIDs()){
                double worldTickTime = mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
                double worldTPS = Math.min(1000.0/worldTickTime, 20);
                sender.func_145747_a(this.getComponent("Dim " + dimId, worldTickTime, worldTPS));
            }
        }else{
            double worldTickTime = mean(server.worldTickTimes.get(dim)) * 1.0E-6D;
            double worldTPS = Math.min(1000.0/worldTickTime, 20);
            sender.func_145747_a(this.getComponent("Dim " + dim, worldTickTime, worldTPS));
        }
    }

    private IChatComponent getComponent(String prefix, double tickTime, double tps){
        ChatComponentText ret = new ChatComponentText(prefix + ": ");
        ChatComponentText com1 = new ChatComponentText("TPS: " + timeFormatter.format(tps));
        if(tps != 20) com1.func_150256_b().func_150238_a(EnumChatFormatting.RED);
        ret.func_150257_a(com1);
        com1 = new ChatComponentText(" Tick Time: " + timeFormatter.format(tickTime) + "ms");
        if(tickTime > 45) com1.func_150256_b().func_150238_a(EnumChatFormatting.RED);
        else if(tickTime > 35) com1.func_150256_b().func_150238_a(EnumChatFormatting.GOLD);
        ret.func_150257_a(com1);
        double percent = (tps / 20) * 100;
        com1 = new ChatComponentText(" (" + timeFormatter.format(percent) + "%)");
        ret.func_150257_a(com1);
        return ret;
    }

    private static long mean(long[] values){
        long sum = 0l;
        for (long v : values){
            sum += v;
        }
        return sum / values.length;
    }
}
