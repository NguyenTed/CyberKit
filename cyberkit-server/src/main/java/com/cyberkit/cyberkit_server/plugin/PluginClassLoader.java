package com.cyberkit.cyberkit_server.plugin;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.*;

public class PluginClassLoader extends URLClassLoader {
    public PluginClassLoader(Path jarPath) throws MalformedURLException {
        super(new URL[]{ jarPath.toUri().toURL() }, ClassLoader.getSystemClassLoader());
    }
}