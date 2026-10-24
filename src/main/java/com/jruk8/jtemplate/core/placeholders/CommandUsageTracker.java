package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.storage.SqliteStorage;

import java.util.UUID;

/**
 * Domain service for the "authors command usage" statistic tracked by the
 * template placeholders. Keeps the DB stat key in one place and exposes the
 * read/write operations the expansion and listeners need.
 */
public final class CommandUsageTracker {

    /**
     * Stat key used for both the per-player counter and the global counter.
     */
    public static final String STAT_KEY = "authors_command_uses";

    private final SqliteStorage storage;

    public CommandUsageTracker(SqliteStorage storage) {
        this.storage = storage;
    }

    /**
     * Records one execution of the tracked command.
     *
     * @param player the player that executed the command
     */
    public void recordUsage(UUID player) {
        this.storage.increment(STAT_KEY, player);
        this.storage.incrementGlobal(STAT_KEY);
    }

    public int playerUses(UUID player) {
        return this.storage.playerTotal(STAT_KEY, player);
    }

    public int globalUses() {
        return this.storage.globalTotal(STAT_KEY);
    }
}