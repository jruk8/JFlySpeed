package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;

/**
 * Fly speed command: {@code /flyspeed [<value>|reset] [targets]}.
 * <p>
 * The same node is registered as the standalone {@code /flyspeed} root (plus
 * its aliases) and as the {@code flyspeed} subcommand of
 * {@code /jflyspeed} (plus its aliases). Running it with no arguments resets
 * the executor's own fly speed. The {@code reset} child comes first so
 * tab-completion lists it before the value defaults.
 */
public class FlySpeedCommand {

    private final CommandContext ctx;
    private final SpeedExecutor exec;
    private final CommandUsageTracker tracker;
    private final List<Double> suggestions;

    public FlySpeedCommand(
            CommandContext ctx,
            SpeedExecutor exec,
            CommandUsageTracker tracker,
            List<Double> suggestions
    ) {
        this.ctx = ctx;
        this.exec = exec;
        this.tracker = tracker;
        this.suggestions = suggestions;
    }

    /**
     * Builds the {@code flyspeed} Brigadier node under any label.
     *
     * @param name the literal label (primary name or alias)
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node(String name) {
        return Commands.literal(name)
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY))
                .executes(context -> SpeedArgumentNodes.resetSelf(
                        SpeedType.FLY, context.getSource(), ctx, exec, tracker))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.FLY, ctx, exec, tracker))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.FLY, ctx, exec, tracker, suggestions));
    }
}
