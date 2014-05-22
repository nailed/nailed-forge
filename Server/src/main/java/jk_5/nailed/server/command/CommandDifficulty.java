package jk_5.nailed.server.command;

import java.util.*;

import net.minecraft.command.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import jk_5.nailed.api.map.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandDifficulty extends NailedCommand {

    public CommandDifficulty() {
        super("difficulty");
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender) {
        return "commands.difficulty.usage";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args) {
        if(args.length == 1){
            EnumDifficulty newDifficulty;
            String s = args[0];
            if("peaceful".equalsIgnoreCase(s) || "p".equalsIgnoreCase(s) || "0".equals(s)){
                newDifficulty = EnumDifficulty.PEACEFUL;
            }else if("easy".equalsIgnoreCase(s) || "e".equalsIgnoreCase(s) || "1".equals(s)){
                newDifficulty = EnumDifficulty.EASY;
            }else if("normal".equalsIgnoreCase(s) || "n".equalsIgnoreCase(s) || "2".equals(s)){
                newDifficulty = EnumDifficulty.NORMAL;
            }else if("hard".equalsIgnoreCase(s) || "h".equalsIgnoreCase(s) || "3".equals(s)){
                newDifficulty = EnumDifficulty.HARD;
            }else{
                throw new WrongUsageException("commands.difficulty.usage");
            }
            map.getWorld().difficultySetting = newDifficulty;
            map.getWorld().setAllowedSpawnTypes(newDifficulty != EnumDifficulty.PEACEFUL, true);
            sender.addChatMessage(new ChatComponentText("Successfully changed difficulty"));
        }else{
            throw new WrongUsageException("commands.difficulty.usage");
        }
    }

    @Override
    public List<String> addAutocomplete(ICommandSender var1, String[] args) {
        if(args.length == 1){
            return getOptions(args, "peaceful", "easy", "normal", "hard");
        }else{
            return null;
        }
    }
}
