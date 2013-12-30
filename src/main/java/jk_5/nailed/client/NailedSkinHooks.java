package jk_5.nailed.client;

import com.google.common.collect.Maps;
import net.minecraft.util.StringUtils;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedSkinHooks {

    private static Map<String, String> skinUrlMap = Maps.newHashMap();
    private static Map<String, String> capeUrlMap = Maps.newHashMap();

    static {
        capeUrlMap.put("jk-5", "http://minecraftcapes.com/userskins/Enderman_cape_by_Bigfoot.png_by_Skeminem.png");
    }

    @SuppressWarnings("unused")
    public static String getSkinUrl(String username){
        if(username == null || !skinUrlMap.containsKey(username)){
            return String.format("http://skins.minecraft.net/MinecraftSkins/%s.png", StringUtils.stripControlCodes(username));
        }else{
            return skinUrlMap.get(username);
        }
    }

    @SuppressWarnings("unused")
    public static String getCapeUrl(String username){
        if(username == null || !capeUrlMap.containsKey(username)){
            return String.format("http://skins.minecraft.net/MinecraftCloaks/%s.png", StringUtils.stripControlCodes(username));
        }else{
            return capeUrlMap.get(username);
        }
    }
}
