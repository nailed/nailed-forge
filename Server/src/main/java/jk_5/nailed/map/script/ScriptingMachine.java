package jk_5.nailed.map.script;

import com.google.common.collect.Lists;
import jk_5.nailed.NailedLog;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.map.script.api.FileSystemApi;
import jk_5.nailed.map.script.api.OSApi;
import jk_5.nailed.map.script.api.TermApi;
import lombok.Getter;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.List;

/**
 * No description given
 *
 * @author jk-5
 */
public class ScriptingMachine {

    private static final String ALLOWED_CHARS = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_'abcdefghijklmnopqrstuvwxyz{|}~⌂ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»";

    private boolean isUnloaded = false;

    private static IMount romMount = null;
    private MachineSynchronizer machineSynchronizer;
    private int id = -1;
    private int delayedStart = -1;
    private byte[] delayedStartData = null;
    private int state = MachineState.OFF;
    private LuaMachine luaMachine = null;
    private List<ILuaAPI> apis = Lists.newArrayList();
    @Getter private APIEnvironment apiEnvironment = new APIEnvironment(this);
    private Terminal terminal;
    private FileSystem fileSystem = null;
    private int lastTermState = 15;

    public ScriptingMachine(MachineSynchronizer synchronizer, Terminal terminal){
        ScriptThread.start();

        this.machineSynchronizer = synchronizer;
        this.terminal = terminal;

        createAPIs();
    }

    public String getDescription(){
        return this.machineSynchronizer.getDescription();
    }

    public void turnOn(byte[] previousState){
        startScriptingMachine(previousState);
    }

    public void turnOff(){
        stopScriptingMachine(false, 0);
    }

    public void reboot(){
        reboot(0);
    }

    private void reboot(int delay){
        stopScriptingMachine(true, delay);
    }

    public boolean isOn(){
        synchronized(this){
            return (this.state == 2) || (this.isUnloaded);
        }
    }

    public void abort(boolean hard){
        synchronized(this){
            if(this.state == 2){
                if(hard){
                    this.luaMachine.abortHard("Too long without yielding");
                }else{
                    this.luaMachine.abortSoft("Too long without yielding");
                }
            }
        }
    }

    public void destroy(){
        synchronized(this){
            turnOff();
        }
    }

    public void unload(){
        synchronized(this){
            if(isOn()){
                this.isUnloaded = true;
                stopScriptingMachine(false, 0);
            }
        }
    }

    public synchronized void writeToNBT(NBTTagCompound nbttagcompound){
        int id = getID();
        if(id >= 0){
            nbttagcompound.setString("userDir", Integer.toString(id));
        }
    }

    public synchronized void readFromNBT(NBTTagCompound nbttagcompound){
        String userDir = nbttagcompound.getString("userDir");
        if(userDir != null && userDir.length() > 0){
            try{
                setID(Integer.parseInt(userDir));
            }catch(NumberFormatException e){
                NailedLog.error("Error: Machine has non-numerical userDir; this is not allowed. A new ID will be assigned.");
                setID(-1);
            }
        }

        this.delayedStart = 0;
        this.delayedStartData = null;
    }

    public int getID(){
        synchronized(this){
            return this.id;
        }
    }

    private int getOrCreateID(){
        synchronized(this){
            if(this.id < 0){
                this.id = this.machineSynchronizer.getMachineID();
            }
            return this.id;
        }
    }

    public void setID(int id){
        synchronized(this){
            this.id = id;
        }
    }

    public void pressKey(char ch, int key){
        synchronized(this){
            if(this.state == 2){
                if(key >= 0){
                    queueLuaEvent("key", key);
                }

                if(ALLOWED_CHARS.indexOf(ch) != -1){
                    queueLuaEvent("char", "" + ch);
                }
            }
        }
    }

    public void clickMouse(int charX, int charY, int button){
        synchronized(this){
            if(this.state == 2){
                switch(button){
                    case 0: case 1: case 2:
                        queueLuaEvent("mouse_click", button + 1, charX + 1, charY + 1);
                        break;
                    case 3:
                        queueLuaEvent("mouse_scroll", 1, charX + 1, charY + 1);
                        break;
                    case 4:
                        queueLuaEvent("mouse_scroll", -1, charX + 1, charY + 1);
                        break;
                    case 5: case 6: case 7:
                        queueLuaEvent("mouse_drag", button - 5 + 1, charX + 1, charY + 1);
                        break;
                }
            }
        }
    }

    public void terminate(){
        synchronized(this){
            if(this.state == 2){
                queueLuaEvent("terminate");
            }
        }
    }

