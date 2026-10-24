package com.jruk8.jtemplate.core.placeholders;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.List;
import java.util.Locale;

/**
 * Listens for executions of the plugin's command and feeds them to the
 * {@link CommandUsageTracker}.
 * <p>
 * Command matching is intentionally data-driven: the base command labels and
 * the tracked subcommand come from {@code PluginConfig.Placeholders}.
 */
public final class AuthorsCommandUsageListener implements Listener {

    private final CommandUsageTracker tracker;
    private final List<String> labels;
    private final String command;

    public AuthorsCommandUsageListener(CommandUsageTracker tracker, List<String> labels, String command) {
        this.tracker = tracker;
        this.labels = labels.stream().map(AuthorsCommandUsageListener::normalize).toList();
        this.command = normalize(command);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (matches(event.getMessage())) {
            this.tracker.recordUsage(event.getPlayer().getUniqueId());
        }
    }

    private boolean matches(String message) {
        return matches(message, this.labels, this.command);
    }

    /**
     * Returns whether a raw command message is an execution of a tracked
     * command, e.g. {@code "/jtemplate authors"} or {@code "/jt authors"}.
     * Upper/lower case is ignored.
     *
     * @param message the event message (starts with {@code /})
     */
    static boolean matches(String message, List<String> labels, String command) {
        if (message == null || message.length() < 2 || message.charAt(0) != '/') {
            return false;
        }
        String[] tokens = message.substring(1).trim().split("\\s+");
        if (tokens.length < 2) {
            return false;
        }
        return labels.contains(normalize(tokens[0]))
                && normalize(command).equals(normalize(tokens[1]));
    }

    private static String normalize(String value) {
        return value.toLowerCase(Locale.ROOT);
    }
}