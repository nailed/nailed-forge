package jk_5.nailed.map.script;

import jk_5.nailed.NailedServer;
import jk_5.nailed.api.map.Map;
import jk_5.nailed.api.scripting.*;
import net.minecraft.nbt.NBTTagCompound;

import java.io.File;

/**
 * No description given
 *
 * @author jk-5
 */
public class MachineSynchronizer extends TerminalSynchronizer implements IMachineSynchronizer {

    private ScriptingMachine machine;
    private int rebootTimer;

    public MachineSynchronizer(Map owner, int terminalWidth, int terminalHeight){
        super(owner, terminalWidth, terminalHeight);
        this.terminal = new Terminal(terminalWidth, terminalHeight);
        this.machine = new ScriptingMachine(this, this.terminal);
    }

    public IAPIEnvironment getAPIEnvironment(){
        return this.machine.getApiEnvironment();
    }

    public void unload(){
        this.machine.unload();
    }

    public void destroy(){
        this.machine.destroy();
    }

    public void update(){
        super.update();
        this.machine.advance(0.05);
        if(this.rebootTimer > 0){
            this.rebootTimer -= 1;
        }
    }

    public void writeToNBT(NBTTagCompound nbttagcompound){
        super.writeToNBT(nbttagcompound);
        if(this.machine != null){
            this.machine.writeToNBT(nbttagcompound);
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound){
        super.readFromNBT(nbttagcompound);
        if(this.machine != null){
            this.machine.readFromNBT(nbttagcompound);
        }
    }

    public void pressKey(char ch, int keycode){
        this.machine.pressKey(ch, keycode);
    }

    public void typeString(String text){
        for(int n = 0; n < text.length(); n++){
            this.machine.pressKey(text.charAt(n), -1);
        }
    }

    public void clickMouse(int charX, int charY, int button){
        this.machine.clickMouse(charX, charY, button);
    }

    public void fireEvent(String event){
        this.machine.queueLuaEvent(event);
    }

    public void turnOn(){
        this.machine.turnOn(null);
    }

    public boolean isOn(){
        return this.machine.isOn();
    }

    public void shutdown(){
        this.machine.turnOff();
    }

    public void reboot(){
        this.machine.reboot();
    }

    public void terminate(){
        this.machine.terminate();
    }

    public boolean isCursorVisible(){
        return this.machine.isBlinking();
    }

    public int getMachineID(){
        return this.owner.getID();
    }

    public void addAPI(ILuaAPI api){
        this.machine.addAPI(api);
    }

    public String[] getMethodNames(){
        return new String[]{"turnOn", "shutdown", "reboot", "getID"};
    }

    public Object[] callMethod(ScriptingMachine machine, ILuaContext context, int method, Object[] arguments) throws Exception{
        switch(method){
            case 0:
                if(this.rebootTimer == 0){
                    turnOn();
                    this.rebootTimer = 60;
                }
                return null;
            case 1:
                shutdown();
                return null;
            case 2:
                if(this.rebootTimer == 0){
                    reboot();
                    this.rebootTimer = 60;
                }
                return null;
            case 3:
                if(isOn()){
                    return new Object[]{getMachineID()};
                }
                return null;
        }
        return null;
    }

    public double getTimeOfDay(){
        return (this.owner.getWorld().getWorldTime() + 6000L) % 24000L / 1000.0D;
    }

    public int getDay(){
        return (int) ((this.owner.getWorld().getWorldTime() + 6000L) / 24000L) + 1;
    }

    public IWritableMount createSaveDirMount(String subPath, long capacity){
        return new FileMount(new File(this.owner.getSaveFolder(), subPath), capacity);
    }

    public IMount createResourceMount(String domain, String subPath){
        return MountUtils.createResourceMount(NailedServer.class, domain, subPath);
    }

    public String getDescription(){
        return this.owner.getSaveFileName();
    }

    public long getMachineSpaceLimit(){
        return 1000000; //1MB
    }
}
