package com.jruk8.jtemplate.core.commands.admin.reload;

import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.configs.PluginConfig;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

import java.util.Map;

/**
 * Reload subcommand that re-reads every config and re-opens storage and placeholders.
 * <p>
 * Paper registers Brigadier commands once during enable and offers no
 * unregister API, so changed command toggles still need a restart; the
 * command says so instead of pretending the toggles applied.
 */
public class ReloadCommand {

    private final Reloader reloader;
    private final CommandContext ctx;
    private final PluginConfig pluginConfig;

    public ReloadCommand(Reloader reloader, CommandContext ctx, PluginConfig pluginConfig) {
        this.reloader = reloader;
        this.ctx = ctx;
        this.pluginConfig = pluginConfig;
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
        boolean flyspeed = toggles().getFlyspeed().isEnabled();
        boolean walkspeed = toggles().getWalkspeed().isEnabled();
        boolean speed = toggles().getSpeed().isEnabled();
        boolean fly = toggles().getFly().isEnabled();
        long start = System.currentTimeMillis();
        boolean ok = reloader.reloadAll();
        long time = System.currentTimeMillis() - start;
        if (ok) {
            ctx.sendReload(
                    sender,
                    m -> m.getCore().getReloadSuccess(),
                    Map.of("time", String.valueOf(time))
            );
        } else {
            ctx.error(
                    sender,
                    m -> m.getCore().getReloadFailure(),
                    Map.of("time", String.valueOf(time))
            );
        }
        if (togglesChanged(flyspeed, walkspeed, speed, fly)) {
            ctx.sendMessage(sender, m -> m.getCore().getReloadRestartRequired(), Map.of());
        }
    }

    private PluginConfig.Commands toggles() {
        return pluginConfig.getCommands();
    }

    private boolean togglesChanged(boolean flyspeed, boolean walkspeed, boolean speed, boolean fly) {
        return toggles().getFlyspeed().isEnabled() != flyspeed
                || toggles().getWalkspeed().isEnabled() != walkspeed
                || toggles().getSpeed().isEnabled() != speed
                || toggles().getFly().isEnabled() != fly;
    }
}
