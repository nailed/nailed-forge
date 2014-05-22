package jk_5.nailed.api.plugin.java;

import java.net.*;

/**
 * No description given
 *
 * @author jk-5
 */
public class PluginClassLoader extends URLClassLoader {

    public PluginClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }
}
