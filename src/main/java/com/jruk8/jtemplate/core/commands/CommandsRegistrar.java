package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.admin.AdminCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.speed.FlySpeedCommand;
import com.jruk8.jtemplate.core.commands.speed.SpeedCommand;
import com.jruk8.jtemplate.core.commands.speed.SpeedCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.speed.SpeedExecutor;
import com.jruk8.jtemplate.core.commands.speed.WalkSpeedCommand;
import com.jruk8.jtemplate.core.commands.user.UserCommandsRegistrar;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

import java.util.List;

/**
 * Registers all plugin commands through Paper's native Brigadier support.
 * <p>
 * The {@code jflyspeed} root (alias {@code jf}) always carries every
 * subcommand; the standalone {@code flyspeed}, {@code walkspeed} and
 * {@code speed} roots are registered only when their config toggle is on.
 */
public class CommandsRegistrar implements Bootstrap {

    private final LifecycleEventManager<Plugin> lifecycle;
    private final Reloader reloader;
    private final CommandContext commandContext;
    private final PluginMeta pluginMeta;
    private final PluginConfig pluginConfig;
    private final SpeedExecutor speedExecutor;

    public CommandsRegistrar(LifecycleEventManager<Plugin> lifecycle,
                             Reloader reloader,
                             CommandContext commandContext,
                             PluginMeta pluginMeta,
                             PluginConfig pluginConfig,
                             SpeedExecutor speedExecutor) {
        this.lifecycle = lifecycle;
        this.reloader = reloader;
        this.commandContext = commandContext;
        this.pluginMeta = pluginMeta;
        this.pluginConfig = pluginConfig;
        this.speedExecutor = speedExecutor;
    }

    @Override
    public void register() {
        lifecycle.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var root = Commands.literal("jflyspeed");
            var commands = pluginConfig.getCommands();
            new UserCommandsRegistrar(root, commandContext, pluginMeta, commands).register();
            new AdminCommandsRegistrar(root, commandContext, reloader).register();
            new SpeedCommandsRegistrar(root, commandContext, speedExecutor).register();
            event.registrar().register(root.build(), "JFlySpeed commands", List.of("jf"));
            registerStandaloneRoots(event.registrar(), commands);
        });
    }

    private void registerStandaloneRoots(Commands registrar, PluginConfig.Commands commands) {
        if (commands.isFlyspeed()) {
            registrar.register(new FlySpeedCommand(commandContext, speedExecutor).node().build());
        }
        if (commands.isWalkspeed()) {
            registrar.register(new WalkSpeedCommand(commandContext, speedExecutor).node().build());
        }
        if (commands.isSpeed()) {
            registrar.register(new SpeedCommand(commandContext, speedExecutor).node().build());
        }
    }
}
