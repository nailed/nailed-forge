package jk_5.nailed.map.instruction;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;
import jk_5.nailed.api.map.IInstruction;
import jk_5.nailed.map.MappackInitializationException;

import java.io.BufferedReader;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionList extends ForwardingList<IInstruction> implements jk_5.nailed.api.map.InstructionList {

    private final List<IInstruction> instructions = Lists.newArrayList();

    public static InstructionList readFrom(BufferedReader reader) throws InstructionParseException, MappackInitializationException{
        return InstructionReader.instance().readInstructions(reader);
    }

    public InstructionList cloneList(){
        InstructionList ret = new InstructionList();
        for(IInstruction element : this){
            ret.add(element.cloneInstruction());
        }
        return ret;
    }

    @Override
    public List<IInstruction> delegate(){
        return this.instructions;
    }
}
