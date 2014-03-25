package jk_5.nailed.client.achievement;

import com.google.common.collect.Maps;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.AchievementPage;

import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class NailedAchievements {

    private static Map<String, Achievement> achievements = Maps.newHashMap();
    private static AchievementPage page;

    private static Achievement hotloaded;

    public static void addAchievements(){
        registerAchievement("nailed.firstJoin", 0, 0, new ItemStack(Items.golden_apple, 1), null);
    }

    private static Achievement registerAchievement(String name, int x, int y, ItemStack icon, Achievement dependency){
        Achievement a = new Achievement(name, name, x, y, icon, dependency);
        achievements.put(name, a);
        return a;
    }

    public static void init(){
        Achievement[] a = achievements.values().toArray(new Achievement[achievements.size()]);
        page = new AchievementPage(StatCollector.translateToLocal("nailed.achievementPage.name"), a);
        AchievementPage.registerAchievementPage(page);

        hotloaded = new Achievement("nailed.testAchievement", "nailed.testAchievement", 5, 0, new ItemStack(Items.bowl, 1), null);
    }

    public static void register(boolean enable){
        if(enable){
            page.getAchievements().add(hotloaded);
        }else{
            page.getAchievements().remove(hotloaded);
        }
    }
}
