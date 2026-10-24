package com.jruk8.jtemplate.core;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.CommandsRegistrar;
import com.jruk8.jtemplate.core.commands.speed.SpeedExecutor;
import com.jruk8.jtemplate.core.commands.speed.SpeedService;
import com.jruk8.jtemplate.core.configs.ConfigRegistrar;
import com.jruk8.jtemplate.core.messages.MessageBootstrap;
import com.jruk8.jtemplate.core.messages.Messenger;
import com.jruk8.jtemplate.core.placeholders.PlaceholdersBootstrap;
import com.jruk8.jtemplate.core.sounds.SoundPlayer;
import com.jruk8.jtemplate.core.storage.StorageBootstrap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class JTemplatePlugin extends JavaPlugin {

    private StorageBootstrap storageBootstrap;

    @Override
    public void onEnable() {
        // Set up all core modules
        setupCoreModules();
    }

    @Override
    public void onDisable() {
        if (this.storageBootstrap != null) {
            this.storageBootstrap.shutdown();
        }
    }

    private void setupCoreModules() {
        var logger = getLogger();

        // Initialize reloader
        Reloader reloader = new Reloader(logger);

        // Initialize all configs
        var configRegistrar = new ConfigRegistrar(this);
        configRegistrar.register();
        reloader.register(configRegistrar);

        // Initialize messenger
        var messageRegistrar = new MessageBootstrap(configRegistrar);
        messageRegistrar.register();
        var messenger = messageRegistrar.getMessenger();

        // Initialize sound player
        var soundPlayer = new SoundPlayer(logger);

        // Initialize commands
        setupCommands(reloader, configRegistrar, messenger, soundPlayer);

        // Initialize storage and placeholders
        setupStorageAndPlaceholders(logger, reloader, configRegistrar);

        // Initialize bStats
        var metricsBootstrap = new MetricsBootstrap(this);
        metricsBootstrap.register();
    }

    private void setupCommands(
            Reloader reloader,
            ConfigRegistrar configRegistrar,
            Messenger messenger,
            SoundPlayer soundPlayer
    ) {
        var context = new CommandContext(
                configRegistrar.getMessagesConfig(),
                configRegistrar.getSoundsConfig(),
                messenger,
                soundPlayer
        );
        var speedExecutor = new SpeedExecutor(context, new SpeedService(configRegistrar.getPluginConfig()));
        var commandRegistrar = new CommandsRegistrar(
                getLifecycleManager(), reloader, context, getPluginMeta(),
                configRegistrar.getPluginConfig(), speedExecutor);
        commandRegistrar.register();
    }

    private void setupStorageAndPlaceholders(Logger logger, Reloader reloader, ConfigRegistrar configRegistrar) {
        // Initialize SQLite storage
        this.storageBootstrap = new StorageBootstrap(configRegistrar.getPluginConfig(), getDataFolder(), logger);
        this.storageBootstrap.register();
        reloader.register(this.storageBootstrap);

        // Initialize PlaceholderAPI placeholders (needs a healthy database)
        if (this.storageBootstrap.getStorage() != null) {
            var placeholdersBootstrap = new PlaceholdersBootstrap(
                    configRegistrar.getPluginConfig(),
                    this.storageBootstrap.getStorage(),
                    this,
                    logger
            );
            placeholdersBootstrap.register();
            reloader.register(placeholdersBootstrap);
        } else {
            logger.severe("SQLite storage is unavailable; PlaceholderAPI placeholders are disabled.");
        }
    }
}