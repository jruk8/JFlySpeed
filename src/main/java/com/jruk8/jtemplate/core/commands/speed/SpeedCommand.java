package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;

/**
 * Unified speed command: {@code /speed [<value>|fly|walk|reset] ...}.
 * <p>
 * A bare value sets both channels at once; {@code fly}/{@code walk} pick
 * one channel explicitly. The both-channels branches ({@code <value>} and
 * {@code reset}) require both the fly and walk permissions. Running the
 * command with no arguments resets the executor's own speeds for every
 * permitted channel. The same node is registered as the standalone
 * {@code /speed} root and as the {@code speed} subcommand of
 * {@code /jflyspeed}.
 */
public class SpeedCommand {

    private final CommandContext ctx;
    private final SpeedExecutor exec;
    private final CommandUsageTracker tracker;
    private final List<Double> suggestions;

    public SpeedCommand(
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
     * Builds the {@code speed} Brigadier node.
     *
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("speed")
                .requires(CommandContext.requiresAnyPermission(SpeedPermissions.FLY, SpeedPermissions.WALK))
                .executes(context -> SpeedArgumentNodes.resetPermittedSelf(
                        context.getSource(), ctx, exec, tracker))
                .then(flyBranch())
                .then(walkBranch())
                .then(resetBranch())
                .then(SpeedArgumentNodes.bothValueArgument(ctx, exec, tracker, suggestions));
    }

    private LiteralArgumentBuilder<CommandSourceStack> flyBranch() {
        return Commands.literal("fly")
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY))
                .executes(context -> SpeedArgumentNodes.resetSelf(
                        SpeedType.FLY, context.getSource(), ctx, exec, tracker))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.FLY, ctx, exec, tracker))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.FLY, ctx, exec, tracker, suggestions));
    }

    private LiteralArgumentBuilder<CommandSourceStack> walkBranch() {
        return Commands.literal("walk")
                .requires(CommandContext.requiresPermission(SpeedPermissions.WALK))
                .executes(context -> SpeedArgumentNodes.resetSelf(
                        SpeedType.WALK, context.getSource(), ctx, exec, tracker))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.WALK, ctx, exec, tracker))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.WALK, ctx, exec, tracker, suggestions));
    }

    private LiteralArgumentBuilder<CommandSourceStack> resetBranch() {
        return SpeedArgumentNodes.resetBothLiteral(ctx, exec, tracker)
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY, SpeedPermissions.WALK));
    }
}
