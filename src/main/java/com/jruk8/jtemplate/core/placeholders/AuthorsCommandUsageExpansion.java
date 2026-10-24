package com.jruk8.jtemplate.core.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * The template PlaceholderAPI expansion.
 * <p>
 * The {@code identifier} (placeholder prefix) is configurable and defaults to
 * {@code jtemplate}. Add new placeholder names as additional switch cases.
 */
public final class AuthorsCommandUsageExpansion extends PlaceholderExpansion {

    public static final String AUTHORS_COMMAND_USES = "authors_command_uses";
    public static final String AUTHORS_COMMAND_USES_GLOBAL = "authors_command_uses_global";

    private final String identifier;
    private final CommandUsageTracker tracker;
    private final JavaPlugin plugin;

    public AuthorsCommandUsageExpansion(String identifier, CommandUsageTracker tracker, JavaPlugin plugin) {
        this.identifier = identifier;
        this.tracker = tracker;
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return this.identifier;
    }

    @Override
    public String getAuthor() {
        List<String> authors = this.plugin.getPluginMeta().getAuthors();
        return authors.isEmpty() ? this.plugin.getName() : authors.getFirst();
    }

    @Override
    public String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }

    @Override
    public String getRequiredPlugin() {
        return this.plugin.getName();
    }

    @Override
    public List<String> getPlaceholders() {
        return List.of(
                "%" + this.identifier + "_" + AUTHORS_COMMAND_USES + "%",
                "%" + this.identifier + "_" + AUTHORS_COMMAND_USES_GLOBAL + "%"
        );
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        return switch (params) {
            case AUTHORS_COMMAND_USES -> player == null
                    ? null
                    : String.valueOf(this.tracker.playerUses(player.getUniqueId()));
            case AUTHORS_COMMAND_USES_GLOBAL -> String.valueOf(this.tracker.globalUses());
            default -> null;
        };
    }
}