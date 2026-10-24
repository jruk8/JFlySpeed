package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.admin.AdminCommandsRegistrar;
import com.jruk8.jtemplate.core.commands.user.UserCommandsRegistrar;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.paper.util.sender.Source;

/**
 * Registers all plugin commands.
 */
public class CommandsRegistrar implements Bootstrap {

    private final CommandManager<Source> commandManager;
    private final AnnotationParser<Source> annotationParser;
    private final Reloader reloader;
    private final CommandContext commandContext;
    private final PluginMeta pluginMeta;

    public CommandsRegistrar(CommandManager<Source> commandManager,
                             AnnotationParser<Source> annotationParser,
                             Reloader reloader,
                             CommandContext commandContext,
                             PluginMeta pluginMeta) {
        this.commandManager = commandManager;
        this.annotationParser = annotationParser;
        this.reloader = reloader;
        this.commandContext = commandContext;
        this.pluginMeta = pluginMeta;
    }

    @Override
    public void register() {
        var userCommands =
                new UserCommandsRegistrar(annotationParser, commandManager, commandContext, pluginMeta);
        userCommands.register();

        var adminCommands =
                new AdminCommandsRegistrar(annotationParser, commandContext, reloader);
        adminCommands.register();
    }
}