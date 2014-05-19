package jk_5.nailed.api.plugin;

import jk_5.nailed.api.NailedAPI;
import jk_5.nailed.api.command.Command;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * No description given
 *
 * @author jk-5
 */
public abstract class Plugin {

    private boolean enabled = false;
    private Logger logger = LogManager.getLogger();

    /**
     * Returns the name of the plugin.
     * <p>
     * This should return the bare name of the plugin and should be used for
     * comparison.
     *
     * @return name of the plugin
     */
    public String getName(){
        return this.getClass().getSimpleName();
    }

    /**
     * Registers a command to the command system
     * @param command The command to register
     */
    public final void registerCommand(Command command){
        command.setOwner(this);
        NailedAPI.getCommandRegistry().registerCommand(command);
    }

    /**
     * Returns a value indicating whether or not this plugin is currently
     * enabled
     *
     * @return true if this plugin is enabled, otherwise false
     */
    public boolean isEnabled(){
        return this.enabled;
    }

    /**
     * Called when this plugin is disabled
     */
    public void onDisable(){}

    /**
     * Called after a plugin is loaded but before it has been enabled.
     * <p>
     * When mulitple plugins are loaded, the onLoad() for all plugins is
     * called before any onEnable() is called.
     */
    public void onLoad(){}

    /**
     * Called when this plugin is enabled
     */
    public void onEnable(){}

    public Logger getLogger() {
        return logger;
    }
}
