package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Registers the speed subcommands on the plugin's root command node,
 * including the channel aliases (always available under the root).
 */
public class SpeedCommandsRegistrar implements Bootstrap {

    private static final Set<String> HUB_COMMANDS = Set.of(
            "flyspeed", "walkspeed", "speed", "reload", "help", "version");

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final SpeedExecutor exec;
    private final CommandUsageTracker tracker;
    private final PluginConfig.Commands commands;
    private final List<Double> suggestions;

    public SpeedCommandsRegistrar(
            LiteralArgumentBuilder<CommandSourceStack> root,
            CommandContext commandContext,
            SpeedExecutor speedExecutor,
            CommandUsageTracker tracker,
            PluginConfig.Commands commands,
            List<Double> suggestions
    ) {
        this.root = root;
        this.ctx = commandContext;
        this.exec = speedExecutor;
        this.tracker = tracker;
        this.commands = commands;
        this.suggestions = suggestions;
    }

    @Override
    public void register() {
        var used = new HashSet<>(HUB_COMMANDS);
        var fly = new FlySpeedCommand(ctx, exec, tracker, suggestions);
        root.then(fly.node("flyspeed"));
        for (String alias : hubAliases(commands.getFlyspeed().getAliases(), used)) {
            root.then(fly.node(alias));
        }
        var walk = new WalkSpeedCommand(ctx, exec, tracker, suggestions);
        root.then(walk.node("walkspeed"));
        for (String alias : hubAliases(commands.getWalkspeed().getAliases(), used)) {
            root.then(walk.node(alias));
        }
        root.then(new SpeedCommand(ctx, exec, tracker, suggestions).node());
    }

    private List<String> hubAliases(List<String> configured, Set<String> used) {
        List<String> accepted = new ArrayList<>();
        for (String raw : configured) {
            String alias = raw.trim().toLowerCase(Locale.ROOT);
            if (!alias.isEmpty() && used.add(alias)) {
                accepted.add(alias);
            }
        }
        return accepted;
    }
}
