package com.jruk8.jtemplate.core.placeholders;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.jruk8.jtemplate.core.placeholders.AuthorsCommandUsageListener.matches;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthorsCommandUsageListenerTest {

    private static final List<String> LABELS = List.of("jtemplate", "jt");
    private static final String COMMAND = "authors";

    @Nested
    @DisplayName("matches()")
    class MatchesTests {

        @Test
        @DisplayName("should match the primary label")
        void shouldMatchPrimaryLabel() {
            assertTrue(matches("/jtemplate authors", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should match the alias label")
        void shouldMatchAliasLabel() {
            assertTrue(matches("/jt authors", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should match case-insensitively")
        void shouldMatchCaseInsensitively() {
            assertTrue(matches("/JTEMPLATE AUTHORS", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should match the command with additional arguments")
        void shouldMatchWithArguments() {
            assertTrue(matches("/jtemplate authors extra arg", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should reject other subcommands")
        void shouldRejectOtherSubcommands() {
            assertFalse(matches("/jtemplate help", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should reject other plugins' commands")
        void shouldRejectForeignCommands() {
            assertFalse(matches("/warp spawn", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should reject bare root commands")
        void shouldRejectBareRootCommands() {
            assertFalse(matches("/jtemplate", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should reject messages without a slash")
        void shouldRejectNonCommandMessages() {
            assertFalse(matches("jtemplate authors", LABELS, COMMAND));
        }

        @Test
        @DisplayName("should reject null and empty messages")
        void shouldRejectNullAndEmptyMessages() {
            assertFalse(matches(null, LABELS, COMMAND));
            assertFalse(matches("", LABELS, COMMAND));
        }
    }
}