package jk_5.nailed.client.skinsync;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import lombok.Getter;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;

import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * No description given
 *
 * @author jk-5
 */
public class SkinSync {

    @Getter private static final SkinSync instance = new SkinSync();

    private final Executor worker = Executors.newCachedThreadPool();
    private final Map<String, String> playerSkinNameMap = Maps.newHashMap();
    private final Map<String, String> playerCapeNameMap = Maps.newHashMap();
    private final Map<String, BufferedImage> skinCache = Maps.newHashMap();
    private final Map<String, BufferedImage> capeCache = Maps.newHashMap();
    private final List<String> reloadingPlayers = Lists.newArrayList();

    @SubscribeEvent
    public void onPreRenderSpecials(RenderPlayerEvent.Specials.Pre event){
        String username = event.entityPlayer.func_146103_bH().getName();
        if(event.entityPlayer instanceof AbstractClientPlayer && this.reloadingPlayers.contains(username)){
            this.reloadingPlayers.remove(username);
            AbstractClientPlayer clientPlayer = (AbstractClientPlayer) event.entityPlayer;
            BufferedImage skin = this.skinCache.get(this.playerSkinNameMap.get(username));
            BufferedImage cape = this.capeCache.get(this.playerCapeNameMap.get(username));
            if(clientPlayer.getTextureSkin().bufferedImage != skin){
                clientPlayer.getTextureSkin().textureUploaded = false;
                clientPlayer.getTextureSkin().bufferedImage = skin;
            }
            if(clientPlayer.getTextureCape().bufferedImage != cape){
                clientPlayer.getTextureCape().textureUploaded = false;
                clientPlayer.getTextureCape().bufferedImage = cape;
            }
        }
        event.renderCape = true;
    }

    public void setPlayerSkinName(String username, String skinName){
        this.playerSkinNameMap.put(username, skinName);
        this.reloadingPlayers.add(username);
    }

    public void setPlayerCloakName(String username, String cloakName){
        this.playerCapeNameMap.put(username, cloakName);
        this.reloadingPlayers.add(username);
    }

    public void cacheSkinData(String skinName, BufferedImage image){
        this.skinCache.put(skinName, image);
    }

    public void cacheCapeData(String capeName, BufferedImage image){
        this.capeCache.put(capeName, image);
    }
}
