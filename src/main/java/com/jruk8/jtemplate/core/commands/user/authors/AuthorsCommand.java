package com.jruk8.jtemplate.core.commands.user.authors;

import com.jruk8.jtemplate.core.commands.CommandContext;
import io.papermc.paper.plugin.configuration.PluginMeta;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;

import java.util.List;

/**
 * Authors subcommand that displays the plugin version and authors.
 */
@Command("jtemplate|jt")
@Permission("jtemplate.user")
public class AuthorsCommand {

    private final AuthorFormatter authorFormatter;
    private final CommandContext ctx;
    private final PluginMeta pluginMeta;

    public AuthorsCommand(AuthorFormatter authorFormatter, CommandContext ctx, PluginMeta pluginMeta) {
        this.authorFormatter = authorFormatter;
        this.ctx = ctx;
        this.pluginMeta = pluginMeta;
    }

    @Command("authors")
    @CommandDescription("Shows the authors of the plugin.")
    @Permission("jtemplate.user.authors")
    public void onAuthors(Source sender) {
        String version = pluginMeta.getVersion();
        List<String> rawAuthors = pluginMeta.getAuthors();
        String authors = authorFormatter.formatAuthors(rawAuthors);

        ctx.success(
                sender,
                m -> m.getCore().getAuthorSuccess(),
                Placeholder.parsed("version", version),
                Placeholder.parsed("authors", authors)
        );
    }
}