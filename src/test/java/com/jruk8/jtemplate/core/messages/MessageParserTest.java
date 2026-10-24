package com.jruk8.jtemplate.core.messages;

import com.jruk8.jtemplate.core.configs.PluginConfig;
import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageParserTest {

    private static ServerMock server;

    private PluginConfig pluginConfig;
    private MessagesConfig messagesConfig;
    private MessageParser parser;
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    private static final PlainTextComponentSerializer PLAIN_TEXT = PlainTextComponentSerializer.plainText();

    @BeforeAll
    static void startServer() {
        server = MockBukkit.mock();
    }

    @AfterAll
    static void stopServer() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void setUp() {
        pluginConfig = new PluginConfig();
        messagesConfig = new MessagesConfig();
        parser = new MessageParser(pluginConfig, messagesConfig);
    }

    @AfterEach
    void tearDown() {
        // Reset config to defaults between tests
        pluginConfig.setMessageFormat(MessageFormat.MINIMESSAGE);
    }

    @Nested
    @DisplayName("parse() guard clauses")
    class GuardClauseTests {

        @Test
        @DisplayName("should return empty component for null input")
        void shouldReturnEmptyForNull() {
            Component result = parser.parse(null, Map.of());
            assertEquals(Component.text(""), result);
        }

        @Test
        @DisplayName("should return empty component for empty input")
        void shouldReturnEmptyForEmpty() {
            Component result = parser.parse("", Map.of());
            assertEquals(Component.text(""), result);
        }
    }

    @Nested
    @DisplayName("parse() prefix replacement")
    class PrefixReplacementTests {

        @Test
        @DisplayName("should replace {prefix} placeholder")
        void shouldReplacePrefixPlaceholder() {
            String raw = "{prefix}Hello";
            Component result = parser.parse(raw, Map.of());
            // The prefix from MessageConfig is "<gradient:#5e42f4:#b742f4>[JTemplate]</gradient> <gray>»</gray> "
            // After MiniMessage parsing, the result should contain the prefix text
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("[JTemplate]"));
        }
    }

    @Nested
    @DisplayName("parse() bracket placeholder normalization")
    class BracketPlaceholderTests {

        @Test
        @DisplayName("should replace placeholder with braces")
        void shouldReplacePlaceholderWithBraces() {
            String raw = "Hello {player}";
            Map<String, String> placeholders = Map.of("{player}", "Steve");
            Component result = parser.parse(raw, placeholders);
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Steve"));
        }

        @Test
        @DisplayName("should normalize placeholder without braces")
        void shouldNormalizePlaceholderWithoutBraces() {
            String raw = "Hello {player}";
            Map<String, String> placeholders = Map.of("player", "Steve");
            Component result = parser.parse(raw, placeholders);
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Steve"));
        }

        @Test
        @DisplayName("should handle multiple placeholders")
        void shouldHandleMultiplePlaceholders() {
            String raw = "{prefix}Hello {player}, you have {count} items";
            Map<String, String> placeholders = Map.of(
                "player", "Steve",
                "count", "5"
            );
            Component result = parser.parse(raw, placeholders);
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Steve"));
            assertTrue(resultString.contains("5"));
        }

        @Test
        @DisplayName("should handle null stringPlaceholders")
        void shouldHandleNullPlaceholders() {
            String raw = "Hello world";
            Component result = parser.parse(raw, null);
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Hello world"));
        }

        @Test
        @DisplayName("should handle empty stringPlaceholders")
        void shouldHandleEmptyPlaceholders() {
            String raw = "Hello world";
            Component result = parser.parse(raw, Map.of());
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Hello world"));
        }
    }

    @Nested
    @DisplayName("parse() format selection")
    class FormatSelectionTests {

        @Test
        @DisplayName("should use MiniMessage format by default")
        void shouldUseMiniMessageByDefault() {
            pluginConfig.setMessageFormat(MessageFormat.MINIMESSAGE);
            String raw = "<green>Hello</green>";
            Component result = parser.parse(raw, Map.of());
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Hello"));
        }

        @Test
        @DisplayName("should use Legacy format when configured")
        void shouldUseLegacyFormat() {
            pluginConfig.setMessageFormat(MessageFormat.LEGACY);
            String raw = "&aHello";
            Component result = parser.parse(raw, Map.of());
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("Hello"));
        }
    }

    @Nested
    @DisplayName("parse() with TagResolvers")
    class TagResolverTests {

        @Test
        @DisplayName("should apply MiniMessage tag resolvers")
        void shouldApplyTagResolvers() {
            String raw = "Time: <time>";
            TagResolver resolver = Placeholder.parsed("time", "100");
            Component result = parser.parse(raw, Map.of(), resolver);
            String resultString = PLAIN_TEXT.serialize(result);
            assertTrue(resultString.contains("100"));
        }
    }
}
