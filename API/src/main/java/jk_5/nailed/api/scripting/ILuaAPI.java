package jk_5.nailed.api.scripting;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ILuaAPI extends ILuaObject {

    String[] getNames();
    void startup();
    void advance(double paramDouble);
    void shutdown();
}
