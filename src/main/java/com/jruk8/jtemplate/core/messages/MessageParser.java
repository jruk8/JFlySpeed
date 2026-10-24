package com.jruk8.jtemplate.core.messages;

import com.jruk8.jtemplate.core.configs.PluginConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

import java.util.Map;

/**
 * Handles the raw-string -> Component pipeline:
 * 1. Internal {bracket} placeholders ({prefix}, {time}, etc.)
 * 2. PlaceholderAPI %percent% placeholders (%player_name%, etc.)
 * 3. Adventure TagResolvers (for MiniMessage mode)
 * 4. MiniMessage or Legacy formatting
 */
public final class MessageParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    private final PluginConfig pluginConfig;
    private final MessagesConfig messagesConfig;

    public MessageParser(PluginConfig pluginConfig, MessagesConfig messagesConfig) {
        this.pluginConfig = pluginConfig;
        this.messagesConfig = messagesConfig;
    }

    public Component parse(String rawString, Map<String, String> stringPlaceholders, TagResolver... resolvers) {
        if (rawString == null || rawString.isEmpty()) {
            return Component.text("");
        }

        String text = rawString;

        // 1. Replace internal {prefix}
        text = text.replace("{prefix}", messagesConfig.getPrefix());

        // 2. Replace custom internal {bracket} string placeholders
        if (stringPlaceholders != null && !stringPlaceholders.isEmpty()) {
            for (Map.Entry<String, String> entry : stringPlaceholders.entrySet()) {
                String key = entry.getKey();
                if (!key.startsWith("{") || !key.endsWith("}")) {
                    key = "{" + key + "}";
                }
                text = text.replace(key, entry.getValue());
            }
        }

        // 3. Process PlaceholderAPI %placeholders%
        if (isPapiEnabled()) {
            text = PlaceholderAPI.setPlaceholders(null, text);
        }

        // 4. Build final Component
        if (pluginConfig.getMessageFormat() == MessageFormat.LEGACY) {
            return LEGACY_SERIALIZER.deserialize(text);
        } else {
            // MiniMessage parses the string AND applies any extra TagResolvers passed in
            return MINI_MESSAGE.deserialize(text, resolvers);
        }
    }

    private boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}