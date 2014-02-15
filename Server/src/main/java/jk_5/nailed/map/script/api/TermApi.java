package jk_5.nailed.map.script.api;

import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.ILuaContext;
import jk_5.nailed.map.script.IAPIEnvironment;
import jk_5.nailed.map.script.ScriptingMachine;
import jk_5.nailed.map.script.Terminal;

/**
 * No description given
 *
 * @author jk-5
 */
public class TermApi implements ILuaAPI {

    private Terminal m_terminal;
    private ScriptingMachine m_environment;
    private boolean m_enableColour;

    public TermApi(IAPIEnvironment _environment){
        this.m_terminal = _environment.getTerminal();
        this.m_environment = _environment.getMachine();
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

                synchronized(this.m_terminal){
                    this.m_terminal.write(text);
                    this.m_terminal.setCursorPos(this.m_terminal.getCursorX() + text.length(), this.m_terminal.getCursorY());
                }
                return null;
            case 1:
                if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Double))){
                    throw new Exception("Expected number");
                }

                int y = (int) ((Double) args[0]).doubleValue();
                synchronized(this.m_terminal){
                    this.m_terminal.scroll(y);
                }
                return null;
            case 2:
                if((args.length != 2) || (args[0] == null) || (!(args[0] instanceof Double)) || (args[1] == null) || (!(args[1] instanceof Double))){
                    throw new Exception("Expected number, number");
                }

                int x = (int) ((Double) args[0]).doubleValue() - 1;
                int y1 = (int) ((Double) args[1]).doubleValue() - 1;
                synchronized(this.m_terminal){
                    this.m_terminal.setCursorPos(x, y1);
                }
                return null;
            case 3:
                if((args.length != 1) || (args[0] == null) || (!(args[0] instanceof Boolean))){
                    throw new Exception("Expected boolean");
                }
                boolean b = ((Boolean) args[0]).booleanValue();
                synchronized(this.m_terminal){
                    this.m_terminal.setCursorBlink(b);
                }
                return null;
            case 4:
                synchronized(this.m_terminal){
                    x = this.m_terminal.getCursorX();
                    y = this.m_terminal.getCursorY();
                }
                return new Object[]{Integer.valueOf(x + 1), Integer.valueOf(y + 1)};
            case 5:
                int width;
                int height;
                synchronized(this.m_terminal){
                    width = this.m_terminal.getWidth();
                    height = this.m_terminal.getHeight();
                }
                return new Object[]{Integer.valueOf(width), Integer.valueOf(height)};
            case 6:
                synchronized(this.m_terminal){
                    this.m_terminal.clear();
                }
                return null;
            case 7:
                synchronized(this.m_terminal){
                    this.m_terminal.clearLine();
                }
                return null;
            case 8:
            case 9:
                int color = parseColour(args);
                synchronized(this.m_terminal){
                    this.m_terminal.setTextColor(color);
                }
                return null;
            case 10:
            case 11:
                int color1 = parseColour(args);
                synchronized(this.m_terminal){
                    this.m_terminal.setBackgroundColor(color1);
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
