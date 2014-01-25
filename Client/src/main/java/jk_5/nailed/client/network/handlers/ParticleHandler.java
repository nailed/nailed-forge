package jk_5.nailed.client.network.handlers;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jk_5.nailed.client.network.NailedPacket;
import jk_5.nailed.client.particle.ParticleHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * No description given
 *
 * @author jk-5
 */
public class ParticleHandler extends SimpleChannelInboundHandler<NailedPacket.Particle> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NailedPacket.Particle msg) throws Exception{
        World world = Minecraft.getMinecraft().theWorld;
        for(int particles = 0; particles < 50; particles++){
            float f = world.rand.nextFloat() - world.rand.nextFloat();
            float f1 = world.rand.nextFloat() * 2.0F;
            float f2 = world.rand.nextFloat() - world.rand.nextFloat();
            ParticleHelper.spawnParticle(msg.name, msg.x + f, msg.y + f1, msg.z + f2, 0, 0, 0);
        }
    }
}
