package com.jruk8.jtemplate.core.commands.user.help;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

import java.util.ArrayList;
import java.util.List;

/**
 * Help subcommand that lists the plugin's commands, optionally filtered by a search query.
 * <p>
 * Every sender may run it; entries are filtered by permission. The standalone
 * root entries mirror the toggles captured at registration time.
 */
public class HelpCommand {

    private final CommandContext ctx;
    private final boolean flyspeedRoot;
    private final boolean walkspeedRoot;
    private final boolean speedRoot;

    public HelpCommand(CommandContext ctx, boolean flyspeedRoot, boolean walkspeedRoot, boolean speedRoot) {
        this.ctx = ctx;
        this.flyspeedRoot = flyspeedRoot;
        this.walkspeedRoot = walkspeedRoot;
        this.speedRoot = speedRoot;
    }

    /**
     * Builds the {@code help} Brigadier node, attached to the {@code jflyspeed} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("help")
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

    private void showHelp(CommandSourceStack sender, String query) {
        ctx.playHelp(sender);
        ctx.sendMessage(sender, m -> m.getCore().getHelpHeader());
        HelpEntry.matching(entries(), query).stream()
                .filter(entry -> entry.visibleTo(sender.getSender()))
                .forEach(entry -> ctx.sendMessage(
                        sender,
                        m -> m.getCore().getHelpEntryFormat(),
                        Placeholder.parsed("syntax", entry.syntax()),
                        Placeholder.parsed("description", entry.description())
                ));
    }

    private List<HelpEntry> entries() {
        List<HelpEntry> entries = new ArrayList<>(List.of(
                new HelpEntry("/jflyspeed help [query]", "Shows this help menu."),
                new HelpEntry("/jflyspeed version", "Shows the plugin version.", SpeedPermissions.VERSION),
                new HelpEntry("/jflyspeed flyspeed <speed|reset> [targets]", "Sets fly speed (0-10, empty resets).",
                        SpeedPermissions.FLY),
                new HelpEntry("/jflyspeed walkspeed <speed|reset> [targets]", "Sets walk speed (0-10, empty resets).",
                        SpeedPermissions.WALK),
                new HelpEntry("/jflyspeed speed fly|walk <speed|reset> [targets]",
                        "Sets one speed (0-10, empty resets).",
                        SpeedPermissions.FLY, SpeedPermissions.WALK),
                new HelpEntry("/jflyspeed speed <speed> [targets]", "Sets speed for your current mode (0-10).",
                        SpeedPermissions.FLY, SpeedPermissions.WALK),
                new HelpEntry("/jflyspeed speed reset [targets]", "Resets both speeds (needs both permissions).",
                        SpeedPermissions.FLY, SpeedPermissions.WALK),
                new HelpEntry("/jflyspeed reload", "Reloads the plugin configuration.", SpeedPermissions.RELOAD)
        ));
        if (flyspeedRoot) {
            entries.add(new HelpEntry("/flyspeed <speed|reset> [targets]", "Sets fly speed (0-10, empty resets).",
                    SpeedPermissions.FLY));
        }
        if (walkspeedRoot) {
            entries.add(new HelpEntry("/walkspeed <speed|reset> [targets]", "Sets walk speed (0-10, empty resets).",
                    SpeedPermissions.WALK));
        }
        if (speedRoot) {
            entries.add(new HelpEntry("/speed <speed> [targets]", "Sets speed for your current mode (0-10).",
                    SpeedPermissions.FLY, SpeedPermissions.WALK));
            entries.add(new HelpEntry("/speed fly|walk|reset ...", "Sets one speed, or resets both (empty resets own).",
                    SpeedPermissions.FLY, SpeedPermissions.WALK));
        }
        return entries;
    }
}
