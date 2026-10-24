package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Builds the shared {@code <value>|reset [targets]} argument tails used by
 * the flyspeed, walkspeed and speed commands.
 */
public final class SpeedArgumentNodes {

    private SpeedArgumentNodes() {
    }

    /**
     * Builds the {@code <value> [targets]} branch that sets one speed channel.
     *
     * @param type which speed channel to set
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @return the value argument node with an optional targets child
     */
    public static RequiredArgumentBuilder<CommandSourceStack, Double> valueArgument(
            SpeedType type,
            CommandContext ctx,
            SpeedExecutor exec
    ) {
        return Commands.argument("value", DoubleArgumentType.doubleArg())
                .executes(context -> {
                    Double scale = validatedScale(context.getSource(), getValue(context), ctx);
                    if (scale == null) {
                        return 0;
                    }
                    var targets = TargetSelector.self(context.getSource(), ctx);
                    return targets.map(list -> exec.setSpeed(context.getSource(), type, scale, list)).orElse(0);
                })
                .then(Commands.argument("targets", ArgumentTypes.players())
                        .suggests(targetSuggestions())
                        .executes(context -> {
                            Double scale = validatedScale(context.getSource(), getValue(context), ctx);
                            if (scale == null) {
                                return 0;
                            }
                            var selector = context.getArgument("targets", PlayerSelectorArgumentResolver.class);
                            var targets = TargetSelector.fromSelector(context.getSource(), selector, ctx);
                            return targets.map(list -> exec.setSpeed(context.getSource(), type, scale, list))
                                    .orElse(0);
                        }));
    }

    /**
     * Builds the {@code reset [targets]} branch that resets one speed channel.
     *
     * @param type which speed channel to reset
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @return the reset node with an optional targets child
     */
    public static LiteralArgumentBuilder<CommandSourceStack> resetLiteral(
            SpeedType type,
            CommandContext ctx,
            SpeedExecutor exec
    ) {
        return Commands.literal("reset")
                .executes(context -> TargetSelector.self(context.getSource(), ctx)
                        .map(list -> exec.resetSpeed(context.getSource(), type, list))
                        .orElse(0))
                .then(targetsArgument((source, targets) -> exec.resetSpeed(source, type, targets), ctx));
    }

    /**
     * Builds the {@code reset [targets]} branch that resets both speed channels.
     *
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @return the reset node with an optional targets child
     */
    public static LiteralArgumentBuilder<CommandSourceStack> resetBothLiteral(CommandContext ctx, SpeedExecutor exec) {
        return Commands.literal("reset")
                .executes(context -> TargetSelector.self(context.getSource(), ctx)
                        .map(list -> exec.resetBoth(context.getSource(), list))
                        .orElse(0))
                .then(targetsArgument((source, targets) -> exec.resetBoth(source, targets), ctx));
    }

    /**
     * Builds the optional {@code [targets]} selector child with custom suggestions.
     *
     * @param action what to run against the resolved targets
     * @param ctx the shared command services
     * @return the targets argument node
     */
    public static RequiredArgumentBuilder<CommandSourceStack, PlayerSelectorArgumentResolver> targetsArgument(
            TargetedAction action,
            CommandContext ctx
    ) {
        return Commands.argument("targets", ArgumentTypes.players())
                .suggests(targetSuggestions())
                .executes(context -> {
                    var selector = context.getArgument("targets", PlayerSelectorArgumentResolver.class);
                    var targets = TargetSelector.fromSelector(context.getSource(), selector, ctx);
                    return targets.map(list -> action.run(context.getSource(), list)).orElse(0);
                });
    }

    private static SuggestionProvider<CommandSourceStack> targetSuggestions() {
        return (context, builder) -> {
            var source = context.getSource();
            boolean canTargetOthers = source.getSender().hasPermission(SpeedPermissions.OTHER);
            String executor = source.getExecutor() instanceof Player player ? player.getName() : "";
            var online = Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
            var prefix = builder.getRemainingLowerCase();
            for (String candidate : SelectorSuggestions.suggest(canTargetOthers, executor, online, prefix)) {
                builder.suggest(candidate);
            }
            return builder.buildFuture();
        };
    }

    private static double getValue(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
        return DoubleArgumentType.getDouble(context, "value");
    }

    private static Double validatedScale(CommandSourceStack source, double scale, CommandContext ctx) {
        if (SpeedValues.isValid(scale)) {
            return scale;
        }
        ctx.error(source,
                m -> m.getSpeed().getInvalidSpeed(),
                Placeholder.parsed("input", SpeedValues.format(scale)));
        return null;
    }
}
