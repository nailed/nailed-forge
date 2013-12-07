package jk_5.nailed.map.instruction;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import jk_5.nailed.map.MappackInitializationException;
import jk_5.nailed.map.instruction.instructions.*;
import lombok.Getter;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionReader {

    private static InstructionReader INSTANCE = new InstructionReader();

    @Getter private final Map<String, Class<?>> instructionMap = Maps.newHashMap();

    public static InstructionReader instance(){
        return INSTANCE;
    }

    @ForgeSubscribe
    public void registerInstructionsFromEvent(RegisterInstructionEvent event){
        event.register("clearinventory", InstructionClearInventory.class);
        event.register("countup", InstructionCountUp.class);
        event.register("countdown", InstructionCountdown.class);
        event.register("disable", InstructionDisableStat.class);
        event.register("enable", InstructionEnableStat.class);
        event.register("clearexperience", InstructionResetExperience.class);
        event.register("resetspawnpoint", InstructionResetSpawnpoint.class);
        event.register("setdifficulty", InstructionSetDifficulty.class);
        event.register("setfoodlevel", InstructionSetFoodlevel.class);
        event.register("setgamemode", InstructionSetGamemode.class);
        event.register("sethealth", InstructionSetHealth.class);
        event.register("setspawn", InstructionSetSpawnpoint.class);
        event.register("settime", InstructionSetTime.class);
        event.register("setwinner", InstructionSetWinner.class);
        event.register("startwinnerinterrupt", InstructionStartWinnerinterrupt.class);
        event.register("stopwinnerinterrupt", InstructionStopWinnerinterrupt.class);
        event.register("trigger", InstructionTriggerStat.class);
        event.register("unwatchunready", InstructionUnwatchUnready.class);
        event.register("watchunready", InstructionWatchUnready.class);
    }

    private InstructionReader(){
        MinecraftForge.EVENT_BUS.register(this);
    }

    public InstructionList readInstructions(BufferedReader reader) throws MappackInitializationException, InstructionParseException {
        InstructionList ret = new InstructionList();
        int lineNumber = 1;
        try{
            while(reader.ready()){
                String line = reader.readLine();
                if(!Strings.isNullOrEmpty(line) && !line.startsWith("#")){
                    String[] data = line.split(" ", 2);
                    if(data.length > 0){
                        if(this.instructionMap.containsKey(data[0].trim())){
                            IInstruction instr = (IInstruction) this.instructionMap.get(data[0].trim()).newInstance();
                            if(instr != null && data.length == 2) instr.injectArguments(data[1]);
                            ret.add(instr);
                        }else{
                            throw new InstructionParseException(lineNumber, "Unknown instruction");
                        }
                    }
                }
                lineNumber ++;
            }
        }catch(IOException e){
            throw new MappackInitializationException("IOException while reading instructions", e);
        }catch(InstructionParseException e){
            throw e;
        }catch(Exception e){
            throw new MappackInitializationException("Exception while reading instructions", e);
        }
        return ret;
    }
}
