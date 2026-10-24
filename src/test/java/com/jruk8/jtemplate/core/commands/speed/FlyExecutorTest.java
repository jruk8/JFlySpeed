package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FlyExecutorTest {

    @Mock
    private CommandContext ctx;

    @Mock
    private CommandSourceStack source;

    private FlyExecutor executor() {
        return new FlyExecutor(ctx);
    }

    private Player player(UUID id) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    @Test
    @DisplayName("should enable flight without notifications when alone")
    void shouldEnableFlightWhenAlone() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);

        int result = executor().setFly(source, true, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(self).setAllowFlight(true);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("on", placeholders.getValue().get("mode"));
        verify(ctx, never()).notifySet(any(Player.class), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should land the player when disabling flight")
    void shouldLandPlayerWhenDisabling() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        Player other = player(UUID.randomUUID());
        when(other.getName()).thenReturn("Alex");
        when(source.getSender()).thenReturn(self);

        int result = executor().setFly(source, false, List.of(other));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(other).setAllowFlight(false);
        verify(other).setFlying(false);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("off", placeholders.getValue().get("mode"));
        assertEquals("Alex", placeholders.getValue().get("targets"));
        verify(ctx).notifySet(eq(other), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should turn flight off when toggling a flying player")
    void shouldToggleOffFlyingPlayer() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);
        when(self.getAllowFlight()).thenReturn(true);

        int result = executor().toggleFly(source, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(self).setAllowFlight(false);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("off", placeholders.getValue().get("mode"));
    }

    @Test
    @DisplayName("should turn everyone on when toggling a mixed group")
    void shouldTurnMixedGroupOn() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        Player other = player(UUID.randomUUID());
        when(self.getName()).thenReturn("Steve");
        when(other.getName()).thenReturn("Alex");
        when(source.getSender()).thenReturn(self);
        when(self.getAllowFlight()).thenReturn(true);
        when(other.getAllowFlight()).thenReturn(false);

        int result = executor().toggleFly(source, List.of(self, other));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(self).setAllowFlight(true);
        verify(other).setAllowFlight(true);
        var placeholders = forClass(Map.class);
        verify(ctx, times(1)).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("on", placeholders.getValue().get("mode"));
        assertEquals("2", placeholders.getValue().get("count"));
        verify(ctx, never()).notifySet(eq(self), any(Function.class), any(Map.class));
        verify(ctx, times(1)).notifySet(eq(other), any(Function.class), any(Map.class));
    }
}
