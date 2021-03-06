package jk_5.nailed.map.script;

import java.io.File;
import java.io.InputStream;
import java.util.List;

import com.google.common.collect.Lists;

import org.apache.commons.io.IOUtils;

import net.minecraftforge.common.MinecraftForge;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.events.RegisterLuaApiEvent;
import jk_5.nailed.api.scripting.ILuaAPI;
import jk_5.nailed.api.scripting.IMount;
import jk_5.nailed.api.scripting.IWritableMount;
import jk_5.nailed.map.script.api.EventApi;
import jk_5.nailed.map.script.api.FileSystemApi;
import jk_5.nailed.map.script.api.MapApi;
import jk_5.nailed.map.script.api.OSApi;
import jk_5.nailed.map.script.api.ScoreboardApi;
import jk_5.nailed.map.script.api.TermApi;

/**
 * No description given
 *
 * @author jk-5
 */
public class ScriptingMachine {

    private static IMount romMount = null;

    private boolean isUnloaded = false;

    private final int id;
    private final ServerMachine machine;
    private int ticksSinceStart = -1;
    private boolean blinking = false;
    private boolean startupRequested = false;
    private MachineState state = MachineState.OFF;
    private LuaMachine luaMachine = null;
    private List<ILuaAPI> apis = Lists.newArrayList();
    private APIEnvironment apiEnvironment = new APIEnvironment(this);
    private Terminal terminal;
    private FileSystem fileSystem = null;

    public ScriptingMachine(ServerMachine machine, Terminal terminal, int id) {
        ScriptThread.start();

        this.id = id;
        this.machine = machine;
        this.terminal = terminal;

        createAPIs();
    }

    public void turnOn() {
        if(this.state == MachineState.OFF){
            this.startupRequested = true;
        }
    }

    public void shutdown() {
        stopScriptingMachine(false);
    }

    public void reboot() {
        stopScriptingMachine(true);
    }

    public boolean isOn() {
        synchronized(this){
            return this.state == MachineState.RUNNING || this.isUnloaded;
        }
    }

    public void abort(boolean hard) {
        synchronized(this){
            if(this.state == MachineState.RUNNING){
                if(hard){
                    this.luaMachine.abortHard("Too long without yielding");
                }else{
                    this.luaMachine.abortSoft("Too long without yielding");
                }
            }
        }
    }

    public void destroy() {
        synchronized(this){
            shutdown();
        }
    }

    public void unload() {
        synchronized(this){
            if(isOn()){
                this.isUnloaded = true;
                this.stopScriptingMachine(false);
            }
        }
    }

    public int getID() {
        return this.id;
    }

    public void advance(double dT) {
        synchronized(this){
            if(this.ticksSinceStart >= 0){
                this.ticksSinceStart += 1;
            }
            if(this.startupRequested && (this.ticksSinceStart < 0 || this.ticksSinceStart > 50)){
                this.startScriptingMachine();
                this.startupRequested = false;
            }
            if(this.state == MachineState.RUNNING){
                //noinspection SynchronizeOnNonFinalField
                synchronized(this.apis){
                    for(ILuaAPI api : this.apis){
                        api.advance(dT);
                    }
                }
            }
        }

        //noinspection SynchronizeOnNonFinalField
        synchronized(this.terminal){
            boolean blinking = this.terminal.isCursorBlink() && this.terminal.getCursorX() >= 0 && this.terminal.getCursorX() < this.terminal.getWidth() && this.terminal.getCursorY() >= 0 && this.terminal.getCursorY() < this.terminal.getHeight();
            if(blinking != this.blinking){
                this.blinking = blinking;
            }
        }
    }

