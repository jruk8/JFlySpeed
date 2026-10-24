package com.jruk8.jtemplate.core.commands.speed;

import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * An action applied to already-resolved speed command targets.
 */
@FunctionalInterface
public interface TargetedAction {

    /**
     * Runs the action against the resolved targets.
     *
     * @param source the command source
     * @param targets the resolved target players
     * @return the Brigadier result code
     */
    int run(CommandSourceStack source, List<Player> targets);
}
