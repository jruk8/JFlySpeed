package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
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
        verify(ctx).sendReset(eq(source), any(Function.class), any(Map.class), any(TagResolver[].class));
        verify(ctx).notifyReset(eq(other), any(Function.class), any(Map.class), any(TagResolver[].class));
        verify(ctx, never())
                .notifyReset(eq(self), any(Function.class), any(Map.class), any(TagResolver[].class));
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
        var resolvers = forClass(TagResolver[].class);
        verify(ctx).sendReset(eq(source), any(Function.class), placeholders.capture(), resolvers.capture());
        assertEquals("walk", placeholders.getValue().get("mode"));
        assertEquals(1, resolvers.getValue().length);
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
        verify(ctx).sendSet(eq(source), any(Function.class), any(TagResolver[].class));
        verify(ctx, never()).notifySet(any(Player.class), any(Function.class), any(TagResolver[].class));
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
        var resolvers = forClass(TagResolver[].class);
        verify(ctx).sendSet(eq(source), any(Function.class), resolvers.capture());
        assertEquals(3, resolvers.getValue().length);
        verify(ctx).notifySet(eq(other), any(Function.class), any(TagResolver[].class));
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
        var resolvers = forClass(TagResolver[].class);
        verify(ctx).sendSet(eq(source), any(Function.class), resolvers.capture());
        assertEquals(4, resolvers.getValue().length);
        verify(ctx).notifySet(eq(first), any(Function.class), any(TagResolver[].class));
        verify(ctx).notifySet(eq(second), any(Function.class), any(TagResolver[].class));
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
                .sendReset(eq(source), any(Function.class), any(TagResolver[].class));
        verify(ctx, never()).notifyReset(eq(self), any(Function.class), any(TagResolver[].class));
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
                .sendReset(eq(source), any(Function.class), any(TagResolver[].class));
        verify(ctx, never()).notifyReset(eq(self), any(Function.class), any(TagResolver[].class));
        verify(ctx, times(1))
                .notifyReset(eq(other), any(Function.class), any(TagResolver[].class));
    }
}
