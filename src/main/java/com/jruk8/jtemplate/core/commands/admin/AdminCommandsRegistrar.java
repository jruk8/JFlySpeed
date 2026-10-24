package com.jruk8.jtemplate.core.commands.admin;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.admin.reload.ReloadCommand;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;

/**
 * Registers all admin subcommands on the plugin's root command node.
 */
public class AdminCommandsRegistrar implements Bootstrap {

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final Reloader reloader;

    public AdminCommandsRegistrar(LiteralArgumentBuilder<CommandSourceStack> root,
                                  CommandContext ctx,
                                  Reloader reloader) {
        this.root = root;
        this.ctx = ctx;
        this.reloader = reloader;
    }

    @Override
    public void register() {
        root.then(new ReloadCommand(reloader, ctx).node());
    }
}