    public void advance(double _dt){
        synchronized(this){
            if(this.delayedStart >= 0){
                this.delayedStart -= 1;
                if(this.delayedStart <= 0){
                    turnOn(this.delayedStartData);
                    this.delayedStart = -1;
                    this.delayedStartData = null;
                }
            }
        }

        synchronized(this.terminal){
            boolean blinking = (this.terminal.isCursorBlink()) && (this.terminal.getCursorX() >= 0) && (this.terminal.getCursorX() < this.terminal.getWidth()) && (this.terminal.getCursorY() >= 0) && (this.terminal.getCursorY() < this.terminal.getHeight());

            int termState = blinking ? 256 : 0;
            if(termState != this.lastTermState){
                this.lastTermState = termState;
            }
        }
    }

    public boolean isBlinking(){
        synchronized(this.terminal){
            return isOn() && ((this.lastTermState & 0x100) > 0);
        }
    }

    private boolean initFileSystem(){
        int id = getOrCreateID();
        try{
            this.fileSystem = new FileSystem("hdd", this.machineSynchronizer.createSaveDirMount("machine/" + id, this.machineSynchronizer.getMachineSpaceLimit()));
            if(romMount == null){
                romMount = this.machineSynchronizer.createResourceMount("nailed", "lua/rom");
            }
            if(romMount != null){
                this.fileSystem.mount("rom", "rom", romMount);
                return true;
            }
            return false;
        }catch(FileSystemException e){
            e.printStackTrace();
        }
        return false;
    }

    public void addAPI(ILuaAPI api){
        this.apis.add(api);
    }

    private void createAPIs(){
        this.apis.add(new TermApi(this.apiEnvironment));
        this.apis.add(new FileSystemApi(this.apiEnvironment));
        this.apis.add(new OSApi(this.apiEnvironment));
        //this.apis.add(new BitAPI(this.apiEnvironment));
        //if(this.machineSynchronizer.isHTTPEnabled()){
        //    this.apis.add(new HTTPAPI(this.apiEnvironment));
        //}
    }

    private void initLua(){
        LuaMachine machine = new LuaMachine();

        for(ILuaAPI api : this.apis){
            machine.addAPI(api);
            api.startup();
        }

        InputStream biosStream;
        try{
            biosStream = ScriptingMachine.class.getResourceAsStream("/assets/nailed/lua/bios.lua");
        }catch(Exception e){
            biosStream = null;
        }

        if(biosStream != null){
            machine.loadBios(biosStream);
            IOUtils.closeQuietly(biosStream);
            if(machine.isFinished()){
                this.terminal.setCursorBlink(false);
                this.terminal.write("Error starting bios.lua");
                this.terminal.setCursorPos(0, this.terminal.getCursorY() + 1);
                this.terminal.write("Contact the server admin");

                machine.unload();
                this.luaMachine = null;
            }else{
                this.luaMachine = machine;
            }
        }else{
            this.terminal.setCursorBlink(false);
            this.terminal.write("Error loading bios.lua");
            this.terminal.setCursorPos(0, this.terminal.getCursorY() + 1);
            this.terminal.write("Contact the server admin");

            machine.unload();
            this.luaMachine = null;
        }
    }

    private void startScriptingMachine(final byte[] previousState){
        synchronized(this){
            if(this.state != 0){
                return;
            }
            this.state = 1;
        }

        ScriptThread.queueTask(new ScriptThread.Task() {
            
            @Override
            public ScriptingMachine getOwner(){
                return ScriptingMachine.this;
            }

            @Override
            public void execute(){
                synchronized(this){
                    if(ScriptingMachine.this.state != 1){
                        return;
                    }

                    synchronized(ScriptingMachine.this.terminal){
                        ScriptingMachine.this.terminal.setTextColor(15);
                        ScriptingMachine.this.terminal.setBackgroundColor(0);
                        ScriptingMachine.this.terminal.clear();
                        ScriptingMachine.this.terminal.setCursorPos(0, 0);
                        ScriptingMachine.this.terminal.setCursorBlink(false);
                    }

                    if(!ScriptingMachine.this.initFileSystem()){
                        ScriptingMachine.this.terminal.setCursorBlink(false);
                        ScriptingMachine.this.terminal.write("Error mounting lua/rom.");
                        ScriptingMachine.this.terminal.setCursorPos(0, ScriptingMachine.this.terminal.getCursorY() + 1);
                        ScriptingMachine.this.terminal.write("Contact the server admin");

                        ScriptingMachine.this.state = 2;
                        ScriptingMachine.this.stopScriptingMachine(false, 0);
                        return;
                    }

                    ScriptingMachine.this.initLua();
                    if(ScriptingMachine.this.luaMachine == null){
                        ScriptingMachine.this.terminal.setCursorBlink(false);
                        ScriptingMachine.this.terminal.write("Error loading bios.lua");
                        ScriptingMachine.this.terminal.setCursorPos(0, ScriptingMachine.this.terminal.getCursorY() + 1);
                        ScriptingMachine.this.terminal.write("Contact the server admin");

                        ScriptingMachine.this.state = 2;
                        ScriptingMachine.this.stopScriptingMachine(false, 0);
                        return;
                    }

                    if(previousState == null){
                        //noinspection SynchronizeOnNonFinalField
                        synchronized(ScriptingMachine.this.luaMachine){
                            //noinspection NullArgumentToVariableArgMethod
                            ScriptingMachine.this.luaMachine.handleEvent(null, null);
                        }
                    }

                    ScriptingMachine.this.state = 2;
                }
            }
        }, this);
    }

