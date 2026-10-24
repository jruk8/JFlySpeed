package com.jruk8.jtemplate.core.update;

import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.jruk8.jtemplate.core.messages.Messenger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Sends update notices to players holding {@code jflyspeed.reload}.
 */
public final class UpdateCheckNotifier {

    private final Messenger messenger;
    private final MessagesConfig messages;

    public UpdateCheckNotifier(Messenger messenger, MessagesConfig messages) {
        this.messenger = messenger;
        this.messages = messages;
    }

    /**
     * Sends an update notice to every permitted online player.
     *
     * @param update the found update
     */
    public void notifyAdmins(UpdateAvailable update) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            notifyPlayer(player, update);
        }
    }

    /**
     * Sends an update notice to one player when permitted.
     *
     * @param player the player to notify
     * @param update the found update
     */
    public void notifyPlayer(Player player, UpdateAvailable update) {
        if (player == null || update == null || !player.hasPermission(SpeedPermissions.RELOAD)) {
            return;
        }
        messenger.send(
                player,
                messages.getCore().getUpdateAvailable(),
                Placeholder.parsed("latest", update.latest()),
                Placeholder.parsed("current", update.current()),
                Placeholder.parsed("url", update.url()),
                Placeholder.parsed("modrinth-url", update.modrinthUrl())
        );
    }
}
