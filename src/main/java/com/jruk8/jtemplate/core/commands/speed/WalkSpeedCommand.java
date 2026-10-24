package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

/**
 * Walk speed command: {@code /walkspeed <value>|reset [targets]}.
 * <p>
 * The same node is registered as the standalone {@code /walkspeed} root and
 * as the {@code walkspeed} subcommand of {@code /jflyspeed}.
 */
public class WalkSpeedCommand {

    private final CommandContext ctx;
    private final SpeedExecutor exec;

    public WalkSpeedCommand(CommandContext ctx, SpeedExecutor exec) {
        this.ctx = ctx;
        this.exec = exec;
    }

    /**
     * Builds the {@code walkspeed} Brigadier node.
     *
     * @return the configured command node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("walkspeed")
                .requires(CommandContext.requiresPermission(SpeedPermissions.WALK))
                .then(SpeedArgumentNodes.valueArgument(SpeedType.WALK, ctx, exec))
                .then(SpeedArgumentNodes.resetLiteral(SpeedType.WALK, ctx, exec));
    }
}
