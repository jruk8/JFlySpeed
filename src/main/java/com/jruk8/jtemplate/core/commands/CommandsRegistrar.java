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
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.jruk8.jtemplate.core.update.UpdateCheckService;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Registers all plugin commands through Paper's native Brigadier support.
 * <p>
 * The {@code jflyspeed} root (alias {@code jf}) always carries every
 * subcommand; the standalone {@code flyspeed}, {@code walkspeed} and
 * {@code speed} roots are registered only when their config toggle is on,
 * each with its configured aliases.
 */
public class CommandsRegistrar implements Bootstrap {

    private static final Set<String> RESERVED_ROOTS = Set.of(
            "jflyspeed", "flyspeed", "walkspeed", "speed");

    private final LifecycleEventManager<Plugin> lifecycle;
    private final Reloader reloader;
    private final CommandContext commandContext;
    private final PluginMeta pluginMeta;
    private final PluginConfig pluginConfig;
    private final SpeedExecutor speedExecutor;
    private final UpdateCheckService updateChecks;
    private final CommandUsageTracker usageTracker;

    public CommandsRegistrar(LifecycleEventManager<Plugin> lifecycle,
                             Reloader reloader,
                             CommandContext commandContext,
                             PluginMeta pluginMeta,
                             PluginConfig pluginConfig,
                             SpeedExecutor speedExecutor,
                             UpdateCheckService updateChecks,
                             CommandUsageTracker usageTracker) {
        this.lifecycle = lifecycle;
        this.reloader = reloader;
        this.commandContext = commandContext;
        this.pluginMeta = pluginMeta;
        this.pluginConfig = pluginConfig;
        this.speedExecutor = speedExecutor;
        this.updateChecks = updateChecks;
        this.usageTracker = usageTracker;
    }

    @Override
    public void register() {
        lifecycle.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            var root = Commands.literal("jflyspeed");
            var commands = pluginConfig.getCommands();
            var hubAliases = rootAliases();
            var suggestions = pluginConfig.getSpeed().getValueSuggestions();
            new UserCommandsRegistrar(root, commandContext, pluginMeta, commands, updateChecks).register();
            new AdminCommandsRegistrar(root, commandContext, reloader, pluginConfig).register();
            new SpeedCommandsRegistrar(
                    root, commandContext, speedExecutor, usageTracker, commands, suggestions).register();
            event.registrar().register(root.build(), "JFlySpeed commands", hubAliases);
            registerStandaloneRoots(event.registrar(), commands, suggestions, hubAliases);
        });
    }

    private List<String> rootAliases() {
        return pluginConfig.getAliases().stream()
                .map(String::trim)
                .map(alias -> alias.toLowerCase(Locale.ROOT))
                .filter(alias -> !alias.isEmpty() && !"jflyspeed".equals(alias))
                .distinct()
                .toList();
    }

    private List<String> channelAliases(List<String> configured, List<String> hubAliases) {
        var taken = new HashSet<>(hubAliases);
        taken.addAll(RESERVED_ROOTS);
        return configured.stream()
                .map(String::trim)
                .map(alias -> alias.toLowerCase(Locale.ROOT))
                .filter(alias -> !alias.isEmpty() && !taken.contains(alias))
                .distinct()
                .toList();
    }

    private void registerStandaloneRoots(
            Commands registrar,
            PluginConfig.Commands commands,
            List<Double> suggestions,
            List<String> hubAliases
    ) {
        if (commands.getFlyspeed().isEnabled()) {
            registrar.register(
                    new FlySpeedCommand(commandContext, speedExecutor, usageTracker, suggestions)
                            .node("flyspeed").build(),
                    "JFlySpeed flyspeed command",
                    channelAliases(commands.getFlyspeed().getAliases(), hubAliases));
        }
        if (commands.getWalkspeed().isEnabled()) {
            registrar.register(
                    new WalkSpeedCommand(commandContext, speedExecutor, usageTracker, suggestions)
                            .node("walkspeed").build(),
                    "JFlySpeed walkspeed command",
                    channelAliases(commands.getWalkspeed().getAliases(), hubAliases));
        }
        if (commands.getSpeed().isEnabled()) {
            registrar.register(
                    new SpeedCommand(commandContext, speedExecutor, usageTracker, suggestions)
                            .node().build(),
                    "JFlySpeed speed command",
                    List.of());
        }
    }
}
