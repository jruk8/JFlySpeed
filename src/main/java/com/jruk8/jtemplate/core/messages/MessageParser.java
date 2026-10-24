package com.jruk8.jtemplate.core.messages;

import com.jruk8.jtemplate.core.configs.PluginConfig;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;

import java.util.Map;

/**
 * Handles the raw-string -> Component pipeline:
 * 1. Internal {bracket} placeholders ({prefix}, {time}, etc.)
 * 2. Color palette placeholders ({plain}, ...) from config.yml
 * 3. PlaceholderAPI %percent% placeholders (%player_name%, etc.)
 * 4. Adventure TagResolvers
 * 5. MiniMessage formatting
 */
public final class MessageParser {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

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

        // 3. Resolve the config.yml color palette (after values, so composed
        // text passed through placeholders picks the colors up too)
        text = resolveColors(text);

        // 4. Process PlaceholderAPI %placeholders%
        if (isPapiEnabled()) {
            text = PlaceholderAPI.setPlaceholders(null, text);
        }

        // 5. Build final Component: MiniMessage parses the string AND applies
        // any extra TagResolvers passed in
        return MINI_MESSAGE.deserialize(text, resolvers);
    }

    private String resolveColors(String text) {
        var colors = pluginConfig.getColors();
        if (colors == null) {
            return text;
        }
        return text
                .replace("{plain}", colors.getPlainColor())
                .replace("{brand}", colors.getBrandColor())
                .replace("{hl}", colors.getHighlightColor())
                .replace("{error}", colors.getErrorColor());
    }

    private boolean isPapiEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI");
    }
}