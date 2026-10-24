package com.jruk8.jtemplate.core.commands.speed;

import com.jruk8.jtemplate.core.configs.PluginConfig;
import org.bukkit.entity.Player;

/**
 * Applies fly/walk speeds to players through the Bukkit API.
 * <p>
 * Speeds are intentionally not persisted: they live on the {@link Player} and
 * reset to the configured defaults when the server restarts or the player
 * rejoins, exactly like vanilla behavior.
 */
public class SpeedService {

    private final PluginConfig pluginConfig;

    public SpeedService(PluginConfig pluginConfig) {
        this.pluginConfig = pluginConfig;
    }

    /**
     * Sets one speed channel to a user-facing scale value.
     *
     * @param player the player to change
     * @param type which speed channel to set
     * @param scale the user-facing value (0 to 10)
     */
    public void setSpeed(Player player, SpeedType type, double scale) {
        apply(player, type, SpeedValues.toBukkit(scale));
    }

    /**
     * Resets one speed channel to its configured default.
     *
     * @param player the player to change
     * @param type which speed channel to reset
     * @return the default that was applied, on the user-facing scale
     */
    public double resetSpeed(Player player, SpeedType type) {
        double def = defaultScale(type);
        apply(player, type, SpeedValues.toBukkit(def));
        return def;
    }

    /**
     * Returns the configured default for a speed channel, clamped to 0-10.
     *
     * @param type which speed channel
     * @return the default on the user-facing scale
     */
    public double defaultScale(SpeedType type) {
        double raw = type == SpeedType.FLY
                ? pluginConfig.getSpeed().getDefaultFlySpeed()
                : pluginConfig.getSpeed().getDefaultWalkSpeed();
        return SpeedValues.clampScale(raw);
    }

    private void apply(Player player, SpeedType type, float bukkit) {
        if (type == SpeedType.FLY) {
            player.setFlySpeed(bukkit);
        } else {
            player.setWalkSpeed(bukkit);
        }
    }
}
