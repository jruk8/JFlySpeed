package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * Fly mode command: {@code /fly [[on|off] [targets]]}.
 * <p>
 * A bare run toggles the executor's own fly mode; {@code on}/{@code off}
 * set it explicitly, optionally for other targets. A bare selector
 * toggles the whole group (off when everyone can already fly, otherwise
 * on). The same node is registered as the standalone {@code /fly} root
 * (plus its aliases) and as the {@code fly} subcommand of
 * {@code /jflyspeed} (plus its aliases).
 */
public class FlyCommand {

    private final CommandContext ctx;
    private final FlyExecutor exec;

    public FlyCommand(CommandContext ctx, FlyExecutor exec) {
        this.ctx = ctx;
        this.exec = exec;
    }

    /**
     * Builds the {@code fly} Brigadier node under any label.
     *
     * @param name the literal label (primary name or alias)
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node(String name) {
        return Commands.literal(name)
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY_MODE))
                .executes(context -> TargetSelector.self(
                                context.getSource(), ctx, m -> m.getFly().getConsoleNeedsTarget())
                        .map(list -> exec.toggleFly(context.getSource(), list))
                        .orElse(0))
                .then(modeLiteral(true))
                .then(modeLiteral(false))
                .then(SpeedArgumentNodes.targetsArgument(
                        (source, targets) -> exec.toggleFly(source, targets), ctx));
    }

    private LiteralArgumentBuilder<CommandSourceStack> modeLiteral(boolean enabled) {
        return Commands.literal(enabled ? "on" : "off")
                .executes(context -> TargetSelector.self(
                                context.getSource(), ctx, m -> m.getFly().getConsoleNeedsTarget())
                        .map(list -> exec.setFly(context.getSource(), enabled, list))
                        .orElse(0))
                .then(SpeedArgumentNodes.targetsArgument(
                        (source, targets) -> exec.setFly(source, enabled, targets), ctx));
    }
}
