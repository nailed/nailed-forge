package jk_5.nailed.map.script;

import java.io.*;

import io.netty.buffer.*;

import net.minecraft.world.*;

import jk_5.nailed.*;
import jk_5.nailed.api.scripting.*;
import jk_5.nailed.map.*;
import jk_5.nailed.network.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class ServerMachine {

    public static final MachineRegistry<ServerMachine> REGISTRY = new MachineRegistry<ServerMachine>();

    private Terminal terminal;
    private World world;
    private final int instanceId;
    private final ScriptingMachine machine;
    private File preferredSaveDir = null;
    public boolean terminalChanged = false;

    public ServerMachine(World world, int id, int instanceId, int width, int height) {
        this.terminal = new Terminal(width, height);
        this.world = world;
        this.instanceId = instanceId;
        this.machine = new ScriptingMachine(this, this.terminal, id);
    }

    public int getId() {
        return this.machine.getID();
    }

    public IAPIEnvironment getApiEnvironment() {
        return this.machine.getApiEnvironment();
    }

    public void destroy() {
        this.machine.destroy();
    }

    public void update() {
        double dt = 0.05D;
        this.machine.advance(dt);

        if(this.needsResync()){
            ByteBuf buffer = Unpooled.buffer();
            this.writeData(buffer);
            NailedNetworkHandler.sendPacketToAllPlayersInDimension(new ScriptPacket.UpdateMachine(this.instanceId, buffer), this.world.provider.dimensionId);
        }
    }

    public boolean isOn() {
        return this.machine.isOn();
    }

    public void turnOn() {
        this.machine.turnOn();
    }

    public void shutdown() {
        this.machine.shutdown();
    }

    public void reboot() {
        this.machine.reboot();
    }

    public void unload() {
        this.machine.unload();
    }

    public void queueEvent(String event, Object... arguments) {
        this.machine.queueEvent(event, arguments);
    }

    public double getTimeOfDay() {
        return (this.world.getWorldTime() + 6000L) % 24000L / 1000.0D;
    }

    public int getDay() {
        return (int) ((this.world.getWorldTime() + 6000L) / 24000L) + 1;
    }

    public IWritableMount createSaveDirMount(String subPath, long capacity) {
        return new FileMount(new File(NailedMapLoader.instance().getMap(this.world).getSaveFolder(), subPath), capacity);
    }

    public IMount createResourceMount(String domain, String subPath) {
        return MountUtils.createResourceMount(NailedServer.class, domain, subPath);
    }

    public long getMachineSpaceLimit() {
        return 1000000; //1MB
    }

    public World getWorld() {
        return this.world;
    }

    public int getInstanceId() {
        return this.instanceId;
    }

    public File getPreferredSaveDir() {
        return this.preferredSaveDir;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setPreferredSaveDir(File preferredSaveDir) {
        this.preferredSaveDir = preferredSaveDir;
    }

    public ScriptingMachine getVM() {
        return this.machine;
    }

    public boolean needsResync() {
        if(this.terminalChanged || (this.terminal != null && this.terminal.isChanged())){
            if(this.terminal != null){
                this.terminal.clearChanged();
            }
            this.terminalChanged = false;
            return true;
        }
        return false;
    }

    public void writeData(ByteBuf buffer) {
        if(this.terminal != null){
            buffer.writeBoolean(true);
            buffer.writeInt(this.terminal.getWidth());
            buffer.writeInt(this.terminal.getHeight());
            this.terminal.writeData(buffer);
        }else{
            buffer.writeBoolean(false);
        }
    }

    public Terminal getTerminal() {
        return this.terminal;
    }
}
