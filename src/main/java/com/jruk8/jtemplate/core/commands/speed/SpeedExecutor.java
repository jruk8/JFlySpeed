package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.mojang.brigadier.Command;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;
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
            ctx.success(source, selfMessage(reset),
                    Placeholder.parsed("type", type.getKey()),
                    Placeholder.parsed("value", value));
            return;
        }
        String names = targets.stream().map(Player::getName).collect(Collectors.joining(", "));
        ctx.success(source, senderMessage(reset),
                Placeholder.parsed("type", type.getKey()),
                Placeholder.parsed("value", value),
                Placeholder.parsed("targets", names));
        for (Player target : targets) {
            if (!isSender(source, target)) {
                ctx.notify(target, targetMessage(reset),
                        Placeholder.parsed("type", type.getKey()),
                        Placeholder.parsed("value", value));
            }
        }
    }

    private boolean isSelfOnly(CommandSourceStack source, List<Player> targets) {
        return targets.size() == 1 && isSender(source, targets.getFirst());
    }

    private boolean isSender(CommandSourceStack source, Player target) {
        return source.getSender() instanceof Player self && self.getUniqueId().equals(target.getUniqueId());
    }

    private Function<MessagesConfig, String> selfMessage(boolean reset) {
        return reset ? m -> m.getSpeed().getResetSelf() : m -> m.getSpeed().getSetSelf();
    }

    private Function<MessagesConfig, String> senderMessage(boolean reset) {
        return reset ? m -> m.getSpeed().getResetSender() : m -> m.getSpeed().getSetSender();
    }

    private Function<MessagesConfig, String> targetMessage(boolean reset) {
        return reset ? m -> m.getSpeed().getResetTarget() : m -> m.getSpeed().getSetTarget();
    }
}
