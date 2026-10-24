package com.jruk8.jtemplate.core.commands.speed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpeedValuesTest {

    private static final double DELTA = 1e-6;

    @Nested
    @DisplayName("toBukkit() / toScale()")
    class ConversionTests {

        @Test
        @DisplayName("should map fly 0, 1 and 10 to Bukkit 0, 0.1 and 1")
        void shouldMapFlyAnchorPoints() {
            assertEquals(0.0f, SpeedValues.toBukkit(SpeedType.FLY, 0.0), DELTA);
            assertEquals(0.1f, SpeedValues.toBukkit(SpeedType.FLY, 1.0), DELTA);
            assertEquals(1.0f, SpeedValues.toBukkit(SpeedType.FLY, 10.0), DELTA);
        }

        @Test
        @DisplayName("should map walk 0, 1 and 10 to Bukkit 0, 0.2 and 1")
        void shouldMapWalkAnchorPoints() {
            assertEquals(0.0f, SpeedValues.toBukkit(SpeedType.WALK, 0.0), DELTA);
            assertEquals(0.2f, SpeedValues.toBukkit(SpeedType.WALK, 1.0), DELTA);
            assertEquals(1.0f, SpeedValues.toBukkit(SpeedType.WALK, 10.0), DELTA);
        }

        @Test
        @DisplayName("should interpolate both segments of each channel")
        void shouldInterpolateSegments() {
            assertEquals(0.05f, SpeedValues.toBukkit(SpeedType.FLY, 0.5), DELTA);
            assertEquals(0.55f, SpeedValues.toBukkit(SpeedType.FLY, 5.5), DELTA);
            assertEquals(0.1f, SpeedValues.toBukkit(SpeedType.WALK, 0.5), DELTA);
            assertEquals(0.6f, SpeedValues.toBukkit(SpeedType.WALK, 5.5), DELTA);
        }

        @Test
        @DisplayName("should map the vanilla defaults to Bukkit 0.1 and 0.2")
        void shouldMapVanillaDefaults() {
            assertEquals(0.1f, SpeedValues.toBukkit(SpeedType.FLY, SpeedValues.DEFAULT_FLY_SCALE), DELTA);
            assertEquals(0.2f, SpeedValues.toBukkit(SpeedType.WALK, SpeedValues.DEFAULT_WALK_SCALE), DELTA);
        }

        @Test
        @DisplayName("should round-trip through both directions per channel")
        void shouldRoundTrip() {
            assertEquals(7.25, SpeedValues.toScale(SpeedType.FLY, SpeedValues.toBukkit(SpeedType.FLY, 7.25)), DELTA);
            assertEquals(7.25, SpeedValues.toScale(SpeedType.WALK, SpeedValues.toBukkit(SpeedType.WALK, 7.25)), DELTA);
            assertEquals(0.25, SpeedValues.toScale(SpeedType.FLY, SpeedValues.toBukkit(SpeedType.FLY, 0.25)), DELTA);
            assertEquals(0.75, SpeedValues.toScale(SpeedType.WALK, SpeedValues.toBukkit(SpeedType.WALK, 0.75)), DELTA);
        }
    }

    @Nested
    @DisplayName("clampScale() / format()")
    class HelpersTests {

        @Test
        @DisplayName("should clamp outside values into range")
        void shouldClamp() {
            assertEquals(0.0, SpeedValues.clampScale(-3.0));
            assertEquals(10.0, SpeedValues.clampScale(42.0));
            assertEquals(5.5, SpeedValues.clampScale(5.5));
        }

        @Test
        @DisplayName("should drop the decimal part of whole numbers")
        void shouldFormatWholeNumbersCompactly() {
            assertEquals("5", SpeedValues.format(5.0));
            assertEquals("5.5", SpeedValues.format(5.5));
        }

        @Test
        @DisplayName("should round previous values to one decimal for the was-suffix")
        void shouldFormatWasToOneDecimal() {
            assertEquals("5", SpeedValues.formatWas(5.04));
            assertEquals("5.1", SpeedValues.formatWas(5.05));
            assertEquals("10", SpeedValues.formatWas(9.99));
            assertEquals("0", SpeedValues.formatWas(0.04));
        }
    }
}
