package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.entity.Player;

import java.util.List;
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
        sendFeedback(source, targets, type, SpeedValues.format(scale), false);
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
        double def = speeds.defaultScale(type);
        for (Player target : targets) {
            speeds.resetSpeed(target, type);
        }
        sendFeedback(source, targets, type, SpeedValues.format(def), true);
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
        resetSpeed(source, SpeedType.FLY, targets);
        resetSpeed(source, SpeedType.WALK, targets);
        return Command.SINGLE_SUCCESS;
    }

    private void sendFeedback(
            CommandSourceStack source,
            List<Player> targets,
            SpeedType type,
            String value,
            boolean reset
    ) {
        if (isSelfOnly(source, targets)) {
            sendSelf(source, type, value, reset);
            return;
        }
        sendSummary(source, targets, type, value, reset);
        for (Player target : targets) {
            if (!isSender(source, target)) {
                notifyTarget(target, type, value, reset);
            }
        }
    }

    private void sendSelf(CommandSourceStack source, SpeedType type, String value, boolean reset) {
        var typeTag = Placeholder.parsed("type", type.getKey());
        var valueTag = Placeholder.parsed("value", value);
        if (reset) {
            ctx.sendReset(source, m -> m.getSpeed().getResetSelf(), typeTag, valueTag);
        } else {
            ctx.sendSet(source, m -> m.getSpeed().getSetSelf(), typeTag, valueTag);
        }
    }

    private void sendSummary(
            CommandSourceStack source,
            List<Player> targets,
            SpeedType type,
            String value,
            boolean reset
    ) {
        String names = targets.stream().map(Player::getName).collect(Collectors.joining(", "));
        var typeTag = Placeholder.parsed("type", type.getKey());
        var valueTag = Placeholder.parsed("value", value);
        var targetsTag = Placeholder.parsed("targets", names);
        if (targets.size() == 1) {
            sendOther(source, reset, typeTag, valueTag, targetsTag);
        } else {
            sendMultiple(source, targets.size(), reset, typeTag, valueTag, targetsTag);
        }
    }

    private void sendOther(
            CommandSourceStack source,
            boolean reset,
            TagResolver type,
            TagResolver value,
            TagResolver targets
    ) {
        if (reset) {
            ctx.sendReset(source, m -> m.getSpeed().getResetOther(), type, value, targets);
        } else {
            ctx.sendSet(source, m -> m.getSpeed().getSetOther(), type, value, targets);
        }
    }

    private void sendMultiple(
            CommandSourceStack source,
            int count,
            boolean reset,
            TagResolver type,
            TagResolver value,
            TagResolver targets
    ) {
        var countTag = Placeholder.parsed("count", String.valueOf(count));
        if (reset) {
            ctx.sendReset(source, m -> m.getSpeed().getResetMultiple(), type, value, countTag, targets);
        } else {
            ctx.sendSet(source, m -> m.getSpeed().getSetMultiple(), type, value, countTag, targets);
        }
    }

    private void notifyTarget(Player target, SpeedType type, String value, boolean reset) {
        var typeTag = Placeholder.parsed("type", type.getKey());
        var valueTag = Placeholder.parsed("value", value);
        if (reset) {
            ctx.notifyReset(target, m -> m.getSpeed().getResetTarget(), typeTag, valueTag);
        } else {
            ctx.notifySet(target, m -> m.getSpeed().getSetTarget(), typeTag, valueTag);
        }
    }

    private boolean isSelfOnly(CommandSourceStack source, List<Player> targets) {
        return targets.size() == 1 && isSender(source, targets.getFirst());
    }

    private boolean isSender(CommandSourceStack source, Player target) {
        return source.getSender() instanceof Player self && self.getUniqueId().equals(target.getUniqueId());
    }
}
