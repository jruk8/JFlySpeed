package com.jruk8.jtemplate.core.placeholders;

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
    @DisplayName("should start at zero for a player and the server")
    void shouldStartAtZero() {
        assertEquals(0, tracker.playerUses(player));
        assertEquals(0, tracker.globalUses());
    }

    @Test
    @DisplayName("should record a usage for the player and the server")
    void shouldRecordUsage() {
        tracker.recordUsage(player);

        assertEquals(1, tracker.playerUses(player));
        assertEquals(1, tracker.globalUses());
    }

    @Test
    @DisplayName("should accumulate usages over time")
    void shouldAccumulateUsages() {
        tracker.recordUsage(player);
        tracker.recordUsage(player);
        tracker.recordUsage(UUID.randomUUID());

        assertEquals(2, tracker.playerUses(player));
        assertEquals(3, tracker.globalUses());
    }
}