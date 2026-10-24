package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Applies fly mode changes to resolved targets and sends the matching feedback:
 * the sender always gets a summary, and affected players other than the
 * sender get a short notification.
 */
public class FlyExecutor {

    private final CommandContext ctx;

    public FlyExecutor(CommandContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Sets fly mode for the targets.
     *
     * @param source the command source
     * @param enabled whether flight is allowed
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}
     */
    public int setFly(CommandSourceStack source, boolean enabled, List<Player> targets) {
        return applyFly(source, enabled, targets);
    }

    /**
     * Toggles fly mode for the targets as a group: off when everyone can
     * already fly, otherwise on for all of them.
     *
     * @param source the command source
     * @param targets the resolved target players
     * @return {@link Command#SINGLE_SUCCESS}
     */
    public int toggleFly(CommandSourceStack source, List<Player> targets) {
        boolean turnOn = targets.stream().anyMatch(target -> !target.getAllowFlight());
        return applyFly(source, turnOn, targets);
    }

    private int applyFly(CommandSourceStack source, boolean enabled, List<Player> targets) {
        for (Player target : targets) {
            target.setAllowFlight(enabled);
            if (!enabled) {
                target.setFlying(false);
            }
        }
        sendFeedback(source, targets, enabled ? "on" : "off");
        return Command.SINGLE_SUCCESS;
    }

    private void sendFeedback(CommandSourceStack source, List<Player> targets, String mode) {
        if (isSelfOnly(source, targets)) {
            ctx.sendSet(source, m -> m.getFly().getSetSelf(), Map.of("mode", mode));
            return;
        }
        var targetsValue = targetNames(targets);
        if (targets.size() == 1) {
            var tags = Map.of("mode", mode, "targets", targetsValue);
            ctx.sendSet(source, m -> m.getFly().getSetOther(), tags);
        } else {
            var tags = Map.of(
                    "mode", mode,
                    "targets", targetsValue,
                    "count", String.valueOf(targets.size()));
            ctx.sendSet(source, m -> m.getFly().getSetMultiple(), tags);
        }
        for (Player target : targets) {
            if (!isSender(source, target)) {
                ctx.notifySet(target, m -> m.getFly().getSetTarget(), Map.of("mode", mode));
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
