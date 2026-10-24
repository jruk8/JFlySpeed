package com.jruk8.jtemplate.core.commands.user.help;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.List;
import java.util.Map;

/**
 * Help subcommand that lists the plugin's commands, optionally filtered by a search query.
 * <p>
 * Senders with {@code jflyspeed.help} may run it (everyone by default);
 * entries are filtered by permission. Entries use the main {@code /jfs}
 * alias and never duplicate the standalone roots. The bare
 * {@code /jflyspeed} root shows this same listing.
 */
public class HelpCommand {

    private final CommandContext ctx;
    private final String version;

    public HelpCommand(CommandContext ctx, String version) {
        this.ctx = ctx;
        this.version = version;
    }

    /**
     * Builds the {@code help} Brigadier node, attached to the {@code jflyspeed} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("help")
                .requires(CommandContext.requiresPermission(SpeedPermissions.HELP))
                .executes(context -> {
                    showHelp(context.getSource(), null);
                    return Command.SINGLE_SUCCESS;
                })
                .then(Commands.argument("query", StringArgumentType.greedyString())
                        .executes(context -> {
                            showHelp(context.getSource(), StringArgumentType.getString(context, "query"));
                            return Command.SINGLE_SUCCESS;
                        }));
    }

    /**
     * Shows the help listing, optionally filtered by a search query.
     *
     * @param sender the sender to show the listing to
     * @param query the search query, may be null or blank
     */
    public void showHelp(CommandSourceStack sender, String query) {
        ctx.playHelp(sender);
        ctx.sendMessage(sender, m -> m.getCore().getHelpHeader(), Map.of("version", version));
        HelpEntry.matching(entries(), query).stream()
                .filter(entry -> entry.visibleTo(sender.getSender()))
                .forEach(entry -> ctx.sendMessage(
                        sender,
                        m -> m.getCore().getHelpEntryFormat(),
                        Map.of("syntax", entry.syntax(), "description", entry.description())
                ));
    }

    private List<HelpEntry> entries() {
        return List.of(
                new HelpEntry("/jfs help [query]", "Shows this help menu."),
                new HelpEntry("/jfs version", "Shows the plugin version.", SpeedPermissions.VERSION),
                new HelpEntry("/jfs flyspeed [<speed>|reset] [targets]",
                        "Sets fly speed (0-10, empty resets).", SpeedPermissions.FLY),
                new HelpEntry("/jfs walkspeed [<speed>|reset] [targets]",
                        "Sets walk speed (0-10, empty resets).", SpeedPermissions.WALK),
                new HelpEntry("/jfs speed <speed> [targets]",
                        "Sets speed for your current mode (0-10).",
                        SpeedPermissions.FLY, SpeedPermissions.WALK),
                new HelpEntry("/jfs speed fly|walk [<speed>|reset] [targets]",
                        "Sets one speed (0-10, empty resets).",
                        SpeedPermissions.FLY, SpeedPermissions.WALK),
                new HelpEntry("/jfs speed reset [targets]",
                        "Resets both speeds (needs both permissions).",
                        SpeedPermissions.FLY, SpeedPermissions.WALK),
                new HelpEntry("/jfs fly [on|off] [targets]",
                        "Toggles fly mode (empty toggles).", SpeedPermissions.FLY_MODE),
                new HelpEntry("/jfs reload", "Reloads the plugin configuration.", SpeedPermissions.RELOAD));
    }
}
