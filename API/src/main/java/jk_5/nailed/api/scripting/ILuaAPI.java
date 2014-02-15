package jk_5.nailed.api.scripting;

/**
 * No description given
 *
 * @author jk-5
 */
public interface ILuaAPI extends ILuaObject {

    public String[] getNames();
    public void startup();
    public void advance(double paramDouble);
    public void shutdown();
}
