package com.jruk8.jtemplate.core.commands.speed;

/**
 * Converts between the user-facing 0-10 speed scale and Bukkit's 0..1 range.
 * <p>
 * {@code 1.0} always means vanilla normal speed and {@code 0.0} means frozen;
 * reverse (negative Bukkit) speeds are unreachable. Each channel maps
 * piecewise through the vanilla pivot: fly pivots at internal {@code 5.5}
 * (Bukkit {@code 0.1}), walk at internal {@code 6.0} (Bukkit {@code 0.2}).
 * Below {@code 1.0} the scale runs from frozen (internal {@code 5.0}) to the
 * pivot; above {@code 1.0} it runs from the pivot to internal {@code 10.0}
 * (Bukkit {@code 1.0}).
 */
public final class SpeedValues {

    /**
     * Lowest accepted value on the user-facing scale (frozen).
     */
    public static final double MIN_SCALE = 0.0;

    /**
     * Highest accepted value on the user-facing scale (Bukkit maximum).
     */
    public static final double MAX_SCALE = 10.0;

    /**
     * User-facing value meaning vanilla normal speed on both channels.
     */
    public static final double NORMAL_SCALE = 1.0;

    /**
     * Default fly speed on the user-facing scale (vanilla Bukkit {@code 0.1}).
     */
    public static final double DEFAULT_FLY_SCALE = 1.0;

    /**
     * Default walk speed on the user-facing scale (vanilla Bukkit {@code 0.2}).
     */
    public static final double DEFAULT_WALK_SCALE = 1.0;

    private static final double FROZEN_INTERNAL = 5.0;
    private static final double MAX_INTERNAL = 10.0;
    private static final double FLY_PIVOT = 5.5;
    private static final double WALK_PIVOT = 6.0;

    private SpeedValues() {
    }

    /**
     * Converts a user-facing scale value to Bukkit's 0..1 range.
     *
     * @param type which speed channel (the vanilla pivot differs per channel)
     * @param scale the user-facing value (0 to 10)
     * @return the equivalent Bukkit speed
     */
    public static float toBukkit(SpeedType type, double scale) {
        return (float) (toInternal(type, scale) / 5.0 - 1.0);
    }

    /**
     * Converts a Bukkit speed to the user-facing 0-10 scale.
     *
     * @param type which speed channel (the vanilla pivot differs per channel)
     * @param bukkit the Bukkit speed (0 to 1)
     * @return the equivalent scale value
     */
    public static double toScale(SpeedType type, double bukkit) {
        double pivotBukkit = pivot(type) / 5.0 - 1.0;
        if (bukkit <= pivotBukkit) {
            return NORMAL_SCALE * bukkit / pivotBukkit;
        }
        return NORMAL_SCALE + (MAX_SCALE - NORMAL_SCALE) * (bukkit - pivotBukkit) / (1.0 - pivotBukkit);
    }

    private static double toInternal(SpeedType type, double scale) {
        double pivotValue = pivot(type);
        if (scale <= NORMAL_SCALE) {
            return FROZEN_INTERNAL + (pivotValue - FROZEN_INTERNAL) * scale / NORMAL_SCALE;
        }
        return pivotValue + (MAX_INTERNAL - pivotValue) * (scale - NORMAL_SCALE) / (MAX_SCALE - NORMAL_SCALE);
    }

    private static double pivot(SpeedType type) {
        return type == SpeedType.FLY ? FLY_PIVOT : WALK_PIVOT;
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
