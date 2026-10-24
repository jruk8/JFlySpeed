package com.jruk8.jtemplate.core.commands.user;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.user.help.HelpCommand;
import com.jruk8.jtemplate.core.commands.user.version.AuthorFormatter;
import com.jruk8.jtemplate.core.commands.user.version.VersionCommand;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.plugin.configuration.PluginMeta;

/**
 * Registers all user-facing subcommands on the plugin's root command node.
 */
public class UserCommandsRegistrar implements Bootstrap {

    private final LiteralArgumentBuilder<CommandSourceStack> root;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;
    private final PluginConfig.Commands commands;

    public UserCommandsRegistrar(LiteralArgumentBuilder<CommandSourceStack> root,
                                 CommandContext commandContext,
                                 PluginMeta pluginMeta,
                                 PluginConfig.Commands commands) {
        this.root = root;
        this.ctx = commandContext;
        this.pluginMeta = pluginMeta;
        this.commands = commands;
    }

    @Override
    public void register() {
        root.then(new HelpCommand(
                ctx,
                commands.isFlyspeed(),
                commands.isWalkspeed(),
                commands.isSpeed()
        ).node());
        root.then(new VersionCommand(new AuthorFormatter(), ctx, pluginMeta).node());
    }
}
