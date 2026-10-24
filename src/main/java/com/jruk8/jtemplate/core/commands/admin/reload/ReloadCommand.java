package com.jruk8.jtemplate.core.commands.admin.reload;

import com.jruk8.jtemplate.core.Reloader;
import com.jruk8.jtemplate.core.commands.CommandContext;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.CommandDescription;
import org.incendo.cloud.annotations.Permission;
import org.incendo.cloud.paper.util.sender.Source;

@Command("jtemplate|jt")
@Permission("jtemplate.admin")
public class ReloadCommand {

    private final Reloader reloader;
    private final CommandContext ctx;

    public ReloadCommand(Reloader reloader, CommandContext ctx) {
        this.reloader = reloader;
        this.ctx = ctx;
    }

    @Command("reload")
    @Permission("jtemplate.admin.reload")
    @CommandDescription("Reloads the plugin configuration.")
    public void onReload(Source sender) {
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
