package jk_5.nailed.server.command;

import jk_5.nailed.api.map.Map;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.EnumDifficulty;

/**
 * No description given
 *
 * @author jk-5
 */
public class CommandDifficulty extends NailedCommand {

    public CommandDifficulty(){
        super("difficulty");
    }

    @Override
    public String getCommandUsage(ICommandSender icommandsender){
        return "commands.difficulty.usage";
    }

    @Override
    public void processCommandWithMap(ICommandSender sender, Map map, String[] args){
        if(args.length == 1){
            EnumDifficulty newDifficulty;
            String s = args[0];
            if(s.equalsIgnoreCase("peaceful") || s.equalsIgnoreCase("p") || s.equals("0")){
                newDifficulty = EnumDifficulty.PEACEFUL;
            }else if(s.equalsIgnoreCase("easy") || s.equalsIgnoreCase("e") || s.equals("1")){
                newDifficulty = EnumDifficulty.EASY;
            }else if(s.equalsIgnoreCase("normal") || s.equalsIgnoreCase("n") || s.equals("2")){
                newDifficulty = EnumDifficulty.NORMAL;
            }else if(s.equalsIgnoreCase("hard") || s.equalsIgnoreCase("h") || s.equals("3")){
                newDifficulty = EnumDifficulty.HARD;
            }else throw new WrongUsageException("commands.difficulty.usage");
            map.getWorld().difficultySetting = newDifficulty;
            map.getWorld().setAllowedSpawnTypes(newDifficulty != EnumDifficulty.PEACEFUL, true);
            sender.addChatMessage(new ChatComponentText("Successfully changed difficulty"));
        }else throw new WrongUsageException("commands.difficulty.usage");
    }
}
