package com.jruk8.jtemplate.core.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.ToIntFunction;

/**
 * The JFlySpeed PlaceholderAPI expansion.
 * <p>
 * Identifier {@code jf}, with self and other-player variants per counter:
 * {@code uses}, {@code fly_uses} and {@code walk_uses}, each optionally
 * followed by {@code _<player>}. Unknown players read as {@code 0}; the
 * self variants need a player context.
 */
public final class SpeedUsageExpansion extends PlaceholderExpansion {

    public static final String IDENTIFIER = "jf";
    public static final String USES = "uses";
    public static final String FLY_USES = "fly_uses";
    public static final String WALK_USES = "walk_uses";

    private final CommandUsageTracker tracker;
    private final JavaPlugin plugin;
    private final Function<String, UUID> nameResolver;

    public SpeedUsageExpansion(
            CommandUsageTracker tracker,
            JavaPlugin plugin,
            Function<String, UUID> nameResolver
    ) {
        this.tracker = tracker;
        this.plugin = plugin;
        this.nameResolver = nameResolver;
    }

    @Override
    public String getIdentifier() {
        return IDENTIFIER;
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
                "%" + IDENTIFIER + "_" + USES + "%",
                "%" + IDENTIFIER + "_" + USES + "_<player>%",
                "%" + IDENTIFIER + "_" + FLY_USES + "%",
                "%" + IDENTIFIER + "_" + FLY_USES + "_<player>%",
                "%" + IDENTIFIER + "_" + WALK_USES + "%",
                "%" + IDENTIFIER + "_" + WALK_USES + "_<player>%"
        );
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (USES.equals(params)) {
            return selfUses(player, this.tracker::playerUses);
        }
        if (params.startsWith(USES + "_")) {
            return namedUses(params.substring(USES.length() + 1), this.tracker::playerUses);
        }
        if (FLY_USES.equals(params)) {
            return selfUses(player, this.tracker::playerFlyUses);
        }
        if (params.startsWith(FLY_USES + "_")) {
            return namedUses(params.substring(FLY_USES.length() + 1), this.tracker::playerFlyUses);
        }
        if (WALK_USES.equals(params)) {
            return selfUses(player, this.tracker::playerWalkUses);
        }
        if (params.startsWith(WALK_USES + "_")) {
            return namedUses(params.substring(WALK_USES.length() + 1), this.tracker::playerWalkUses);
        }
        return null;
    }

    private static String selfUses(OfflinePlayer player, ToIntFunction<UUID> read) {
        return player == null ? null : String.valueOf(read.applyAsInt(player.getUniqueId()));
    }

    private String namedUses(String name, ToIntFunction<UUID> read) {
        if (name.isEmpty()) {
            return null;
        }
        return String.valueOf(read.applyAsInt(this.nameResolver.apply(name)));
    }
}
