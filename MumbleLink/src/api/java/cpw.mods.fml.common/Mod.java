package cpw.mods.fml.common;

/**
 * No description given
 *
 * @author jk-5
 */
public @interface Mod {
    public String modid();
    public String name();
    public String version();
    public String modLanguage();
    public String dependencies();

    public @interface EventHandler{}
}
