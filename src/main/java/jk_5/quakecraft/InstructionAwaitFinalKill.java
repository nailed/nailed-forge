package jk_5.quakecraft;

import jk_5.nailed.map.instruction.GameController;
import jk_5.nailed.map.instruction.IInstruction;
import jk_5.nailed.map.instruction.TimedInstruction;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionAwaitFinalKill extends TimedInstruction {

    public boolean finalKillMade = false;

    @Override
    public void injectArguments(String args){

    }

    @Override
    public boolean executeTimed(GameController controller, int ticks){
        return this.finalKillMade;
    }

    @Override
    public IInstruction cloneInstruction(){
        return new InstructionAwaitFinalKill();
    }
}
