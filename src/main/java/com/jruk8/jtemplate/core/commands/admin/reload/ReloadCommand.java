package com.jruk8.jtemplate.core.commands.admin.reload;

import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;

/**
 * Reload subcommand that re-reads every config and re-opens storage and placeholders.
 */
public class ReloadCommand {

    private final Reloader reloader;
    private final CommandContext ctx;

    public ReloadCommand(Reloader reloader, CommandContext ctx) {
        this.reloader = reloader;
        this.ctx = ctx;
    }

    /**
     * Builds the {@code reload} Brigadier node, attached to the {@code jflyspeed} root by the registrar.
     *
     * @return the configured subcommand node
     */
    public LiteralArgumentBuilder<CommandSourceStack> node() {
        return Commands.literal("reload")
                .requires(CommandContext.requiresPermission(SpeedPermissions.RELOAD))
                .executes(context -> {
                    onReload(context.getSource());
                    return Command.SINGLE_SUCCESS;
                });
    }

    public void onReload(CommandSourceStack sender) {
        long start = System.currentTimeMillis();
        reloader.reloadAll();
        long time = System.currentTimeMillis() - start;
        ctx.success(
                sender,
                m -> m.getCore().getReloadSuccess(),
                Placeholder.parsed("time", String.valueOf(time))
        );
    }
}
