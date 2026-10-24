package com.jruk8.jtemplate.core.commands.user.version;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.update.UpdateAvailable;
import com.jruk8.jtemplate.core.update.UpdateCheckService;
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
    private final UpdateCheckService updateChecks;

    public VersionCommand(
            AuthorFormatter authorFormatter,
            CommandContext ctx,
            PluginMeta pluginMeta,
            UpdateCheckService updateChecks
    ) {
        this.authorFormatter = authorFormatter;
        this.ctx = ctx;
        this.pluginMeta = pluginMeta;
        this.updateChecks = updateChecks;
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
        if (sender.getSender().hasPermission(SpeedPermissions.RELOAD)) {
            updateChecks.pendingUpdate().ifPresent(update -> sendUpdateNotice(sender, update));
        }
    }

    private void sendUpdateNotice(CommandSourceStack sender, UpdateAvailable update) {
        ctx.sendMessage(
                sender,
                m -> m.getCore().getUpdateAvailable(),
                Placeholder.parsed("latest", update.latest()),
                Placeholder.parsed("current", update.current()),
                Placeholder.parsed("url", update.url()),
                Placeholder.parsed("modrinth-url", update.modrinthUrl())
        );
    }
}
