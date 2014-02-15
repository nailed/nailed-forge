package jk_5.nailed.map.script;

import lombok.Getter;

import java.util.Arrays;

/**
 * No description given
 *
 * @author jk-5
 */
public class Terminal {

    public static final int WIDTH = 51;
    public static final int HEIGHT = 19;
    public static final String base16 = "0123456789abcdef";

    @Getter private int cursorX = 0;
    @Getter private int cursorY = 0;
    @Getter private boolean cursorBlink = false;
    @Getter private int textColor = 15;
    @Getter private int backgroundColor = 0;
    @Getter private int width;
    @Getter private int height;
    private String emptyLine;
    private String emptyColorLine;
    private String[] lines;
    private String[] colorLines;
    @Getter private boolean changed = false;
    @Getter private boolean[] lineChanged;
    @Getter private boolean[] colorLineChanged;

    public Terminal(int width, int height){
        this.width = width;
        this.height = height;

        rebuildEmptyLine();
        rebuildEmptyColorLine();

        this.lines = new String[this.height];
        this.colorLines = new String[this.height];
        this.lineChanged = new boolean[this.height];
        this.colorLineChanged = new boolean[this.height];

        for(int i = 0; i < this.height; i++){
            this.lines[i] = this.emptyLine;
            this.colorLines[i] = this.emptyColorLine;
        }
    }

    public void resize(int width, int height){
        if(width == this.width && height == this.height){
            return;
        }

        int oldHeight = this.height;
        int oldWidth = this.width;
        String[] oldLines = this.lines;
        String[] oldColorLines = this.colorLines;

        this.width = width;
        this.height = height;

        rebuildEmptyLine();
        rebuildEmptyColorLine();

        this.lines = new String[this.height];
        this.colorLines = new String[this.height];
        this.lineChanged = new boolean[this.height];
        this.colorLineChanged = new boolean[this.height];

        for(int i = 0; i < this.height; i++){
            if(i < oldHeight){
                String oldLine = oldLines[i];
                String oldTextColorLine = oldColorLines[i].substring(0, oldWidth);
                String oldBgColorLine = oldColorLines[i].substring(oldWidth, oldWidth * 2);
                String textColorLine;
                String bgColorLine;
                if(oldLine.length() >= this.width){
                    this.lines[i] = oldLine.substring(0, this.width);
                    textColorLine = oldTextColorLine.substring(0, this.width);
                    bgColorLine = oldBgColorLine.substring(0, this.width);
                }else{
                    this.lines[i] = oldLine + this.emptyLine.substring(oldLine.length(), this.width);
                    textColorLine = oldTextColorLine + this.emptyColorLine.substring(oldLine.length(), this.width);
                    bgColorLine = oldBgColorLine + this.emptyColorLine.substring(this.width + oldLine.length(), this.width * 2);
                }
                this.colorLines[i] = textColorLine + bgColorLine;
            }else{
                this.lines[i] = this.emptyLine;
                this.colorLines[i] = this.emptyColorLine;
            }
            this.lineChanged[i] = true;
            this.colorLineChanged[i] = true;
        }

        this.changed = true;
    }

    public void setCursorPos(int x, int y){
        if(this.cursorX != x || this.cursorY != y){
            this.cursorX = x;
            this.cursorY = y;
            this.changed = true;
        }
    }

    public void setCursorBlink(boolean blink){
        if(this.cursorBlink != blink){
            this.cursorBlink = blink;
            this.changed = true;
        }
    }

    private void rebuildEmptyLine(){
        char[] spaces = new char[this.width];
        Arrays.fill(spaces, ' ');
        this.emptyLine = new String(spaces);
    }

    private void rebuildEmptyColorLine(){
        char textColorChar = base16.charAt(this.textColor);
        char[] textColorChars = new char[this.width];
        Arrays.fill(textColorChars, textColorChar);

        char bgColorChar = base16.charAt(this.backgroundColor);
        char[] bgColorChars = new char[this.width];
        Arrays.fill(bgColorChars, bgColorChar);

        this.emptyColorLine = new String(textColorChars) + new String(bgColorChars);
    }

    public void setTextColor(int color){
        if(this.textColor != color){
            this.textColor = color;
            this.changed = true;
            rebuildEmptyColorLine();
        }
    }

    public void setBackgroundColor(int color){
        if(this.backgroundColor != color){
            this.backgroundColor = color;
            this.changed = true;
            rebuildEmptyColorLine();
        }
    }

