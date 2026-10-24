package com.jruk8.jtemplate.core.commands.speed;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SpeedValuesTest {

    private static final double DELTA = 1e-6;

    @Nested
    @DisplayName("isValid()")
    class ValidityTests {

        @Test
        @DisplayName("should accept the 0-10 range including its bounds")
        void shouldAcceptBounds() {
            assertTrue(SpeedValues.isValid(0.0));
            assertTrue(SpeedValues.isValid(5.5));
            assertTrue(SpeedValues.isValid(10.0));
        }

        @Test
        @DisplayName("should reject out-of-range values and NaN")
        void shouldRejectOutsideRange() {
            assertFalse(SpeedValues.isValid(-0.1));
            assertFalse(SpeedValues.isValid(10.1));
            assertFalse(SpeedValues.isValid(Double.NaN));
        }
    }

    @Nested
    @DisplayName("toBukkit() / toScale()")
    class ConversionTests {

        @Test
        @DisplayName("should map 0, 5 and 10 to -1, 0 and 1")
        void shouldMapAnchorPoints() {
            assertEquals(-1.0f, SpeedValues.toBukkit(0.0), DELTA);
            assertEquals(0.0f, SpeedValues.toBukkit(5.0), DELTA);
            assertEquals(1.0f, SpeedValues.toBukkit(10.0), DELTA);
        }

        @Test
        @DisplayName("should map the vanilla defaults to Bukkit 0.1 and 0.2")
        void shouldMapVanillaDefaults() {
            assertEquals(0.1f, SpeedValues.toBukkit(SpeedValues.DEFAULT_FLY_SCALE), DELTA);
            assertEquals(0.2f, SpeedValues.toBukkit(SpeedValues.DEFAULT_WALK_SCALE), DELTA);
        }

        @Test
        @DisplayName("should round-trip through both directions")
        void shouldRoundTrip() {
            assertEquals(7.25, SpeedValues.toScale(SpeedValues.toBukkit(7.25)), DELTA);
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
    }
}
