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
public class ServerMachine extends ServerTerminal implements IMachine {

    public static final MachineRegistry<ServerMachine> REGISTRY = new MachineRegistry<ServerMachine>();

    private World world;
    private final int instanceId;
    private final ScriptingMachine machine;
    private File preferredSaveDir = null;

    public ServerMachine(World world, int id, int instanceId, int termWidth, int termHeight) {
        super(termWidth, termHeight);
        this.world = world;
        this.instanceId = instanceId;
        this.machine = new ScriptingMachine(this, this.getTerminal(), id);
    }

    @Override
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

        if(super.pollChanged()){
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

    public void writeData(ByteBuf buffer) {
        super.writeData(buffer);
        buffer.writeInt(this.machine.getID());
        buffer.writeBoolean(this.machine.isOn());
        buffer.writeBoolean(this.machine.isBlinking());
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
}
