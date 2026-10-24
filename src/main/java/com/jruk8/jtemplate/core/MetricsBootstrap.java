package com.jruk8.jtemplate.core;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class MetricsBootstrap implements Bootstrap {

    private static final int PLUGIN_ID = 34924;
    private final JavaPlugin plugin;

    public MetricsBootstrap(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void register() {
        new Metrics(plugin, PLUGIN_ID);
    }
}
