package com.jruk8.jtemplate.core.commands;

import com.jruk8.jtemplate.core.messages.MessagesConfig;
import com.jruk8.jtemplate.core.messages.Messenger;
import com.jruk8.jtemplate.core.sounds.SoundPlayer;
import com.jruk8.jtemplate.core.sounds.SoundsConfig;
import java.util.function.Function;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.incendo.cloud.paper.util.sender.Source;

/**
 * Bundles the cross-cutting services almost every command class needs for user-facing feedback: config-driven
 * message/sound definitions, plus the dispatchers that actually send/play them.
 * <p>
 * Command classes take a single {@code CommandContext} instead of listing {@code messages}, {@code sounds},
 * {@code messenger}, and {@code soundPlayer} individually — adding a new shared service only requires updating this
 * record and its single construction site in {@link CommandsRegistrar}, not every command class's constructor.
 */
public record CommandContext(
        MessagesConfig messages,
        SoundsConfig sounds,
        Messenger messenger,
        SoundPlayer soundPlayer
) {

    /**
     * Convenience method for sending a message resolved from config and playing a success sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void success(
            Source sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.source(), messageSelector.apply(messages), resolvers);
        playSuccess(sender);
    }

    /**
     * Convenience method for sending a message and playing a success sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param rawMessage The raw message to send.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void success(Source sender, String rawMessage, TagResolver... resolvers) {
        messenger.send(sender.source(), rawMessage, resolvers);
        playSuccess(sender);
    }

    /**
     * Convenience method for sending a message resolved from config and playing an error sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void error(
            Source sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.source(), messageSelector.apply(messages), resolvers);
        playError(sender);
    }

    /**
     * Convenience method for sending a message and playing an error sound.
     * @param sender The command sender to send the message to and play the sound for.
     * @param rawMessage The raw message to send.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void error(Source sender, String rawMessage, TagResolver... resolvers) {
        messenger.send(sender.source(), rawMessage, resolvers);
        playError(sender);
    }

    /**
     * Convenience method for sending a message resolved from config without playing a sound.
     * @param sender The command sender to send the message to.
     * @param messageSelector Function to select the message string from {@link MessagesConfig}.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendMessage(
            Source sender,
            Function<MessagesConfig, String> messageSelector,
            TagResolver... resolvers
    ) {
        messenger.send(sender.source(), messageSelector.apply(messages), resolvers);
    }

    /**
     * Convenience method for sending a message without playing a sound.
     * @param sender The command sender to send the message to.
     * @param rawMessage The raw message to send.
     * @param resolvers The placeholder resolvers for the message.
     */
    public void sendMessage(Source sender, String rawMessage, TagResolver... resolvers) {
        messenger.send(sender.source(), rawMessage, resolvers);
    }

    public void playSuccess(Source sender) {
        soundPlayer.play(sender.source(), sounds.getSuccessSound());
    }

    public void playError(Source sender) {
        soundPlayer.play(sender.source(), sounds.getErrorSound());
    }
}