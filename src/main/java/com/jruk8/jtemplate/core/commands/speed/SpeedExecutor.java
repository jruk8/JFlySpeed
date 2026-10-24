package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
        for (Player target : targets) {
            speeds.setSpeed(target, type, scale);
        }
        sendSetFeedback(source, targets, type, SpeedValues.format(scale));
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
        Map<UUID, String> olds = new HashMap<>();
        for (Player target : targets) {
            olds.put(target.getUniqueId(), SpeedValues.format(speeds.currentScale(target, type)));
        }
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
            String value
    ) {
        var typeTag = Placeholder.parsed("type", type.getKey());
        var valueTag = Placeholder.parsed("value", value);
        if (isSelfOnly(source, targets)) {
            ctx.sendSet(source, m -> m.getSpeed().getSetSelf(), typeTag, valueTag);
            return;
        }
        var targetsTag = Placeholder.parsed("targets", targetNames(targets));
        if (targets.size() == 1) {
            ctx.sendSet(source, m -> m.getSpeed().getSetOther(), typeTag, valueTag, targetsTag);
        } else {
            var countTag = Placeholder.parsed("count", String.valueOf(targets.size()));
            ctx.sendSet(source, m -> m.getSpeed().getSetMultiple(), typeTag, valueTag, countTag, targetsTag);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                ctx.notifySet(target, m -> m.getSpeed().getSetTarget(), typeTag, valueTag);
            }
        }
    }

    private void sendResetFeedback(
            CommandSourceStack source,
            List<Player> targets,
            SpeedType type,
            Map<UUID, String> olds
    ) {
        var mode = Map.of("mode", type.getKey());
        if (isSelfOnly(source, targets)) {
            Player self = targets.getFirst();
            ctx.sendReset(source, m -> m.getSpeed().getResetSelf(), mode, oldTag(self, olds));
            return;
        }
        var targetsTag = Placeholder.parsed("targets", targetNames(targets));
        if (targets.size() == 1) {
            Player other = targets.getFirst();
            ctx.sendReset(source, m -> m.getSpeed().getResetOther(), mode, targetsTag, oldTag(other, olds));
        } else {
            var countTag = Placeholder.parsed("count", String.valueOf(targets.size()));
            ctx.sendReset(source, m -> m.getSpeed().getResetMultiple(), mode, targetsTag, countTag);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                ctx.notifyReset(target, m -> m.getSpeed().getResetTarget(), mode, oldTag(target, olds));
            }
        }
    }

    private static TagResolver oldTag(Player target, Map<UUID, String> olds) {
        return Placeholder.parsed("old", olds.getOrDefault(target.getUniqueId(), "?"));
    }

    private void sendBothFeedback(CommandSourceStack source, List<Player> targets) {
        if (isSelfOnly(source, targets)) {
            ctx.sendReset(source, m -> m.getSpeed().getResetBoth());
            return;
        }
        var targetsTag = Placeholder.parsed("targets", targetNames(targets));
        if (targets.size() == 1) {
            ctx.sendReset(source, m -> m.getSpeed().getResetBothOther(), targetsTag);
        } else {
            var countTag = Placeholder.parsed("count", String.valueOf(targets.size()));
            ctx.sendReset(source, m -> m.getSpeed().getResetBothMultiple(), targetsTag, countTag);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                ctx.notifyReset(target, m -> m.getSpeed().getResetBoth());
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
