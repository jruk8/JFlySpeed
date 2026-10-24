package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.commands.speed.SpeedType;
import com.jruk8.jtemplate.core.storage.SqliteStorage;

import java.util.UUID;

/**
 * Domain service for the speed-command usage statistics behind the
 * {@code jf} placeholders. Keeps the DB stat keys in one place and exposes
 * the read/write operations the expansion and commands need.
 * <p>
 * Every successful speed command execution records one overall use plus one
 * use on each affected channel, so a both-channels reset counts once
 * overall and once per channel.
 */
public final class CommandUsageTracker {

    /**
     * Stat key for total speed command executions per player.
     */
    public static final String USES_KEY = "jf_uses";

    /**
     * Stat key for fly channel executions per player.
     */
    public static final String FLY_USES_KEY = "jf_fly_uses";

    /**
     * Stat key for walk channel executions per player.
     */
    public static final String WALK_USES_KEY = "jf_walk_uses";

    private final SqliteStorage storage;

    public CommandUsageTracker(SqliteStorage storage) {
        this.storage = storage;
    }

    /**
     * Records one execution affecting a single speed channel.
     *
     * @param player the player that executed the command
     * @param type which speed channel was affected
     */
    public void recordUse(UUID player, SpeedType type) {
        this.storage.increment(USES_KEY, player);
        this.storage.increment(type == SpeedType.FLY ? FLY_USES_KEY : WALK_USES_KEY, player);
    }

    /**
     * Records one execution affecting both speed channels.
     *
     * @param player the player that executed the command
     */
    public void recordBothUse(UUID player) {
        this.storage.increment(USES_KEY, player);
        this.storage.increment(FLY_USES_KEY, player);
        this.storage.increment(WALK_USES_KEY, player);
    }

    public int playerUses(UUID player) {
        return this.storage.playerTotal(USES_KEY, player);
    }

    public int playerFlyUses(UUID player) {
        return this.storage.playerTotal(FLY_USES_KEY, player);
    }

    public int playerWalkUses(UUID player) {
        return this.storage.playerTotal(WALK_USES_KEY, player);
    }
}
