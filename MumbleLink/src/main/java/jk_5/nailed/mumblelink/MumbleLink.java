package jk_5.nailed.mumblelink;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

/**
 * No description given
 *
 * @author jk-5
 */
@Mod(modid = "NailedMumble", name = "NailedMumble", version = "0.1", dependencies = "required-after:Nailed")
public class MumbleLink {

    private static final long notificationDelay = 100;
    private boolean messagePrinted = false;
    private long start = -1;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event){
        Mumble.tryInit();

        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onRender(TickEvent.RenderTickEvent event){
        Minecraft mc = Minecraft.getMinecraft();
        if(event.phase == TickEvent.Phase.START){
            if(!Mumble.isInited() && !Mumble.tryInit()){
                return;
            }
            this.update(mc);
            if(Mumble.isInited()){
                if(mc != null && mc.theWorld != null){
                    if(!this.messagePrinted) {
                        long now = mc.theWorld.getWorldTime();
                        if(this.start == -1) {
                            this.start = now;
                        }
                        if(start + notificationDelay < now){
                            this.messagePrinted = true;
                            mc.thePlayer.addChatMessage(new ChatComponentText("Linked to mumble"));
                        }
                    }
                }else{
                    this.messagePrinted = false;
                }
            }
        }
    }

    public void update(Minecraft mc){
        try{
            float fAvatarFrontX = 1;
            float fAvatarFrontY = 0;
            float fAvatarFrontZ = 1;

            float fCameraFrontX = 1;
            float fCameraFrontY = 0;
            float fCameraFrontZ = 1;

            float fAvatarTopX = 0;
            float fAvatarTopY = 1;
            float fAvatarTopZ = 0;

            float fCameraTopX = 0;
            float fCameraTopY = 1;
            float fCameraTopZ = 0;

            Vec3 camera = mc.thePlayer.getLookVec();

            //Position of the player
            float[] fAvatarPosition = {
                    Float.parseFloat(Double.toString(mc.thePlayer.posX)), // TODO: losing precision here
                    Float.parseFloat(Double.toString(mc.thePlayer.posZ)), // TODO: losing precision here
                    Float.parseFloat(Double.toString(mc.thePlayer.posY))}; // TODO: losing precision here

            //Unit vector pointing out of the players eyes (here Front looks into scene).
            float[] fAvatarFront = {
                    Float.parseFloat(Double.toString(camera.xCoord * fAvatarFrontX)), // TODO: losing precision here
                    Float.parseFloat(Double.toString(camera.zCoord * fAvatarFrontZ)), // TODO: losing precision here
                    Float.parseFloat(Double.toString(camera.yCoord * fAvatarFrontY))}; // TODO: losing precision here

            //Unit vector pointing out of the top of the avatars head (here Top looks straight up).
            float[] fAvatarTop = {fAvatarTopX, fAvatarTopZ, fAvatarTopY};

            float[] fCameraPosition = {
                    Float.parseFloat(Double.toString(mc.thePlayer.posX)), //TODO: losing precision here
                    Float.parseFloat(Double.toString(mc.thePlayer.posZ)), //TODO: losing precision here
                    Float.parseFloat(Double.toString(mc.thePlayer.posY))}; //TODO: losing precision here

            float[] fCameraFront = {
                    Float.parseFloat(Double.toString(camera.xCoord * fCameraFrontX)), //TODO: losing precision here
                    Float.parseFloat(Double.toString(camera.zCoord * fCameraFrontZ)), //TODO: losing precision here
                    Float.parseFloat(Double.toString(camera.yCoord * fCameraFrontY))}; //TODO: losing precision here

            float[] fCameraTop = {fCameraTopX, fCameraTopZ, fCameraTopY};

            String identity = mc.thePlayer.getGameProfile().getName();
            String context = "NailedGlobal";
            String name = "Nailed";
            String description = "Link plugin for Minecraft with ModLoader";

            Mumble.update(fAvatarPosition, fAvatarFront, fAvatarTop, name, description, fCameraPosition, fCameraFront, fCameraTop, identity, context);
        }catch(Exception ignored){

        }
    }
}
