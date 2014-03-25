package jk_5.nailed.server.command;

import jk_5.nailed.achievement.NailedAchievements;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.player.Player;
import jk_5.nailed.network.NailedNetworkHandler;
import jk_5.nailed.network.NailedPacket;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandRegisterAchievement extends NailedCommand {

    public CommandRegisterAchievement(){
        super("ra");
    }

    @Override
    public void processCommandPlayer(Player sender, Map map, String[] args){
        NailedAchievements.register(args[0].equalsIgnoreCase("true"));
        NailedNetworkHandler.sendPacketToPlayer(new NailedPacket.RegisterAchievement(args[0].equalsIgnoreCase("true")), sender.getEntity());
    }
}
