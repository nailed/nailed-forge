package jk_5.nailed.map.instruction;

import com.google.common.collect.Lists;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionReader {

    public static List<IInstruction> readInstructions(BufferedReader reader) throws IOException {
        List<IInstruction> ret = Lists.newArrayList();
        while(reader.ready()){
            String line = reader.readLine();
        }
        return ret;
    }
}
