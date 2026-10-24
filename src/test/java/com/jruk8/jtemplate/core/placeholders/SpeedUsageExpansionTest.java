package com.jruk8.jtemplate.core.placeholders;

import io.papermc.paper.plugin.configuration.PluginMeta;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpeedUsageExpansionTest {

    private final UUID selfId = UUID.randomUUID();
    private final UUID otherId = UUID.randomUUID();

    @Mock
    private CommandUsageTracker tracker;

    @Mock
    private JavaPlugin plugin;

    private SpeedUsageExpansion expansion() {
        return new SpeedUsageExpansion(tracker, plugin, Map.of("Alex", otherId)::get);
    }

    private OfflinePlayer player(UUID id) {
        OfflinePlayer player = mock(OfflinePlayer.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    private void meta(String name, String version) {
        PluginMeta meta = mock(PluginMeta.class);
        when(meta.getAuthors()).thenReturn(List.of("jruk8"));
        when(meta.getVersion()).thenReturn(version);
        when(plugin.getPluginMeta()).thenReturn(meta);
        when(plugin.getName()).thenReturn(name);
    }

    @Test
    @DisplayName("should advertise the jf identifier and all six placeholders")
    void shouldAdvertisePlaceholders() {
        var placeholders = expansion().getPlaceholders();

        assertEquals("jf", expansion().getIdentifier());
        assertEquals(6, placeholders.size());
        assertTrue(placeholders.contains("%jf_uses%"));
        assertTrue(placeholders.contains("%jf_uses_<player>%"));
        assertTrue(placeholders.contains("%jf_fly_uses%"));
        assertTrue(placeholders.contains("%jf_fly_uses_<player>%"));
        assertTrue(placeholders.contains("%jf_walk_uses%"));
        assertTrue(placeholders.contains("%jf_walk_uses_<player>%"));
    }

    @Test
    @DisplayName("should read author, version and required plugin from metadata")
    void shouldReadMetadata() {
        meta("JFlySpeed", "1.2.0");

        assertEquals("jruk8", expansion().getAuthor());
        assertEquals("1.2.0", expansion().getVersion());
        assertEquals("JFlySpeed", expansion().getRequiredPlugin());
    }

    @Test
    @DisplayName("should resolve self counters for the requesting player")
    void shouldResolveSelfCounters() {
        when(tracker.playerUses(selfId)).thenReturn(7);
        when(tracker.playerFlyUses(selfId)).thenReturn(5);
        when(tracker.playerWalkUses(selfId)).thenReturn(2);
        OfflinePlayer self = player(selfId);

        assertEquals("7", expansion().onRequest(self, "uses"));
        assertEquals("5", expansion().onRequest(self, "fly_uses"));
        assertEquals("2", expansion().onRequest(self, "walk_uses"));
    }

    @Test
    @DisplayName("should resolve counters for a named player")
    void shouldResolveNamedCounters() {
        when(tracker.playerUses(otherId)).thenReturn(4);
        when(tracker.playerFlyUses(otherId)).thenReturn(3);
        when(tracker.playerWalkUses(otherId)).thenReturn(1);

        assertEquals("4", expansion().onRequest(mock(OfflinePlayer.class), "uses_Alex"));
        assertEquals("3", expansion().onRequest(mock(OfflinePlayer.class), "fly_uses_Alex"));
        assertEquals("1", expansion().onRequest(mock(OfflinePlayer.class), "walk_uses_Alex"));
    }

    @Test
    @DisplayName("should return null for missing context and unknown params")
    void shouldReturnNullForUnresolvable() {
        assertNull(expansion().onRequest(null, "uses"));
        assertNull(expansion().onRequest(mock(OfflinePlayer.class), "uses_"));
        assertNull(expansion().onRequest(mock(OfflinePlayer.class), "nope"));
    }
}
