package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * Fly speed command: {@code /flyspeed <value>|reset [targets]}.
 * <p>
 * The same node is registered as the standalone {@code /flyspeed} root and as
 * the {@code flyspeed} subcommand of {@code /jflyspeed}.
 */
public class FlySpeedCommand {

    private final CommandContext ctx;
    private final SpeedExecutor exec;

    public FlySpeedCommand(CommandContext ctx, SpeedExecutor exec) {
        this.ctx = ctx;
        this.exec = exec;
    }

    /**
     * Builds the {@code flyspeed} Brigadier node.
     *
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("flyspeed")
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.FLY, ctx, exec))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.FLY, ctx, exec));
    }
}
