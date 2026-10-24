package com.jruk8.jtemplate.core.commands.user.version;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.List;

/**
 * Version subcommand that displays the plugin version and authors.
 */
public class VersionCommand {

    private final AuthorFormatter authorFormatter;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;

    public VersionCommand(AuthorFormatter authorFormatter, CommandContext ctx, PluginMeta pluginMeta) {
        this.authorFormatter = authorFormatter;
        this.ctx = ctx;
        this.pluginMeta = pluginMeta;
    }

    /**
     * Builds the {@code version} Brigadier node, attached to the {@code jflyspeed} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("version")
                .requires(CommandContext.requiresPermission(SpeedPermissions.VERSION))
                .executes(context -> {
                    onVersion(context.getSource());
                    return Command.SINGLE_SUCCESS;
                });
    }

    public void onVersion(CommandSourceStack sender) {
        String version = pluginMeta.getVersion();
        List<String> rawAuthors = pluginMeta.getAuthors();
        String authors = authorFormatter.formatAuthors(rawAuthors);

        ctx.sendVersion(
                sender,
                m -> m.getCore().getVersionSuccess(),
                Placeholder.parsed("version", version),
                Placeholder.parsed("authors", authors)
        );
    }
}
