package com.jruk8.jtemplate.core.commands.user;

import com.jruk8.jtemplate.core.Bootstrap;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.user.authors.AuthorFormatter;
import com.jruk8.jtemplate.core.commands.user.authors.AuthorsCommand;
import com.jruk8.jtemplate.core.commands.user.help.HelpCommand;
import io.papermc.paper.plugin.configuration.PluginMeta;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;
import org.incendo.cloud.paper.util.sender.Source;

/**
 * Registers all plugin commands.
 */
public class UserCommandsRegistrar implements Bootstrap {

    private final AnnotationParser<Source> annotationParser;
    private final CommandManager<Source> manager;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;

    public UserCommandsRegistrar(AnnotationParser<Source> annotationParser,
                                 CommandManager<Source> commandManager,
                                 CommandContext commandContext,
                                 PluginMeta pluginMeta) {
        this.annotationParser = annotationParser;
        this.manager = commandManager;
        this.ctx = commandContext;
        this.pluginMeta = pluginMeta;
    }

    @Override
    public void register() {
        var mcHelp = MinecraftHelp.<Source>builder()
                .commandManager(manager)
                .audienceProvider(Source::source)      // Source -> Audience
                .commandPrefix("/jtemplate")
                .build();
        annotationParser.parse(new HelpCommand(ctx, manager, mcHelp));
        annotationParser.parse(new AuthorsCommand(new AuthorFormatter(), ctx, pluginMeta));
    }
}