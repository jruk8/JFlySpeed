package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.jruk8.jtemplate.core.messages.Messenger;
import com.jruk8.jtemplate.core.sounds.SoundPlayer;
import com.jruk8.jtemplate.core.sounds.SoundsConfig;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import java.util.function.Function;
import java.util.function.Predicate;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;

/**
 * Bundles the cross-cutting services almost every command class needs for user-facing feedback: config-driven
 * message/sound definitions, plus the dispatchers that actually send/play them.
 * <p>
 * Command classes take a single {@code CommandContext} instead of listing {@code messages}, {@code sounds},
 * {@code messenger}, and {@code soundPlayer} individually — adding a new shared service only requires updating this
 * record and its single construction site in {@code JTemplatePlugin}, not every command class's constructor.
 */
public record CommandContext(
        MessagesConfig messages,
        SoundsConfig sounds,
        Messenger messenger,
        SoundPlayer soundPlayer
) {

    /**
     * Builds a Brigadier {@code requires} predicate that passes only when the sender holds every listed permission.
     *
     * @param permissions the permission nodes the sender must hold
     * @return a predicate over the command source stack
     */
    public static Predicate<CommandSourceStack> requiresPermission(String... permissions) {
        return source -> {
            for (String permission : permissions) {
                if (!source.getSender().hasPermission(permission)) {
                    return false;
                }
            }
            return true;
        };
    }

    /**
     * Builds a Brigadier {@code requires} predicate that passes when the sender holds any listed permission.
     *
     * @param permissions the permission nodes, one of which the sender must hold
     * @return a predicate over the command source stack
     */
    public static Predicate<CommandSourceStack> requiresAnyPermission(String... permissions) {
        return source -> {
            for (String permission : permissions) {
                if (source.getSender().hasPermission(permission)) {
                    return true;
                }
            }
            return false;
        };
    }

    /**
     * Sends a set-speed message to the executor and plays the neutral sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendSet(
            CommandSourceStack sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.getSender(), messageSelector.apply(messages), resolvers);
        soundPlayer.play(sender.getSender(), sounds.getNeutral());
    }

    /**
     * Sends a set-speed message to an affected player and plays the neutral sound.
     * @param target The recipient of the message and sound.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void notifySet(
            CommandSender target,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(target, messageSelector.apply(messages), resolvers);
        soundPlayer.play(target, sounds.getNeutral());
    }

    /**
     * Sends a reset-speed message to the executor and plays the reset sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendReset(
            CommandSourceStack sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.getSender(), messageSelector.apply(messages), resolvers);
        soundPlayer.play(sender.getSender(), sounds.getReset());
    }

    /**
     * Sends a reset-speed message to an affected player and plays the reset sound.
     * @param target The recipient of the message and sound.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void notifyReset(
            CommandSender target,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(target, messageSelector.apply(messages), resolvers);
        soundPlayer.play(target, sounds.getReset());
    }

    /**
     * Sends a reload message to the executor and plays the reload sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendReload(
            CommandSourceStack sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.getSender(), messageSelector.apply(messages), resolvers);
        soundPlayer.play(sender.getSender(), sounds.getReload());
    }

    /**
     * Sends a version message to the executor and plays the neutral sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendVersion(
            CommandSourceStack sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.getSender(), messageSelector.apply(messages), resolvers);
        soundPlayer.play(sender.getSender(), sounds.getNeutral());
    }

    /**
     * Plays the help sound for the executor.
     * @param sender The command sender to play the sound for.
     */
    public void playHelp(CommandSourceStack sender) {
        soundPlayer.play(sender.getSender(), sounds.getHelp());
    }

    /**
     * Convenience method for sending a message resolved from config and playing an error sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void error(
            CommandSourceStack sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.getSender(), messageSelector.apply(messages), resolvers);
        soundPlayer.play(sender.getSender(), sounds.getError());
    }

    /**
     * Convenience method for sending a message and playing an error sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param rawMessage The raw message to send.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void error(CommandSourceStack sender, String rawMessage, TagResolver... resolvers) {
        messenger.send(sender.getSender(), rawMessage, resolvers);
        soundPlayer.play(sender.getSender(), sounds.getError());
    }

    /**
     * Convenience method for sending a message resolved from config without playing a sound.
     * @param sender The command sender to send the message to.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendMessage(
            CommandSourceStack sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.getSender(), messageSelector.apply(messages), resolvers);
    }

    /**
     * Convenience method for sending a message without playing a sound.
     * @param sender The command sender to send the message to.
     * @param rawMessage The raw message to send.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendMessage(CommandSourceStack sender, String rawMessage, TagResolver... resolvers) {
        messenger.send(sender.getSender(), rawMessage, resolvers);
    }

}