    private boolean initFileSystem() {
        int id = this.getID();
        try{
            ServerMachine machine = this.machine;
            File dir = machine.getPreferredSaveDir();
            IWritableMount saveDirMount;
            if(dir == null){
                saveDirMount = machine.createSaveDirMount("machine/" + id, machine.getMachineSpaceLimit());
            }else{
                saveDirMount = new FileMount(dir, machine.getMachineSpaceLimit());
            }
            this.fileSystem = new FileSystem("hdd", saveDirMount);
            if(romMount == null){
                romMount = machine.createResourceMount("nailed", "lua/rom");
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

    private void createAPIs() {
        this.apis.add(new TermApi(this.apiEnvironment));
        this.apis.add(new FileSystemApi(this.apiEnvironment));
        this.apis.add(new OSApi(this.apiEnvironment));
        this.apis.add(new MapApi(this.apiEnvironment));
        this.apis.add(new ScoreboardApi(this.apiEnvironment));
        this.apis.add(new EventApi(this.apiEnvironment));
        MinecraftForge.EVENT_BUS.post(new RegisterLuaApiEvent(NailedAPI.getMapLoader().getMap(this.apiEnvironment.getMachine().getMachine().getWorld()), this.apis));
    }

    private void initLua() {
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

    private void startScriptingMachine() {
        synchronized(this){
            if(this.state != MachineState.OFF){
                return;
            }
            this.state = MachineState.STARTING;
            this.ticksSinceStart = 0;
        }

        ScriptThread.queueTask(new ScriptThread.Task() {

            @Override
            public ScriptingMachine getOwner() {
                return ScriptingMachine.this;
            }

            @Override
            public void execute() {
                synchronized(this){
                    if(ScriptingMachine.this.state != MachineState.STARTING){
                        return;
                    }

                    //noinspection SynchronizeOnNonFinalField
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

                        ScriptingMachine.this.state = MachineState.RUNNING;
                        ScriptingMachine.this.stopScriptingMachine(false);
                        return;
                    }

                    ScriptingMachine.this.initLua();
                    if(ScriptingMachine.this.luaMachine == null){
                        ScriptingMachine.this.terminal.setCursorBlink(false);
                        ScriptingMachine.this.terminal.write("Error loading bios.lua");
                        ScriptingMachine.this.terminal.setCursorPos(0, ScriptingMachine.this.terminal.getCursorY() + 1);
                        ScriptingMachine.this.terminal.write("Contact the server admin");

                        ScriptingMachine.this.state = MachineState.RUNNING;
                        ScriptingMachine.this.stopScriptingMachine(false);
                        return;
                    }

                    //noinspection SynchronizeOnNonFinalField
                    synchronized(ScriptingMachine.this.luaMachine){
                        //noinspection NullArgumentToVariableArgMethod
                        ScriptingMachine.this.luaMachine.handleEvent(null);
                    }
                    ScriptingMachine.this.state = MachineState.RUNNING;
                }
            }
        }, this);
    }

    private void stopScriptingMachine(final boolean reboot) {
        synchronized(this){
            if(this.state != MachineState.RUNNING){
                return;
            }
            this.state = MachineState.STOPPING;
        }

        ScriptThread.queueTask(new ScriptThread.Task() {

            @Override
            public ScriptingMachine getOwner() {
                return ScriptingMachine.this;
            }

            @Override
            public void execute() {
                synchronized(this){
                    if(ScriptingMachine.this.state != MachineState.STOPPING){
                        return;
                    }

                    //noinspection SynchronizeOnNonFinalField
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
                        //noinspection SynchronizeOnNonFinalField
                        synchronized(ScriptingMachine.this.terminal){
                            ScriptingMachine.this.terminal.setTextColor(15);
                            ScriptingMachine.this.terminal.setBackgroundColor(0);
                            ScriptingMachine.this.terminal.clear();
                            ScriptingMachine.this.terminal.setCursorPos(0, 0);
                            ScriptingMachine.this.terminal.setCursorBlink(false);
                        }

                        //noinspection SynchronizeOnNonFinalField
                        synchronized(ScriptingMachine.this.luaMachine){
                            ScriptingMachine.this.luaMachine.unload();
                            ScriptingMachine.this.luaMachine = null;
                        }
                    }

                    ScriptingMachine.this.state = MachineState.OFF;
                    if(reboot){
                        ScriptingMachine.this.startupRequested = true;
                    }
                }
            }
        }, this);
    }

    public void queueEvent(final String event, final Object... arguments) {
        synchronized(this){
            if(this.state != MachineState.RUNNING){
                return;
            }
        }

        ScriptThread.queueTask(new ScriptThread.Task() {

            public ScriptingMachine getOwner() {
                return ScriptingMachine.this;
            }

            public void execute() {
                synchronized(this){
                    if(ScriptingMachine.this.state != MachineState.RUNNING){
                        return;
                    }
                }

                //noinspection SynchronizeOnNonFinalField
                synchronized(ScriptingMachine.this.luaMachine){
                    ScriptingMachine.this.luaMachine.handleEvent(event, arguments);
                    if(ScriptingMachine.this.luaMachine.isFinished()){
                        ScriptingMachine.this.terminal.setCursorBlink(false);
                        ScriptingMachine.this.terminal.write("Error resuming bios.lua");
                        ScriptingMachine.this.terminal.setCursorPos(0, ScriptingMachine.this.terminal.getCursorY() + 1);
                        ScriptingMachine.this.terminal.write("Contact the server admin");
                        ScriptingMachine.this.stopScriptingMachine(false);
                    }
                }
            }
        }, this);
    }

    public ServerMachine getMachine() {
        return this.machine;
    }

    public LuaMachine getLuaMachine() {
        return this.luaMachine;
    }

    public APIEnvironment getApiEnvironment() {
        return this.apiEnvironment;
    }

    private static class APIEnvironment implements IAPIEnvironment {

        private ScriptingMachine machine;

        public APIEnvironment(ScriptingMachine machine) {
            this.machine = machine;
        }

        @Override
        public ScriptingMachine getMachine() {
            return this.machine;
        }

        @Override
        public int getMachineID() {
            return this.machine.getID();
        }

        @Override
        public Terminal getTerminal() {
            return this.machine.terminal;
        }

        @Override
        public FileSystem getFileSystem() {
            return this.machine.fileSystem;
        }

        @Override
        public void queueEvent(String event, Object... args) {
            this.machine.queueEvent(event, args);
        }

        @Override
        public void shutdown() {
            this.machine.shutdown();
        }

        @Override
        public void reboot() {
            this.machine.reboot();
        }
    }
}
