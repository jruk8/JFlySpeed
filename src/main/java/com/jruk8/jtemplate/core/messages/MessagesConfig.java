package com.jruk8.jtemplate.core.messages;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Comment;
import eu.okaeri.configs.annotation.Header;
import lombok.Getter;

@Getter
@SuppressWarnings("FieldMayBeFinal")
@Header({
        "################################",
        "#            Messages          #",
        "################################",
        "",
        "Every message starts with {prefix}, which expands to the prefix below.",
        "Colors come from the config.yml palette: {plain-color}, {brand-color},",
        "{highlight-color} and {error-color}.",
        "Angle placeholders (<time>, <type>, ...) are filled in by the plugin;",
        "PlaceholderAPI placeholders (%player_name%, ...) work when installed.",
        ""
})
public class MessagesConfig extends OkaeriConfig {

    @Comment("Core plugin messages")
    private CoreMessages core = new CoreMessages();

    @Comment("Speed command messages")
    private SpeedMessages speed = new SpeedMessages();

    public String getPrefix() {
        return this.core.getPrefix();
    }

    @Getter
    public static class CoreMessages extends OkaeriConfig {

        @Comment("Prefix starting every message (use {prefix} in other messages)")
        private String prefix = "<#a6a6a6>[<gradient:#8fdde3:#84b8db><bold>J</bold>FlySpeed</gradient>]</#a6a6a6>";

        @Comment("Message sent on plugin reload (use <time> for the reload duration in ms)")
        private String reloadSuccess = "{prefix} {plain-color}Configuration reloaded in <time>ms.";

        @Comment("Message sent when the reload fails (use <time> for the reload duration in ms)")
        private String reloadFailure = "{prefix} {error-color}Reload failed in <time>ms. "
                + "Some settings may not have applied; check the console.";

        @Comment("Notice sent when command toggles changed and need a restart to apply")
        private String reloadRestartRequired = "{prefix} {highlight-color}Command toggles changed. "
                + "{plain-color}Restart the server to apply them.";

        @Comment("Message sent on help command")
        private String helpHeader = "{prefix} {brand-color}Available commands:";

        @Comment("Format for each help entry (use <syntax> and <description>)")
        private String helpEntryFormat = "{prefix} {highlight-color}<syntax> {plain-color}- <description>";

        @Comment("Format for version command success (use <version> and <authors>)")
        private String versionSuccess = "{prefix} {plain-color}Version <version>\n"
                + "{highlight-color}» {plain-color}Authors: <authors>";

        @Comment("Update notice (use <latest>, <current> and <url>)")
        private String updateAvailable = "{prefix} {highlight-color}JFlySpeed <latest> is available "
                + "{plain-color}(running <current>): <url>";
    }

    @Getter
    public static class SpeedMessages extends OkaeriConfig {

        @Comment("Sent to a player setting their own speed (use <type> and <value>)")
        private String setSelf = "{prefix} {plain-color}Your <type> speed is now <value>.";

        @Comment("Sent to the sender setting one other player's speed (use <type>, <value> and <targets>)")
        private String setOther = "{prefix} {plain-color}Set <type> speed to <value> for <targets>.";

        @Comment("Sent to the sender setting several players' speeds (use <type>, <value>, <count> and <targets>)")
        private String setMultiple = "{prefix} {plain-color}Set <type> speed to <value> "
                + "for <count> players: <targets>.";

        @Comment("Sent to each affected player (use <type> and <value>)")
        private String setTarget = "{prefix} {plain-color}Your <type> speed was set to <value>.";

        @Comment("Sent to a player resetting their own speed (use <type> and <value>)")
        private String resetSelf = "{prefix} {plain-color}Your <type> speed was reset to <value>.";

        @Comment("Sent to the sender resetting one other player's speed (use <type>, <value> and <targets>)")
        private String resetOther = "{prefix} {plain-color}Reset <type> speed to <value> for <targets>.";

        @Comment("Sent to the sender resetting several players' speeds "
                + "(use <type>, <value>, <count> and <targets>)")
        private String resetMultiple = "{prefix} {plain-color}Reset <type> speed to <value> "
                + "for <count> players: <targets>.";

        @Comment("Sent to each affected player (use <type> and <value>)")
        private String resetTarget = "{prefix} {plain-color}Your <type> speed was reset to <value>.";

        @Comment("Sent when the target selector matches no players")
        private String noTargets = "{prefix} {error-color}No players matched that selector.";

        @Comment("Sent when the speed value is not a number (use <input>)")
        private String invalid = "{prefix} {error-color}'<input>' is not a number. "
                + "Use a value between 0 and 10.";

        @Comment("Sent when console runs a speed command without a target selector")
        private String consoleNeedsTarget = "{prefix} {error-color}Run this from in game "
                + "or specify a target player.";

        @Comment("Sent when targeting others without the jflyspeed.other permission")
        private String noPermission = "{prefix} {error-color}You need the jflyspeed.other permission "
                + "to change other players' speeds.";
    }
}
