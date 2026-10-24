package com.jruk8.jtemplate.core.commands.speed;

/**
 * Converts between the user-facing 0-10 speed scale and Bukkit's -1..1 range.
 * <p>
 * The mapping is linear: {@code bukkit = scale / 5 - 1}, so {@code 0} maps to
 * {@code -1}, {@code 5} maps to {@code 0} and {@code 10} maps to {@code 1}.
 * Vanilla defaults land at {@code 5.5} (fly, Bukkit {@code 0.1}) and
 * {@code 6.0} (walk, Bukkit {@code 0.2}).
 */
public final class SpeedValues {

    /**
     * Lowest accepted value on the user-facing scale.
     */
    public static final double MIN_SCALE = 0.0;

    /**
     * Highest accepted value on the user-facing scale.
     */
    public static final double MAX_SCALE = 10.0;

    /**
     * Default fly speed on the user-facing scale (vanilla Bukkit {@code 0.1}).
     */
    public static final double DEFAULT_FLY_SCALE = 5.5;

    /**
     * Default walk speed on the user-facing scale (vanilla Bukkit {@code 0.2}).
     */
    public static final double DEFAULT_WALK_SCALE = 6.0;

    private SpeedValues() {
    }

    /**
     * Returns whether a scale value is usable (a real number from 0 to 10).
     *
     * @param scale the user-facing value to check
     * @return {@code true} when the value is within range
     */
    public static boolean isValid(double scale) {
        return !Double.isNaN(scale) && scale >= MIN_SCALE && scale <= MAX_SCALE;
    }

    /**
     * Converts a user-facing scale value to Bukkit's -1..1 range.
     *
     * @param scale the user-facing value (0 to 10)
     * @return the equivalent Bukkit speed
     */
    public static float toBukkit(double scale) {
        return (float) (scale / 5.0 - 1.0);
    }

    /**
     * Converts a Bukkit speed to the user-facing 0-10 scale.
     *
     * @param bukkit the Bukkit speed (-1 to 1)
     * @return the equivalent scale value
     */
    public static double toScale(double bukkit) {
        return (bukkit + 1.0) * 5.0;
    }

    /**
     * Clamps a scale value into the 0-10 range. Used for config defaults so a
     * misconfigured default degrades gracefully instead of throwing.
     *
     * @param scale the value to clamp
     * @return the value restricted to 0-10
     */
    public static double clampScale(double scale) {
        return Math.min(MAX_SCALE, Math.max(MIN_SCALE, scale));
    }

    /**
     * Formats a scale value for chat, dropping the decimal part of whole numbers.
     *
     * @param scale the value to format
     * @return {@code "5"} for {@code 5.0}, {@code "5.5"} for {@code 5.5}
     */
    public static String format(double scale) {
        if (!Double.isInfinite(scale) && scale == Math.rint(scale)) {
            return String.valueOf((long) scale);
        }
        return String.valueOf(scale);
    }
}
