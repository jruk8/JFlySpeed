package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.commands.CommandContext;
import com.jruk8.jtemplate.core.commands.SpeedPermissions;
import com.jruk8.jtemplate.core.placeholders.CommandUsageTracker;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

/**
 * Builds the shared {@code <value>|reset [targets]} argument tails used by
 * the flyspeed, walkspeed and speed commands.
 */
public final class SpeedArgumentNodes {

    private SpeedArgumentNodes() {
    }

    /**
     * Builds the {@code <value> [targets]} branch that sets one speed channel.
     * <p>
     * The value is parsed as a string so non-numeric input gets the
     * plugin's own invalid message instead of Brigadier's default error.
     *
     * @param type which speed channel to set
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @param tracker the usage tracker
     * @param suggestions the configured value completion defaults
     * @return the value argument node with an optional targets child
     */
    public static RequiredArgumentBuilder<CommandSourceStack, String> valueArgument(
            SpeedType type,
            CommandContext ctx,
            SpeedExecutor exec,
            CommandUsageTracker tracker,
            List<Double> suggestions
    ) {
        return valueNode(
                (source, scale, targets) -> {
                    trackUse(source, type, tracker);
                    return exec.setSpeed(source, type, scale, targets);
                },
                ctx,
                suggestions);
    }

