package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpeedExecutorTest {

    @Mock
    private CommandContext ctx;

    @Mock
    private SpeedService speeds;

    @Mock
    private CommandSourceStack source;

    private SpeedExecutor executor() {
        return new SpeedExecutor(ctx, speeds);
    }

    private Player player(UUID id) {
        Player player = mock(Player.class);
        when(player.getUniqueId()).thenReturn(id);
        return player;
    }

    @Test
    @DisplayName("should apply to every target and notify the others")
    void shouldApplyToEveryTarget() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        Player other = player(UUID.randomUUID());
        when(self.getName()).thenReturn("Steve");
        when(other.getName()).thenReturn("Alex");
        when(source.getSender()).thenReturn(self);

        int result = executor().resetSpeed(source, SpeedType.FLY, List.of(self, other));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(speeds).resetSpeed(self, SpeedType.FLY);
        verify(speeds).resetSpeed(other, SpeedType.FLY);
        var placeholders = forClass(Map.class);
        verify(ctx).sendReset(eq(source), any(Function.class), placeholders.capture());
        assertEquals("fly", placeholders.getValue().get("mode"));
        assertEquals("Steve, Alex", placeholders.getValue().get("targets"));
        assertEquals("2", placeholders.getValue().get("count"));
        verify(ctx).notifyReset(eq(other), any(Function.class), any(Map.class));
        verify(ctx, never()).notifyReset(eq(self), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should include the mode and old value on a self reset")
    void shouldIncludeModeAndOldOnSelfReset() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);
        when(speeds.currentScale(self, SpeedType.WALK)).thenReturn(5.0);

        int result = executor().resetSpeed(source, SpeedType.WALK, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(speeds).resetSpeed(self, SpeedType.WALK);
        var placeholders = forClass(Map.class);
        verify(ctx).sendReset(eq(source), any(Function.class), placeholders.capture());
        assertEquals("walk", placeholders.getValue().get("mode"));
        assertEquals("5", placeholders.getValue().get("old"));
    }

    @Test
    @DisplayName("should send the self message without notifications when alone")
    void shouldSendSelfMessageWhenAlone() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);

        int result = executor().setSpeed(source, SpeedType.WALK, 7.0, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(speeds).setSpeed(self, SpeedType.WALK, 7.0);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("walk", placeholders.getValue().get("type"));
        assertEquals("7", placeholders.getValue().get("value"));
        verify(ctx, never()).notifySet(any(Player.class), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should send one summary for a single other target")
    void shouldSendOtherSummaryForSingleTarget() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        Player other = player(UUID.randomUUID());
        when(other.getName()).thenReturn("Alex");
        when(source.getSender()).thenReturn(self);

        int result = executor().setSpeed(source, SpeedType.FLY, 8.0, List.of(other));

        assertEquals(Command.SINGLE_SUCCESS, result);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("fly", placeholders.getValue().get("type"));
        assertEquals("8", placeholders.getValue().get("value"));
        assertEquals("Alex", placeholders.getValue().get("targets"));
        verify(ctx).notifySet(eq(other), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should send the multi-target summary for several targets")
    void shouldSendMultipleSummaryForSeveralTargets() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        Player first = player(UUID.randomUUID());
        Player second = player(UUID.randomUUID());
        when(first.getName()).thenReturn("Alex");
        when(second.getName()).thenReturn("Steve");
        when(source.getSender()).thenReturn(self);

        int result = executor().setSpeed(source, SpeedType.FLY, 8.0, List.of(first, second));

        assertEquals(Command.SINGLE_SUCCESS, result);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), any(Function.class), placeholders.capture());
        assertEquals("2", placeholders.getValue().get("count"));
        assertEquals("Alex, Steve", placeholders.getValue().get("targets"));
        verify(ctx).notifySet(eq(first), any(Function.class), any(Map.class));
        verify(ctx).notifySet(eq(second), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should append the was-suffix on a set when the old value differs")
    void shouldShowWasSuffixOnChangedSet() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);
        when(speeds.currentScale(self, SpeedType.FLY)).thenReturn(2.0);

        int result = executor().setSpeed(source, SpeedType.FLY, 7.0, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        var selector = forClass(Function.class);
        var placeholders = forClass(Map.class);
        verify(ctx).sendSet(eq(source), selector.capture(), placeholders.capture());
        assertEquals("2", placeholders.getValue().get("old"));
        @SuppressWarnings("unchecked")
        Function<MessagesConfig, String> fn = (Function<MessagesConfig, String>) selector.getValue();
        assertTrue(fn.apply(new MessagesConfig()).contains("(was"));
    }

    @Test
    @DisplayName("should hide the was-suffix on a set when the rounded old value matches")
    void shouldHideWasSuffixOnUnchangedSet() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);
        when(speeds.currentScale(self, SpeedType.FLY)).thenReturn(7.04);

        int result = executor().setSpeed(source, SpeedType.FLY, 7.0, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        var selector = forClass(Function.class);
        verify(ctx).sendSet(eq(source), selector.capture(), any(Map.class));
        @SuppressWarnings("unchecked")
        Function<MessagesConfig, String> fn = (Function<MessagesConfig, String>) selector.getValue();
        var messages = new MessagesConfig();
        assertEquals(messages.getSpeed().getSetSelf(), fn.apply(messages));
    }

    @Test
    @DisplayName("should reset both channels via resetBoth()")
    void shouldResetBothChannels() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        when(source.getSender()).thenReturn(self);

        int result = executor().resetBoth(source, List.of(self));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(speeds).resetSpeed(self, SpeedType.FLY);
        verify(speeds).resetSpeed(self, SpeedType.WALK);
        verify(ctx, times(1))
                .sendReset(eq(source), any(Function.class), any(Map.class));
        verify(ctx, never()).notifyReset(eq(self), any(Function.class), any(Map.class));
    }

    @Test
    @DisplayName("should summarize once and notify others on a both reset")
    void shouldSummarizeBothResetOnce() {
        var selfId = UUID.randomUUID();
        Player self = player(selfId);
        Player other = player(UUID.randomUUID());
        when(other.getName()).thenReturn("Alex");
        when(source.getSender()).thenReturn(self);

        int result = executor().resetBoth(source, List.of(self, other));

        assertEquals(Command.SINGLE_SUCCESS, result);
        verify(ctx, times(1))
                .sendReset(eq(source), any(Function.class), any(Map.class));
        verify(ctx, never()).notifyReset(eq(self), any(Function.class), any(Map.class));
        verify(ctx, times(1))
                .notifyReset(eq(other), any(Function.class), any(Map.class));
    }
}
