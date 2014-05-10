package jk_5.quakecraft;

import com.google.common.collect.Lists;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.map.scoreboard.DisplayType;
import jk_5.nailed.api.map.scoreboard.Objective;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.util.ChatColor;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class QuakecraftLuaApi implements ILuaAPI {

    public static final List<String> doneMaps = Lists.newArrayList();
    private final Map map;

    public QuakecraftLuaApi(Map map) {
        this.map = map;
    }

    @Override
    public String[] getNames() {
        return new String[]{
                "quakecraft"
        };
    }

    @Override
    public void startup() {

    }

    @Override
    public void advance(double paramDouble) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "isDone",
                "giveItems"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] arguments) throws Exception {
        switch(method){
            case 0: //isDone
                return new Object[]{doneMaps.contains(map.getSaveFileName())};
            case 1: //giveItems
                Objective obj = map.getScoreboardManager().getOrCreateObjective("kills");
                map.getScoreboardManager().setDisplay(DisplayType.SIDEBAR, obj);
                for(Player player : map.getPlayers()){
                    ItemStack stack = new ItemStack(Items.wooden_hoe, 1);
                    stack.setStackDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + "Railgun");
                    player.getEntity().inventory.setInventorySlotContents(0, stack);
                    player.getEntity().addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 1000000, 1, true));
                    obj.getScore(player.getUsername()).setValue(0);
                }
                break;
        }
        return new Object[0];
    }
}