    /**
     * Builds the {@code <value> [targets]} branch that sets both speed
     * channels at once, so it requires both the fly and walk permissions.
     *
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @param tracker the usage tracker
     * @param suggestions the configured value completion defaults
     * @return the value argument node with an optional targets child
     */
    public static RequiredArgumentBuilder<CommandSourceStack, String> bothValueArgument(
            CommandContext ctx,
            SpeedExecutor exec,
            CommandUsageTracker tracker,
            List<Double> suggestions
    ) {
        return valueNode(
                (source, scale, targets) -> {
                    trackBothUse(source, tracker);
                    return exec.setBoth(source, scale, targets);
                },
                ctx,
                suggestions)
                .requires(CommandContext.requiresPermission(SpeedPermissions.FLY, SpeedPermissions.WALK));
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> valueNode(
            ValueAction action,
            CommandContext ctx,
            List<Double> suggestions
    ) {
        return Commands.argument("value", StringArgumentType.word())
                .suggests(valueSuggestions(suggestions))
                .executes(context -> {
                    Double scale = parsedScale(context.getSource(), getValue(context), ctx);
                    if (scale == null) {
                        return 0;
                    }
                    var targets = TargetSelector.self(context.getSource(), ctx, m -> m.getSpeed().getConsoleNeedsTarget());
                    return targets.map(list -> action.apply(context.getSource(), scale, list)).orElse(0);
                })
                .then(Commands.argument("targets", ArgumentTypes.players())
                        .suggests(targetSuggestions())
                        .executes(context -> {
                            Double scale = parsedScale(context.getSource(), getValue(context), ctx);
                            if (scale == null) {
                                return 0;
                            }
                            var selector = context.getArgument("targets", PlayerSelectorArgumentResolver.class);
                            var targets = TargetSelector.fromSelector(context.getSource(), selector, ctx);
                            return targets.map(list -> action.apply(context.getSource(), scale, list)).orElse(0);
                        }));
    }

    /**
     * Resets one speed channel for the executor themselves.
     *
     * @param type which speed channel to reset
     * @param source the command source
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @param tracker the usage tracker
     * @return {@link com.mojang.brigadier.Command#SINGLE_SUCCESS} or 0 on failure
     */
    public static int resetSelf(
            SpeedType type,
            CommandSourceStack source,
            CommandContext ctx,
            SpeedExecutor exec,
            CommandUsageTracker tracker
    ) {
        return TargetSelector.self(source, ctx, m -> m.getSpeed().getConsoleNeedsTarget())
                .map(list -> {
                    trackUse(source, type, tracker);
                    return exec.resetSpeed(source, type, list);
                })
                .orElse(0);
    }

    /**
     * Resets the executor's own speeds for every channel they hold the
     * permission for: both when permitted (the both-reset message), otherwise
     * just the one permitted channel.
     *
     * @param source the command source
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @param tracker the usage tracker
     * @return {@link com.mojang.brigadier.Command#SINGLE_SUCCESS} or 0 on failure
     */
    public static int resetPermittedSelf(
            CommandSourceStack source,
            CommandContext ctx,
            SpeedExecutor exec,
            CommandUsageTracker tracker
    ) {
        boolean fly = source.getSender().hasPermission(SpeedPermissions.FLY);
        boolean walk = source.getSender().hasPermission(SpeedPermissions.WALK);
        return TargetSelector.self(source, ctx, m -> m.getSpeed().getConsoleNeedsTarget())
                .map(list -> {
                    if (fly && walk) {
                        trackBothUse(source, tracker);
                        return exec.resetBoth(source, list);
                    }
                    SpeedType type = fly ? SpeedType.FLY : SpeedType.WALK;
                    trackUse(source, type, tracker);
                    return exec.resetSpeed(source, type, list);
                })
                .orElse(0);
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
            SpeedExecutor exec,
            CommandUsageTracker tracker
    ) {
        return Commands.literal("reset")
                .executes(context -> resetSelf(type, context.getSource(), ctx, exec, tracker))
                .then(targetsArgument((source, targets) -> {
                    trackUse(source, type, tracker);
                    return exec.resetSpeed(source, type, targets);
                }, ctx));
    }

    /**
     * Builds the {@code reset [targets]} branch that resets both speed channels.
     *
     * @param ctx the shared command services
     * @param exec the executor applying the change
     * @return the reset node with an optional targets child
     */
    public static LiteralArgumentBuilder<CommandSourceStack> resetBothLiteral(
            CommandContext ctx,
            SpeedExecutor exec,
            CommandUsageTracker tracker
    ) {
        return Commands.literal("reset")
                .executes(context -> TargetSelector.self(context.getSource(), ctx, m -> m.getSpeed().getConsoleNeedsTarget())
                        .map(list -> {
                            trackBothUse(context.getSource(), tracker);
                            return exec.resetBoth(context.getSource(), list);
                        })
                        .orElse(0))
                .then(targetsArgument((source, targets) -> {
                    trackBothUse(source, tracker);
                    return exec.resetBoth(source, targets);
                }, ctx));
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

    private static SuggestionProvider<CommandSourceStack> valueSuggestions(List<Double> configured) {
        return (context, builder) -> {
            for (String candidate : ValueSuggestions.suggest(configured, builder.getRemainingLowerCase())) {
                builder.suggest(candidate);
            }
            return builder.buildFuture();
        };
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

    private static void trackUse(CommandSourceStack source, SpeedType type, CommandUsageTracker tracker) {
        if (source.getSender() instanceof Player player) {
            tracker.recordUse(player.getUniqueId(), type);
        }
    }

    private static void trackBothUse(CommandSourceStack source, CommandUsageTracker tracker) {
        if (source.getSender() instanceof Player player) {
            tracker.recordBothUse(player.getUniqueId());
        }
    }

    private static String getValue(com.mojang.brigadier.context.CommandContext<CommandSourceStack> context) {
        return StringArgumentType.getString(context, "value");
    }

    @FunctionalInterface
    private interface ValueAction {
        int apply(CommandSourceStack source, double scale, List<Player> targets);
    }

    private static Double parsedScale(CommandSourceStack source, String input, CommandContext ctx) {
        double scale;
        try {
            scale = Double.parseDouble(input);
        } catch (NumberFormatException invalid) {
            ctx.error(source, m -> m.getSpeed().getInvalid(), Map.of("input", input));
            return null;
        }
        if (!Double.isFinite(scale)) {
            ctx.error(source, m -> m.getSpeed().getInvalid(), Map.of("input", input));
            return null;
        }
        return SpeedValues.clampScale(scale);
    }
}
