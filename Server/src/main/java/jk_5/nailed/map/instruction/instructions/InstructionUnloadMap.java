package jk_5.nailed.map.instruction.instructions;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionUnloadMap implements IInstruction {

    @Override
    public void injectArguments(String args){

    }

    @Override
    public void execute(GameController controller){
        controller.getMap().unloadAndRemove();
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionUnloadMap();
    }
}
