package com.jruk8.jtemplate.core.sounds;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

/**
 * Handles the resolution and playback of namespaced Bukkit sounds.
 * <p>
 * Acts as a bridge between {@link SoundsConfig.Sound} definitions and
 * Bukkit's sound registry/playback API. Unknown or invalid sound keys
 * are logged and skipped rather than throwing.
 */
public final class SoundPlayer {

    private final Logger logger;

    /**
     * Constructs a new {@code SoundPlayer} with the given logger.
     *
     * @param logger the logger used to report unresolvable sound keys
     */
    public SoundPlayer(Logger logger) {
        this.logger = logger;
    }

    /**
     * Plays the given sound to a recipient at their own location, if enabled.
     * <p>
     * Sounds require a location to play; senders that are not a {@link Player}
     * (e.g. console) are silently ignored.
     *
     * @param sender the recipient of the sound; if null or not a {@link Player}, the operation is ignored
     * @param sound  the sound definition to play; if null or disabled, the operation is ignored
     */
    public void play(CommandSender sender, SoundsConfig.Sound sound) {
        if (!(sender instanceof Player player)) {
            return;
        }
        play(player, sound);
    }

    /**
     * Plays the given sound to a player at their own location, if enabled.
     *
     * @param player the recipient of the sound; if null, operation is ignored
     * @param sound  the sound definition to play; if null or disabled, operation is ignored
     */
    public void play(Player player, SoundsConfig.Sound sound) {
        if (player == null || sound == null || !sound.isEnabled()) {
            return;
        }

        org.bukkit.Sound bukkitSound = resolveSound(sound.getSound());
        if (bukkitSound == null) {
            return;
        }

        player.playSound(player.getLocation(), bukkitSound, sound.getVolume(), sound.getPitch());
    }

    /**
     * Plays the given sound to every online player, at each player's own location, if enabled.
     *
     * @param sound the sound definition to play; if null or disabled, operation is ignored
     */
    public void broadcast(SoundsConfig.Sound sound) {
        if (sound == null || !sound.isEnabled()) {
            return;
        }

        org.bukkit.Sound bukkitSound = resolveSound(sound.getSound());
        if (bukkitSound == null) {
            return;
        }

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), bukkitSound, sound.getVolume(), sound.getPitch());
        }
    }

    /**
     * Resolves a raw namespaced sound key (e.g. {@code "block.note_block.pling"} or
     * {@code "minecraft:block.note_block.pling"}) against Bukkit's sound registry.
     *
     * @param key the raw sound key from a {@link SoundsConfig.Sound} definition
     * @return the resolved {@link org.bukkit.Sound}, or {@code null} if the key is invalid or unknown
     */
    private org.bukkit.Sound resolveSound(String key) {
        if (key == null || key.isBlank()) {
            logger.warning("Sound key is null or empty.");
            return null;
        }

        NamespacedKey namespacedKey = NamespacedKey.fromString(key.toLowerCase());
        if (namespacedKey == null) {
            logger.warning("Invalid sound key: '" + key + "'");
            return null;
        }

        org.bukkit.Sound bukkitSound = Registry.SOUNDS.get(namespacedKey);
        if (bukkitSound == null) {
            logger.warning("Unknown sound key: '" + key + "'");
        }

        return bukkitSound;
    }
}