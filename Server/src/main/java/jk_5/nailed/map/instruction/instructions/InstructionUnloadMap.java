package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.api.map.GameController;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.api.map.TimedInstruction;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionUnloadMap extends TimedInstruction {

    @Override
    public void injectArguments(String args){

    }

    @Override
    public boolean executeTimed(GameController controller, int ticks){
        if(ticks >= 1){
            controller.getMap().unloadAndRemove();
            return false;
        }else{
            return true;
        }
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionUnloadMap();
    }
}
