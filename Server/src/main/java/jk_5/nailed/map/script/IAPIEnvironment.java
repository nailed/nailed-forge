package jk_5.nailed.map.script;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IAPIEnvironment {

    public ScriptingMachine getMachine();
    public int getMachineID();
    public MachineSynchronizer getSynchronizer();
    public Terminal getTerminal();
    public FileSystem getFileSystem();
    public void queueEvent(String paramString, Object[] paramArrayOfObject);
    public void shutdown();
    public void reboot(int paramInt);
}
