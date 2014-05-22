package jk_5.nailed.api.plugin;

import java.io.*;

import jk_5.eventbus.*;

/**
 * Handles all plugin management from the Server
 *
 * @author jk-5
 */
public interface PluginManager {

    /**
     * Registers the specified plugin loader
     *
     * @param loader Class name of the PluginLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *                                  valid PluginLoader
     */
    void registerLoader(Class<? extends PluginLoader> loader) throws IllegalArgumentException;

    /**
     * Checks if the given plugin is loaded and returns it when applicable
     * <p/>
     * Please note that the name of the plugin is case-sensitive
     *
     * @param name Name of the plugin to check
     * @return Plugin if it exists, otherwise null
     */
    Plugin getPlugin(String name);

    /**
     * Gets a list of all currently loaded plugins
     *
     * @return Array of Plugins
     */
    Plugin[] getPlugins();

    /**
     * Checks if the given plugin is enabled or not
     * <p/>
     * Please note that the name of the plugin is case-sensitive.
     *
     * @param name Name of the plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    boolean isPluginEnabled(String name);

    /**
     * Checks if the given plugin is enabled or not
     *
     * @param plugin Plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    boolean isPluginEnabled(Plugin plugin);

    /**
     * Loads the plugin in the specified file
     * <p/>
     * File must be valid according to the current enabled Plugin interfaces
     *
     * @param file File containing the plugin to load
     * @return The Plugin loaded, or null if it was invalid
     * @throws InvalidPluginException     Thrown when the specified file is not a
     *                                    valid plugin
     * @throws InvalidMetadataException   Thrown when the specified file
     *                                    contains an invalid metadata file
     * @throws UnknownDependencyException If a required dependency could not
     *                                    be resolved
     */
    Plugin loadPlugin(File file) throws InvalidPluginException, InvalidMetadataException, UnknownDependencyException;

    /**
     * Loads the plugins contained within the specified directory
     *
     * @param directory Directory to check for plugins
     * @return A list of all plugins loaded
     */
    Plugin[] loadPlugins(File directory);

    /**
     * Disables all the loaded plugins
     */
    void disablePlugins();

    /**
     * Disables and removes all plugins
     */
    void clearPlugins();

    /**
     * Calls an event with the given details
     *
     * @param event Event details
     * @throws IllegalStateException Thrown when an asynchronous event is
     *                               fired from synchronous code.
     *                               <p/>
     *                               <i>Note: This is best-effort basis, and should not be used to test
     *                               synchronized state. This is an indicator for flawed flow logic.</i>
     */
    void callEvent(Event event) throws IllegalStateException;

    /**
     * Registers all the events in the given listener class
     *
     * @param listener Listener to register
     * @param plugin   Plugin to register
     */
    void registerEventListener(Object listener, Plugin plugin);
}
