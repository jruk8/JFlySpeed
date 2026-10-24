package com.jruk8.jtemplate.core.commands.speed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValueSuggestionsTest {

    @Test
    @DisplayName("should keep the configured order and drop decimals of whole numbers")
    void shouldKeepOrderAndFormatWholeNumbers() {
        assertEquals(
                List.of("1", "2", "5", "10"),
                ValueSuggestions.suggest(List.of(1.0, 2.0, 5.0, 10.0), ""));
    }

    @Test
    @DisplayName("should filter candidates by the typed prefix")
    void shouldFilterByPrefix() {
        var configured = List.of(1.0, 2.0, 5.0, 10.0);

        assertEquals(List.of("1", "10"), ValueSuggestions.suggest(configured, "1"));
        assertEquals(List.of("2"), ValueSuggestions.suggest(configured, "2"));
        assertEquals(List.of(), ValueSuggestions.suggest(configured, "x"));
    }

    @Test
    @DisplayName("should suggest everything when the prefix is null")
    void shouldSuggestAllOnNullPrefix() {
        assertEquals(
                List.of("1", "2"),
                ValueSuggestions.suggest(List.of(1.0, 2.0), null));
    }

    @Test
    @DisplayName("should drop null and non-finite entries")
    void shouldDropInvalidEntries() {
        assertEquals(
                List.of("1"),
                ValueSuggestions.suggest(Arrays.asList(1.0, null, Double.NaN, Double.POSITIVE_INFINITY), ""));
    }

    @Test
    @DisplayName("should clamp out-of-range entries into 0-10")
    void shouldClampOutOfRangeEntries() {
        assertEquals(
                List.of("10", "0"),
                ValueSuggestions.suggest(List.of(25.0, -3.0), ""));
    }

    @Test
    @DisplayName("should keep fractions and drop duplicates after formatting")
    void shouldKeepFractionsAndDedupe() {
        assertEquals(
                List.of("2.5", "1"),
                ValueSuggestions.suggest(List.of(2.5, 1.0, 1.0), ""));
    }
}
