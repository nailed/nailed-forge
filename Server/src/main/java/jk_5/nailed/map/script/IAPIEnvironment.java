package jk_5.nailed.map.script;

/**
 * No description given
 *
 * @author jk-5
 */
public interface IAPIEnvironment {

    ScriptingMachine getMachine();
    int getMachineID();
    Terminal getTerminal();
    FileSystem getFileSystem();
    void queueEvent(String paramString, Object... paramArrayOfObject);
    void shutdown();
    void reboot();
}
