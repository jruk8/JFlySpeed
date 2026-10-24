package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

/**
 * Registers the speed subcommands on the plugin's root command node.
 */
public class SpeedCommandsRegistrar implements Bootstrap {

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final SpeedExecutor exec;

    public SpeedCommandsRegistrar(LiteralArgumentBuilder<CommandSourceStack> root,
                                  CommandContext commandContext,
                                  SpeedExecutor speedExecutor) {
        this.root = root;
        this.ctx = commandContext;
        this.exec = speedExecutor;
    }

    @Override
    public void register() {
        root.then(new FlySpeedCommand(ctx, exec).node());
        root.then(new WalkSpeedCommand(ctx, exec).node());
        root.then(new SpeedCommand(ctx, exec).node());
    }
}
