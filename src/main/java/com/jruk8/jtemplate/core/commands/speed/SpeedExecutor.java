package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Applies speed changes to resolved targets and sends the matching feedback:
 * the sender always gets a summary, and affected players other than the
 * sender get a short notification.
 */
public class SpeedExecutor {

    private final CommandContext ctx;
    private final SpeedService speeds;

    public SpeedExecutor(CommandContext ctx, SpeedService speeds) {
        this.ctx = ctx;
        this.speeds = speeds;
    }

    /**
     * Sets one speed channel for the targets.
     *
     * @param source the command source
     * @param type which speed channel to set
     * @param scale the user-facing value (0 to 10)
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}
     */
    public int setSpeed(CommandSourceStack source, SpeedType type, double scale, List<Player> targets) {
        Map<UUID, String> olds = captureOlds(targets, type);
        for (Player target : targets) {
            speeds.setSpeed(target, type, scale);
        }
        sendSetFeedback(source, targets, type, SpeedValues.format(scale), olds, scale);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Resets one speed channel for the targets.
     *
     * @param source the command source
     * @param type which speed channel to reset
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}
     */
    public int resetSpeed(CommandSourceStack source, SpeedType type, List<Player> targets) {
        Map<UUID, String> olds = captureOlds(targets, type);
        for (Player target : targets) {
            speeds.resetSpeed(target, type);
        }
        sendResetFeedback(source, targets, type, olds);
        return Command.SINGLE_SUCCESS;
    }

    /**
     * Resets both speed channels for the targets.
     *
     * @param source the command source
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}
     */
    public int resetBoth(CommandSourceStack source, List<Player> targets) {
        for (Player target : targets) {
            speeds.resetSpeed(target, SpeedType.FLY);
            speeds.resetSpeed(target, SpeedType.WALK);
        }
        sendBothFeedback(source, targets);
        return Command.SINGLE_SUCCESS;
    }

    private void sendSetFeedback(
            CommandSourceStack source,
            List<Player> targets,
            SpeedType type,
            String value,
            Map<UUID, String> olds,
            double scale
    ) {
        String updated = SpeedValues.formatWas(scale);
        if (isSelfOnly(source, targets)) {
            Player self = targets.getFirst();
            var tags = setTags(type, value, self, olds);
            ctx.sendSet(source, withWas(m -> m.getSpeed().getSetSelf(), self, olds, updated), tags);
            return;
        }
        var targetsValue = targetNames(targets);
        if (targets.size() == 1) {
            Player other = targets.getFirst();
            var tags = setTags(type, value, other, olds);
            tags.put("targets", targetsValue);
            ctx.sendSet(source, withWas(m -> m.getSpeed().getSetOther(), other, olds, updated), tags);
        } else {
            var tags = Map.of(
                    "type", type.getKey(),
                    "value", value,
                    "targets", targetsValue,
                    "count", String.valueOf(targets.size()));
            ctx.sendSet(source, m -> m.getSpeed().getSetMultiple(), tags);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                var tags = setTags(type, value, target, olds);
                ctx.notifySet(target, withWas(m -> m.getSpeed().getSetTarget(), target, olds, updated), tags);
            }
        }
    }

    private static Map<String, String> setTags(
            SpeedType type,
            String value,
            Player target,
            Map<UUID, String> olds
    ) {
        var tags = new HashMap<String, String>();
        tags.put("type", type.getKey());
        tags.put("value", value);
        tags.put("old", olds.getOrDefault(target.getUniqueId(), "?"));
        return tags;
    }

    private static Function<MessagesConfig, String> withWas(
            Function<MessagesConfig, String> base,
            Player target,
            Map<UUID, String> olds,
            String updated
    ) {
        if (olds.getOrDefault(target.getUniqueId(), "?").equals(updated)) {
            return base;
        }
        return messages -> base.apply(messages) + messages.getSpeed().getWasSuffix();
    }

    private Map<UUID, String> captureOlds(List<Player> targets, SpeedType type) {
        Map<UUID, String> olds = new HashMap<>();
        for (Player target : targets) {
            olds.put(target.getUniqueId(), SpeedValues.formatWas(speeds.currentScale(target, type)));
        }
        return olds;
    }

    private void sendResetFeedback(
            CommandSourceStack source,
            List<Player> targets,
            SpeedType type,
            Map<UUID, String> olds
    ) {
        if (isSelfOnly(source, targets)) {
            Player self = targets.getFirst();
            ctx.sendReset(source, withWasSuffix(m -> m.getSpeed().getResetSelf()), resetTags(type, self, olds));
            return;
        }
        if (targets.size() == 1) {
            Player other = targets.getFirst();
            var tags = new HashMap<>(resetTags(type, other, olds));
            tags.put("targets", targetNames(targets));
            ctx.sendReset(source, withWasSuffix(m -> m.getSpeed().getResetOther()), tags);
        } else {
            var tags = Map.of(
                    "mode", type.getKey(),
                    "targets", targetNames(targets),
                    "count", String.valueOf(targets.size()));
            ctx.sendReset(source, m -> m.getSpeed().getResetMultiple(), tags);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                var tags = resetTags(type, target, olds);
                ctx.notifyReset(target, withWasSuffix(m -> m.getSpeed().getResetTarget()), tags);
            }
        }
    }

    private static Function<MessagesConfig, String> withWasSuffix(Function<MessagesConfig, String> base) {
        return messages -> base.apply(messages) + messages.getSpeed().getWasSuffix();
    }

    private static Map<String, String> resetTags(SpeedType type, Player target, Map<UUID, String> olds) {
        return Map.of("mode", type.getKey(), "old", olds.getOrDefault(target.getUniqueId(), "?"));
    }

    private void sendBothFeedback(CommandSourceStack source, List<Player> targets) {
        if (isSelfOnly(source, targets)) {
            ctx.sendReset(source, m -> m.getSpeed().getResetBoth(), Map.of());
            return;
        }
        var targetsValue = targetNames(targets);
        if (targets.size() == 1) {
            ctx.sendReset(source, m -> m.getSpeed().getResetBothOther(), Map.of("targets", targetsValue));
        } else {
            var tags = Map.of("targets", targetsValue, "count", String.valueOf(targets.size()));
            ctx.sendReset(source, m -> m.getSpeed().getResetBothMultiple(), tags);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                ctx.notifyReset(target, m -> m.getSpeed().getResetBoth(), Map.of());
            }
        }
    }

    private static String targetNames(List<Player> targets) {
        return targets.stream().map(Player::getName).collect(Collectors.joining(", "));
    }

    private boolean isSelfOnly(CommandSourceStack source, List<Player> targets) {
        return targets.size() == 1 && isSender(source, targets.getFirst());
    }

    private boolean isSender(CommandSourceStack source, Player target) {
        return source.getSender() instanceof Player self && self.getUniqueId().equals(target.getUniqueId());
    }
}
