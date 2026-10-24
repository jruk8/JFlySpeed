package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;

/**
 * Walk speed command: {@code /walkspeed [<value>|reset] [targets]}.
 * <p>
 * The same node is registered as the standalone {@code /walkspeed} root (plus
 * its aliases) and as the {@code walkspeed} subcommand of
 * {@code /jflyspeed} (plus its aliases). Running it with no arguments resets
 * the executor's own walk speed. The {@code reset} child comes first so
 * tab-completion lists it before the value defaults.
 */
public class WalkSpeedCommand {

    private final CommandContext ctx;
    private final SpeedExecutor exec;
    private final CommandUsageTracker tracker;
    private final List<Double> suggestions;

    public WalkSpeedCommand(
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
     * Builds the {@code walkspeed} Brigadier node under any label.
     *
     * @param name the literal label (primary name or alias)
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node(String name) {
        return Commands.literal(name)
                .requires(CommandContext.requiresPermission(SpeedPermissions.WALK))
                .executes(context -> SpeedArgumentNodes.resetSelf(
                        SpeedType.WALK, context.getSource(), ctx, exec, tracker))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.WALK, ctx, exec, tracker))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.WALK, ctx, exec, tracker, suggestions));
    }
}
