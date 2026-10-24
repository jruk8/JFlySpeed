package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * Unified speed command: {@code /speed fly|walk <value>|reset [targets]} and
 * {@code /speed reset [targets]}.
 * <p>
 * The {@code reset} branch resets both channels at once, so it requires both
 * the fly and walk permissions. The same node is registered as the standalone
 * {@code /speed} root and as the {@code speed} subcommand of {@code /jflyspeed}.
 */
public class SpeedCommand {

    private final CommandContext ctx;
    private final SpeedExecutor exec;

    public SpeedCommand(CommandContext ctx, SpeedExecutor exec) {
        this.ctx = ctx;
        this.exec = exec;
    }

    /**
     * Builds the {@code speed} Brigadier node.
     *
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("speed")
                .requires(CommandContext.requiresAnyPermission(SpeedPermissions.FLY, SpeedPermissions.WALK))
                .then(flyBranch())
                .then(walkBranch())
                .then(resetBranch());
    }

    private LiteralArgumentBuilder<CommandSourceStack> flyBranch() {
        return Commands.literal("fly")
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.FLY, ctx, exec))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.FLY, ctx, exec));
    }

    private LiteralArgumentBuilder<CommandSourceStack> walkBranch() {
        return Commands.literal("walk")
                .requires(CommandContext.requiresPermission(SpeedPermissions.WALK))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.WALK, ctx, exec))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.WALK, ctx, exec));
    }

    private LiteralArgumentBuilder<CommandSourceStack> resetBranch() {
        return SpeedArgumentNodes.resetBothLiteral(ctx, exec)
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY, SpeedPermissions.WALK));
    }
}
