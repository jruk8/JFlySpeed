package com.jruk8.jtemplate.core.commands.speed;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Builds the tab-completion defaults for the speed commands' value argument.
 * <p>
 * The configured order is kept; non-finite entries are dropped, the rest are
 * clamped to 0-10 and whole numbers display without decimals. Candidates are
 * always prefix-filtered.
 */
public final class ValueSuggestions {

    private ValueSuggestions() {
    }

    /**
     * Returns the completion candidates for a speed value argument.
     *
     * @param configured the configured defaults, in suggestion order
     * @param prefix the already-typed, lower-cased input used for filtering (may be null)
     * @return the filtered candidates, formatted for chat
     */
    public static List<String> suggest(List<Double> configured, String prefix) {
        String needle = prefix == null ? "" : prefix.toLowerCase(Locale.ROOT);
        return configured.stream()
                .filter(Objects::nonNull)
                .filter(Double::isFinite)
                .map(SpeedValues::clampScale)
                .map(SpeedValues::format)
                .distinct()
                .filter(candidate -> candidate.toLowerCase(Locale.ROOT).startsWith(needle))
                .toList();
    }
}
