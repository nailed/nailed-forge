package jk_5.nailed.map.script.api;

import jk_5.nailed.map.script.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class TermApi implements ILuaAPI {

    private Terminal terminal;
    private ScriptingMachine machine;

    public TermApi(IAPIEnvironment _environment){
        this.terminal = _environment.getTerminal();
        this.machine = _environment.getMachine();
    }

    public String[] getNames(){
        return new String[]{"term"};
    }

    public void startup(){
    }

    public void advance(double _dt){
    }

    public void shutdown(){
    }

    public String[] getMethodNames(){
        return new String[]{"write", "scroll", "setCursorPos", "setCursorBlink", "getCursorPos", "getSize", "clear", "clearLine", "setTextColour", "setTextColor", "setBackgroundColour", "setBackgroundColor"};
    }

    public static int parseColour(Object[] args) throws Exception{
        if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Double))){
            throw new Exception("Expected number");
        }
        int colour = (int) ((Double) args[0]).doubleValue();
        if(colour <= 0){
            throw new Exception("Colour out of range");
        }
        colour = 16 - getHighestBit(colour);
        if((colour < 0) || (colour > 15)){
            throw new Exception("Colour out of range");
        }
        return colour;
    }

    public Object[] callMethod(ILuaContext context, int method, Object[] args)
            throws Exception{
        switch(method){
            case 0:
                String text;
                if((args.length > 0) && (args[0] != null))
                    text = args[0].toString();
                else{
                    text = "";
                }

                synchronized(this.terminal){
                    this.terminal.write(text);
                    this.terminal.setCursorPos(this.terminal.getCursorX() + text.length(), this.terminal.getCursorY());
                }
                return null;
            case 1:
                if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Double))){
                    throw new Exception("Expected number");
                }

                int y = (int) ((Double) args[0]).doubleValue();
                synchronized(this.terminal){
                    this.terminal.scroll(y);
                }
                return null;
            case 2:
                if((args.length != 2) || (args[0] == null) || (!(args[0] instanceof Double)) || (args[1] == null) || (!(args[1] instanceof Double))){
                    throw new Exception("Expected number, number");
                }

                int x = (int) ((Double) args[0]).doubleValue() - 1;
                int y1 = (int) ((Double) args[1]).doubleValue() - 1;
                synchronized(this.terminal){
                    this.terminal.setCursorPos(x, y1);
                }
                return null;
            case 3:
                if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Boolean))){
                    throw new Exception("Expected boolean");
                }
                boolean b = ((Boolean) args[0]).booleanValue();
                synchronized(this.terminal){
                    this.terminal.setCursorBlink(b);
                }
                return null;
            case 4:
                synchronized(this.terminal){
                    x = this.terminal.getCursorX();
                    y = this.terminal.getCursorY();
                }
                return new Object[]{Integer.valueOf(x + 1), Integer.valueOf(y + 1)};
            case 5:
                int width;
                int height;
                synchronized(this.terminal){
                    width = this.terminal.getWidth();
                    height = this.terminal.getHeight();
                }
                return new Object[]{Integer.valueOf(width), Integer.valueOf(height)};
            case 6:
                synchronized(this.terminal){
                    this.terminal.clear();
                }
                return null;
            case 7:
                synchronized(this.terminal){
                    this.terminal.clearLine();
                }
                return null;
            case 8:
            case 9:
                int color = parseColour(args);
                synchronized(this.terminal){
                    this.terminal.setTextColor(color);
                }
                return null;
            case 10:
            case 11:
                int color1 = parseColour(args);
                synchronized(this.terminal){
                    this.terminal.setBackgroundColor(color1);
                }
                return null;
        }
        return null;
    }

    private static int getHighestBit(int group){
        int bit = 0;
        while(group > 0){
            group >>= 1;
            bit++;
        }
        return bit;
    }
}
