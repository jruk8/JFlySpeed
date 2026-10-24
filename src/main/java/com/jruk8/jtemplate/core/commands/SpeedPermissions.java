package com.jruk8.jtemplate.core.commands;

/**
 * Permission nodes used by the JFlySpeed commands, declared in
 * {@code paper-plugin.yml}.
 */
public final class SpeedPermissions {

    /**
     * Change fly speed. Required for {@code flyspeed} and {@code speed fly}, on self or (with
     * {@link #OTHER}) on others.
     */
    public static final String FLY = "jflyspeed.flyspeed";

    /**
     * Change walk speed. Required for {@code walkspeed} and {@code speed walk}, on self or (with
     * {@link #OTHER}) on others.
     */
    public static final String WALK = "jflyspeed.walkspeed";

    /**
     * Target other players with the speed commands. Never sufficient on its own: changing
     * someone else's fly speed needs {@link #FLY} plus this node.
     */
    public static final String OTHER = "jflyspeed.other";

    /**
     * Reload the plugin configuration.
     */
    public static final String RELOAD = "jflyspeed.reload";

    /**
     * View the plugin version.
     */
    public static final String VERSION = "jflyspeed.version";

    private SpeedPermissions() {
    }
}
