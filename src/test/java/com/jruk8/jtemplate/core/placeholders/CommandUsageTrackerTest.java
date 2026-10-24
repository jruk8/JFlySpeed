package com.jruk8.jtemplate.core.placeholders;

import com.jruk8.jtemplate.core.commands.speed.SpeedType;
import com.jruk8.jtemplate.core.storage.SqliteStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CommandUsageTrackerTest {

    private final UUID player = UUID.randomUUID();

    private SqliteStorage storage;
    private CommandUsageTracker tracker;

    @BeforeEach
    void setUp() {
        storage = new SqliteStorage("jdbc:sqlite::memory:", Logger.getLogger("tracker-test"));
        assertTrue(storage.init());
        tracker = new CommandUsageTracker(storage);
    }

    @AfterEach
    void tearDown() {
        storage.close();
    }

    @Test
    @DisplayName("should start at zero on every counter")
    void shouldStartAtZero() {
        assertEquals(0, tracker.playerUses(player));
        assertEquals(0, tracker.playerFlyUses(player));
        assertEquals(0, tracker.playerWalkUses(player));
    }

    @Test
    @DisplayName("should record a fly use on the total and fly counters")
    void shouldRecordFlyUse() {
        tracker.recordUse(player, SpeedType.FLY);

        assertEquals(1, tracker.playerUses(player));
        assertEquals(1, tracker.playerFlyUses(player));
        assertEquals(0, tracker.playerWalkUses(player));
    }

    @Test
    @DisplayName("should record a walk use on the total and walk counters")
    void shouldRecordWalkUse() {
        tracker.recordUse(player, SpeedType.WALK);

        assertEquals(1, tracker.playerUses(player));
        assertEquals(0, tracker.playerFlyUses(player));
        assertEquals(1, tracker.playerWalkUses(player));
    }

    @Test
    @DisplayName("should record a both-channels use once overall and once per channel")
    void shouldRecordBothUse() {
        tracker.recordBothUse(player);

        assertEquals(1, tracker.playerUses(player));
        assertEquals(1, tracker.playerFlyUses(player));
        assertEquals(1, tracker.playerWalkUses(player));
    }

    @Test
    @DisplayName("should accumulate usages per player over time")
    void shouldAccumulateUsages() {
        tracker.recordUse(player, SpeedType.FLY);
        tracker.recordBothUse(player);
        tracker.recordUse(UUID.randomUUID(), SpeedType.WALK);

        assertEquals(2, tracker.playerUses(player));
        assertEquals(2, tracker.playerFlyUses(player));
        assertEquals(1, tracker.playerWalkUses(player));
    }
}
