package jk_5.nailed.api.plugin;

import java.io.*;

import jk_5.eventbus.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class DefaultPluginManager implements PluginManager {

    @Override
    public void registerLoader(Class<? extends PluginLoader> loader) throws IllegalArgumentException {

    }

    @Override
    public Plugin getPlugin(String name) {
        return null;
    }

    @Override
    public Plugin[] getPlugins() {
        return new Plugin[0];
    }

    @Override
    public boolean isPluginEnabled(String name) {
        return false;
    }

    @Override
    public boolean isPluginEnabled(Plugin plugin) {
        return false;
    }

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, InvalidMetadataException, UnknownDependencyException {
        return null;
    }

    @Override
    public Plugin[] loadPlugins(File directory) {
        return new Plugin[0];
    }

    @Override
    public void disablePlugins() {

    }

    @Override
    public void clearPlugins() {

    }

    @Override
    public void callEvent(Event event) throws IllegalStateException {

    }

    @Override
    public void registerEventListener(Object listener, Plugin plugin) {

    }
}
