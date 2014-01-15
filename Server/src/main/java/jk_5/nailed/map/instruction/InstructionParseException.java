package jk_5.nailed.map.instruction;

/**
 * No description given
 *
 * @author jk-5
 */
public class InstructionParseException extends Exception {

    public InstructionParseException(int linenumber){
        super("Line " + linenumber);
    }

    public InstructionParseException(int linenumber, String message){
        super(message + " at line " + linenumber);
    }
}