    private void stopScriptingMachine(final boolean reboot, final int rebootDelay){
        synchronized(this){
            if(this.state != 2){
                return;
            }
            this.state = 3;
        }

        ScriptThread.queueTask(new ScriptThread.Task() {

            @Override
            public ScriptingMachine getOwner(){
                return ScriptingMachine.this;
            }

            @Override
            public void execute(){
                synchronized(this){
                    if(ScriptingMachine.this.state != 3){
                        return;
                    }

                    synchronized(ScriptingMachine.this.apis){
                        for(ILuaAPI api : ScriptingMachine.this.apis){
                            api.shutdown();
                        }
                    }

                    if(ScriptingMachine.this.fileSystem != null){
                        ScriptingMachine.this.fileSystem.unload();
                        ScriptingMachine.this.fileSystem = null;
                    }

                    if(ScriptingMachine.this.luaMachine != null){
                        synchronized(ScriptingMachine.this.terminal){
                            ScriptingMachine.this.terminal.setTextColor(15);
                            ScriptingMachine.this.terminal.setBackgroundColor(0);
                            ScriptingMachine.this.terminal.clear();
                            ScriptingMachine.this.terminal.setCursorPos(0, 0);
                            ScriptingMachine.this.terminal.setCursorBlink(false);
                        }

                        synchronized(ScriptingMachine.this.luaMachine){
                            ScriptingMachine.this.luaMachine.unload();
                            ScriptingMachine.this.luaMachine = null;
                        }
                    }

                    ScriptingMachine.this.state = 0;
                    if(reboot){
                        ScriptingMachine.this.delayedStart = rebootDelay;
                        ScriptingMachine.this.delayedStartData = null;
                    }
                }
            }
        }, this);
    }

    @SuppressWarnings("NullArgumentToVariableArgMethod")
    public void queueLuaEvent(String event){
        queueLuaEvent(event, null);
    }

    public void queueLuaEvent(final String event, final Object... arguments){
        synchronized(this){
            if(this.state != 2){
                return;
            }
        }

        ScriptThread.queueTask(new ScriptThread.Task() {

            public ScriptingMachine getOwner(){
                return ScriptingMachine.this;
            }

            public void execute(){
                synchronized(this){
                    if(ScriptingMachine.this.state != 2){
                        return;
                    }
                }

                synchronized(ScriptingMachine.this.luaMachine){
                    ScriptingMachine.this.luaMachine.handleEvent(event, arguments);
                    if(ScriptingMachine.this.luaMachine.isFinished()){
                        ScriptingMachine.this.terminal.setCursorBlink(false);
                        ScriptingMachine.this.terminal.write("Error resuming bios.lua");
                        ScriptingMachine.this.terminal.setCursorPos(0, ScriptingMachine.this.terminal.getCursorY() + 1);
                        ScriptingMachine.this.terminal.write("Contact the server admin");
                        ScriptingMachine.this.stopScriptingMachine(false, 0);
                    }
                }
            }
        }, this);
    }

    private static class APIEnvironment implements IAPIEnvironment {

        private ScriptingMachine machine;

        public APIEnvironment(ScriptingMachine machine){
            this.machine = machine;
        }

        @Override
        public ScriptingMachine getMachine(){
            return this.machine;
        }

        @Override
        public int getMachineID(){
            return this.machine.getID();
        }

        @Override
        public MachineSynchronizer getSynchronizer(){
            return this.machine.machineSynchronizer;
        }

        @Override
        public Terminal getTerminal(){
            return this.machine.terminal;
        }

        @Override
        public FileSystem getFileSystem(){
            return this.machine.fileSystem;
        }

        @Override
        public void queueEvent(String event, Object[] args){
            this.machine.queueLuaEvent(event, args);
        }

        @Override
        public void shutdown(){
            this.machine.turnOff();
        }

        @Override
        public void reboot(int startupDelay){
            this.machine.reboot(startupDelay);
        }
    }
}
