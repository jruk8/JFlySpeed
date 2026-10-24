package com.jruk8.jtemplate.core.messages;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

/**
 * Messages shown by the plugin.
 * <p>
 * Every message starts with {prefix} and uses the config.yml color palette:
 * {plain} for body text, {brand} for headers and the brand name, {hl} for
 * numbers, player names and similar values, {error} for errors. Speed
 * commands insert runtime values through the {mode} string placeholder and
 * MiniMessage tags: &lt;type&gt;, &lt;value&gt;, &lt;old&gt;, &lt;targets&gt;,
 * &lt;count&gt;, &lt;input&gt; and &lt;permission&gt;.
 */
@Getter
@Setter
public class MessagesConfig extends OkaeriConfig {

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Core extends OkaeriConfig {

        @Comment("Sent when a reload succeeds (use <time>)")
        private String reloadSuccess = "{prefix} {plain}Configuration reloaded in {hl}<time>{plain}ms.";

        @Comment("Sent when a reload fails (use <time>)")
        private String reloadFailure = "{prefix} {error}Reload failed in <time>ms. "
                + "Check the console for details.";

        @Comment("Sent when command toggles changed and need a restart")
        private String reloadRestartRequired = "{prefix} {hl}Reload complete. "
                + "{plain}Root command changes need a server restart.";

        @Comment("Help header (use <version>)")
        private String helpHeader = "{prefix} {brand}JFlySpeed <version> - Commands:";

        @Comment("One help entry (use <syntax> and <description>)")
        private String helpEntryFormat = "{prefix} {hl}<syntax> {plain}- <description>";

        @Comment("Version command output (use <version> and <authors>)")
        private String versionSuccess = "{prefix} {plain}Version {hl}<version>{plain}\n"
                + "{hl}» {plain}Authors: {hl}<authors>{plain}";

        @Comment("Update notice (use <latest>, <current>, <url> and <modrinth-url>)")
        private String updateAvailable = "{prefix} {hl}JFlySpeed <latest> is available"
                + "{plain} (running {hl}<current>{plain}): <url> / <modrinth-url>";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class SpeedMessages extends OkaeriConfig {

        @Comment("Sent to yourself when your speed is set (use <type> and <value>)")
        private String setSelf = "{prefix} {plain}Your <type> speed is now {hl}<value>{plain}.";

        @Comment("Sent to the sender when one other player's speed is set (use <type>, <value> and <targets>)")
        private String setOther = "{prefix} {plain}Set <type> speed to {hl}<value>{plain} "
                + "for {hl}<targets>{plain}.";

        @Comment("Sent to the sender when several players' speeds are set "
                + "(use <type>, <value>, <count> and <targets>)")
        private String setMultiple = "{prefix} {plain}Set <type> speed to {hl}<value>{plain} "
                + "for {hl}<count>{plain} players: {hl}<targets>{plain}.";

        @Comment("Sent to each affected player when their speed is set (use <type> and <value>)")
        private String setTarget = "{prefix} {plain}Your <type> speed is now {hl}<value>{plain}.";

        @Comment("Sent to yourself when your speed is reset (use {mode} and <old>)")
        private String resetSelf = "{prefix}{plain}Your {mode} speed was reset back to {hl}1{plain} "
                + "(was {hl}<old>{plain}).";

        @Comment("Sent to the sender when one other player's speed is reset (use {mode}, <targets> and <old>)")
        private String resetOther = "{prefix}{plain}Reset {mode} speed back to {hl}1{plain} "
                + "for {hl}<targets>{plain} (was {hl}<old>{plain}).";

        @Comment("Sent to the sender when several players' speeds are reset (use {mode}, <count> and <targets>)")
        private String resetMultiple = "{prefix}{plain}Reset {mode} speed back to {hl}1{plain} "
                + "for {hl}<count>{plain} players: {hl}<targets>{plain}.";

        @Comment("Sent to each affected player when their speed is reset (use {mode} and <old>)")
        private String resetTarget = "{prefix}{plain}Your {mode} speed was reset back to {hl}1{plain} "
                + "(was {hl}<old>{plain}).";

        @Comment("Sent when both speeds are reset, to yourself and to each affected player")
        private String resetBoth = "{prefix}{plain}Your speeds were reset back to {hl}1{plain}.";

        @Comment("Sent to the sender when one other player's speeds are reset (use <targets>)")
        private String resetBothOther = "{prefix}{plain}Reset speeds back to {hl}1{plain} "
                + "for {hl}<targets>{plain}.";

        @Comment("Sent to the sender when several players' speeds are reset (use <count> and <targets>)")
        private String resetBothMultiple = "{prefix}{plain}Reset speeds back to {hl}1{plain} "
                + "for {hl}<count>{plain} players: {hl}<targets>{plain}.";

        @Comment("Sent when the target selector matched no players")
        private String noTargets = "{prefix} {error}No players matched that target selector.";

        @Comment("Sent when the speed value is not a number (use <input>)")
        private String invalid = "{prefix} {error}'<input>' is not a valid speed. "
                + "Use a number or 'reset'.";

        @Comment("Sent when console runs a speed command without a target selector")
        private String consoleNeedsTarget = "{prefix} {error}Console must specify a target. "
                + "Usage: /<command> <speed> <target>";

        @Comment("Sent when targeting others without the jflyspeed.other permission")
        private String noPermission = "{prefix} {error}You need the jflyspeed.other permission "
                + "to target other players.";

        @Comment("Sent when console runs /speed with a value instead of an explicit mode")
        private String consoleNeedsMode = "{prefix} {error}Console must specify a mode. "
                + "Usage: /speed fly|walk <speed> [targets]";

        @Comment("Sent when the sender lacks the inferred channel permission (use <type> and <permission>)")
        private String noModePermission = "{prefix} {error}You need the <permission> permission "
                + "to change your <type> speed.";
    }

    @Comment("Prefix prepended to every message")
    private String prefix = "<#a6a6a6>[<gradient:#8fdde3:#84b8db><bold>J</bold>FlySpeed</gradient>]</#a6a6a6>";

    @Comment("Core plugin messages")
    private Core core = new Core();

    @Comment("Speed command messages")
    private SpeedMessages speed = new SpeedMessages();
}