    public void write(String line){
        if(this.cursorY >= 0 && this.cursorY < this.height){
            int writeX = this.cursorX;
            int spaceLeft = this.width - this.cursorX;
            if(spaceLeft > this.width + line.length()){
                return;
            }
            if(spaceLeft > this.width){
                writeX = 0;
                line = line.substring(spaceLeft - this.width);
                spaceLeft = this.width;
            }
            line = line.replace('\t', ' ');

            if(spaceLeft > 0){
                String oldLine = this.lines[this.cursorY];
                String oldColorLine = this.colorLines[this.cursorY];
                String oldTextLine = oldColorLine.substring(0, oldLine.length());
                String oldBackgroundLine = oldColorLine.substring(oldLine.length(), 2 * oldLine.length());
                StringBuilder newLine = new StringBuilder();
                StringBuilder newTextLine = new StringBuilder();
                StringBuilder newBackgroundLine = new StringBuilder();
                newLine.append(oldLine.substring(0, writeX));
                newTextLine.append(oldTextLine.substring(0, writeX));
                newBackgroundLine.append(oldBackgroundLine.substring(0, writeX));

                if(line.length() < spaceLeft){
                    newLine.append(line);
                    newTextLine.append(this.emptyColorLine.substring(0, line.length()));
                    newBackgroundLine.append(this.emptyColorLine.substring(oldLine.length(), oldLine.length() + line.length()));

                    newLine.append(oldLine.substring(writeX + line.length()));
                    newTextLine.append(oldTextLine.substring(writeX + line.length()));
                    newBackgroundLine.append(oldBackgroundLine.substring(writeX + line.length()));
                }else{
                    newLine.append(line.substring(0, spaceLeft));
                    newTextLine.append(this.emptyColorLine.substring(0, spaceLeft));
                    newBackgroundLine.append(this.emptyColorLine.substring(oldLine.length(), oldLine.length() + spaceLeft));
                }
                this.lines[this.cursorY] = newLine.toString();
                this.colorLines[this.cursorY] = newTextLine.toString() + newBackgroundLine.toString();

                if(!this.lines[this.cursorY].equals(oldLine)){
                    this.changed = true;
                    this.lineChanged[this.cursorY] = true;
                }
                if(!this.colorLines[this.cursorY].equals(oldColorLine)){
                    this.changed = true;
                    this.colorLineChanged[this.cursorY] = true;
                }
            }
        }
    }

    public void scroll(int yDiff){
        String[] newLines = new String[this.height];
        String[] newColorLines = new String[this.height];
        for(int y = 0; y < this.height; y++){
            int oldY = y + yDiff;
            if((oldY >= 0) && (oldY < this.height)){
                newLines[y] = this.lines[oldY];
                newColorLines[y] = this.colorLines[oldY];
            }else{
                newLines[y] = this.emptyLine;
                newColorLines[y] = this.emptyColorLine;
            }

            if(!newLines[y].equals(this.lines[y])){
                this.changed = true;
                this.lineChanged[y] = true;
            }
            if(!newColorLines[y].equals(this.colorLines[y])){
                this.changed = true;
                this.colorLineChanged[y] = true;
            }
        }
        this.lines = newLines;
        this.colorLines = newColorLines;
    }

    public void clear(){
        for(int y = 0; y < this.height; y++){
            if(!this.lines[y].equals(this.emptyLine)){
                this.lines[y] = this.emptyLine;
                this.lineChanged[y] = true;
                this.changed = true;
            }

            if(!this.colorLines[y].equals(this.emptyColorLine)){
                this.colorLines[y] = this.emptyColorLine;
                this.colorLineChanged[y] = true;
                this.changed = true;
            }
        }
    }

    public void clearLine(){
        if(this.cursorY >= 0 && this.cursorY < this.height){
            if(!this.lines[this.cursorY].equals(this.emptyLine)){
                this.lines[this.cursorY] = this.emptyLine;
                this.lineChanged[this.cursorY] = true;
                this.changed = true;
            }

            if(!this.colorLines[this.cursorY].equals(this.emptyColorLine)){
                this.colorLines[this.cursorY] = this.emptyColorLine;
                this.colorLineChanged[this.cursorY] = true;
                this.changed = true;
            }
        }
    }

    public String getLine(int y){
        if(y >= 0 && y < this.height){
            return this.lines[y];
        }
        return this.emptyLine;
    }

    public void setLine(int y, String line, String color){
        this.lines[y] = (line + this.emptyLine).substring(0, this.width);
        this.colorLines[y] = (color + this.emptyColorLine).substring(0, this.width * 2);

        this.lineChanged[y] = true;
        this.colorLineChanged[y] = true;
        this.changed = true;
    }

    public String getColorLine(int y){
        if(y >= 0 && y < this.height){
            return this.colorLines[y];
        }
        return "";
    }

    public void clearChanged(){
        if(this.changed){
            this.changed = false;
            for(int y = 0; y < this.height; y++){
                this.lineChanged[y] = false;
                this.colorLineChanged[y] = false;
            }
        }
    }
}
