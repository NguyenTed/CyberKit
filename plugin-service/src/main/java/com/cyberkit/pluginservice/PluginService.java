package com.cyberkit.pluginservice;

import java.util.Map;

public interface PluginService {
    Map<String, Object> execute(String action, Map<String, Object> input);
}
