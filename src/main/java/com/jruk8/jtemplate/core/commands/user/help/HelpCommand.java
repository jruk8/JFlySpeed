package com.jruk8.jtemplate.core.commands.user.help;

import com.jruk8.jtemplate.core.commands.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.CommandManager;
import org.incendo.cloud.annotation.specifier.Greedy;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;
import org.incendo.cloud.minecraft.extras.MinecraftHelp;

@Command("jtemplate|jt")
@Permission("jtemplate.user")
public class HelpCommand {

    private final CommandContext ctx;
    private final CommandManager<Source> manager;
    private final MinecraftHelp<Source> help;

    public HelpCommand(CommandContext ctx, CommandManager<Source> manager, MinecraftHelp<Source> help) {
        this.ctx = ctx;
        this.manager = manager;
        this.help = help;
    }

    @Command("help [query]")
    @CommandDescription("Shows this help menu.")
    @Permission("jtemplate.user.help")
    public void onHelp(
            Source sender,
            @Argument(value = "query", description = "Search query")
            @Greedy
            String query
    ) {
        ctx.playSuccess(sender);
        if (query == null || query.isBlank()) {

            listAllReachableCommands(sender);
            return;
        }

        help.queryCommands(query, sender);
    }

    private void listAllReachableCommands(Source sender) {
        ctx.sendMessage(sender, m -> m.getCore().getHelpHeader());
        manager.createHelpHandler()
                .queryRootIndex(sender)
                .entries()
                .forEach(entry -> {
                    String description = entry.command().commandDescription().description().textDescription();
                    ctx.sendMessage(
                            sender,
                            m -> m.getCore().getHelpEntryFormat(),
                            Placeholder.parsed("syntax", "/" + entry.syntax()),
                            Placeholder.parsed("description", description)
                    );
                });
    }
}
