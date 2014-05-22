package jk_5.nailed.map.script.api;

import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.script.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class TermApi implements ILuaAPI {

    private Terminal terminal;

    public TermApi(IAPIEnvironment env) {
        this.terminal = env.getTerminal();
    }

    @Override
    public String[] getNames() {
        return new String[]{"term"};
    }

    @Override
    public void startup() {

    }

    @Override
    public void advance(double dt) {

    }

    @Override
    public void shutdown() {

    }

    public static int parseColor(Object[] args) throws Exception {
        if(args.length != 1 || args[0] == null || !(args[0] instanceof Double)){
            throw new Exception("Expected number");
        }
        int colour = (int) ((Double) args[0]).doubleValue();
        if(colour <= 0){
            throw new Exception("Color out of range");
        }
        colour = 16 - getHighestBit(colour);
        if(colour < 0 || colour > 15){
            throw new Exception("Color out of range");
        }
        return colour;
    }

    @Override
    public String[] getMethodNames() {
        return new String[]{
                "write",
                "scroll",
                "setCursorPos",
                "setCursorBlink",
                "getCursorPos",
                "getSize",
                "clear",
                "clearLine",
                "setTextColor",
                "setBackgroundColor"
        };
    }

    @Override
    public Object[] callMethod(ILuaContext context, int method, Object[] args) throws Exception {
        switch(method){
            case 0: //write
                String text;
                if(args.length > 0 && args[0] != null){
                    text = args[0].toString();
                }else{
                    text = "";
                }

                synchronized(this.terminal){
                    this.terminal.write(text);
                    this.terminal.setCursorPos(this.terminal.getCursorX() + text.length(), this.terminal.getCursorY());
                }
                return null;
            case 1: //scroll
                if(args.length != 1 || args[0] == null || !(args[0] instanceof Double)){
                    throw new Exception("Expected number");
                }

                int y = (int) ((Double) args[0]).doubleValue();
                synchronized(this.terminal){
                    this.terminal.scroll(y);
                }
                return null;
            case 2: //setCursorPos
                if(args.length != 2 || args[0] == null || !(args[0] instanceof Double) || args[1] == null || !(args[1] instanceof Double)){
                    throw new Exception("Expected number, number");
                }

                int x = (int) ((Double) args[0]).doubleValue() - 1;
                int y1 = (int) ((Double) args[1]).doubleValue() - 1;
                synchronized(this.terminal){
                    this.terminal.setCursorPos(x, y1);
                }
                return null;
            case 3: //setCursorBlink
                if(args.length != 1 || args[0] == null || !(args[0] instanceof Boolean)){
                    throw new Exception("Expected boolean");
                }
                boolean b = (Boolean) args[0];
                synchronized(this.terminal){
                    this.terminal.setCursorBlink(b);
                }
                return null;
            case 4: //getCursorPos
                synchronized(this.terminal){
                    x = this.terminal.getCursorX();
                    y = this.terminal.getCursorY();
                }
                return new Object[]{x + 1, y + 1};
            case 5: //getSize
                int width;
                int height;
                synchronized(this.terminal){
                    width = this.terminal.getWidth();
                    height = this.terminal.getHeight();
                }
                return new Object[]{width, height};
            case 6: //clear
                synchronized(this.terminal){
                    this.terminal.clear();
                }
                return null;
            case 7: //clearLine
                synchronized(this.terminal){
                    this.terminal.clearLine();
                }
                return null;
            case 8: //setTextColor
                int color = parseColor(args);
                synchronized(this.terminal){
                    this.terminal.setTextColor(color);
                }
                return null;
            case 9: //setBackgroundColor
                int color1 = parseColor(args);
                synchronized(this.terminal){
                    this.terminal.setBackgroundColor(color1);
                }
                return null;
        }
        return null;
    }

    private static int getHighestBit(int group) {
        int bit = 0;
        while(group > 0){
            group >>= 1;
            bit++;
        }
        return bit;
    }
}
