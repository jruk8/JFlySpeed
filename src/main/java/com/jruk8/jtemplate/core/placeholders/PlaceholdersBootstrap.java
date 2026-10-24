package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloadable;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.jruk8.jtemplate.core.storage.SqliteStorage;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

/**
 * Registers and tears down the PlaceholderAPI expansion and its backing
 * listener.
 * <p>
 * When PlaceholderAPI is not installed, or placeholders are disabled in the
 * config, registration is skipped gracefully. {@link #reload()} re-registers
 * everything so config changes (e.g. a new identifier) take effect.
 */
public final class PlaceholdersBootstrap implements Bootstrap, Reloadable {

    private static final String PLACEHOLDER_API_NAME = "PlaceholderAPI";

    private final PluginConfig pluginConfig;
    private final SqliteStorage storage;
    private final JavaPlugin plugin;
    private final Logger logger;

    private CommandUsageTracker tracker;
    private AuthorsCommandUsageListener listener;
    private AuthorsCommandUsageExpansion expansion;

    public PlaceholdersBootstrap(
            PluginConfig pluginConfig,
            SqliteStorage storage,
            JavaPlugin plugin,
            Logger logger
    ) {
        this.pluginConfig = pluginConfig;
        this.storage = storage;
        this.plugin = plugin;
        this.logger = logger;
    }

    @Override
    public void register() {
        if (!canRegister()) {
            return;
        }
        PluginConfig.Placeholders config = this.pluginConfig.getPlaceholders();

        this.tracker = new CommandUsageTracker(this.storage);
        this.listener = new AuthorsCommandUsageListener(
                this.tracker, config.getLabels(), config.getCommand());
        this.plugin.getServer().getPluginManager().registerEvents(this.listener, this.plugin);

        this.expansion = new AuthorsCommandUsageExpansion(config.getIdentifier(), this.tracker, this.plugin);
        if (this.expansion.register()) {
            this.logger.info("Registered placeholders with identifier '" + config.getIdentifier() + "'.");
        } else {
            unregister();
            this.logger.warning("PlaceholderAPI rejected the expansion registration.");
        }
    }

    @Override
    public void reload() {
        unregister();
        register();
    }

    public boolean unregister() {
        if (this.expansion != null) {
            this.expansion.unregister();
            this.expansion = null;
        }
        if (this.listener != null) {
            HandlerList.unregisterAll(this.listener);
            this.listener = null;
        }
        this.tracker = null;
        return true;
    }

    private boolean canRegister() {
        if (!this.pluginConfig.getPlaceholders().isEnabled()) {
            this.logger.info("Placeholders are disabled in the config; skipping registration.");
            return false;
        }
        if (this.plugin.getServer().getPluginManager().getPlugin(PLACEHOLDER_API_NAME) == null) {
            this.logger.info("PlaceholderAPI is not installed; skipping placeholder registration.");
            return false;
        }
        return true;
    }
}