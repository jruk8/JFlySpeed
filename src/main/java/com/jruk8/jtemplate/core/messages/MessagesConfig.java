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

        @Comment("Prefix appended before messages (use {prefix} in other messages)")
        private String prefix = "<gray>[<gradient:#5e42f4:#b742f4>JTemplate</gradient>]</gray> <gray>»</gray> ";

        @Comment("Message sent on plugin reload")
        private String reloadSuccess = "{prefix}<green>Configuration successfully reloaded in <time>ms!</green>";

        @Comment("Message sent on help command")
        private String helpHeader = "\n{prefix}<#b742f4>Available commands:</#b742f4>";

        @Comment("Format for version command success (use <version> and <authors>)")
        private String versionSuccess = "{prefix}Version <version>\n<white>» <gray>Authors:</gray> <authors>";

        @Comment("Format for each help entry (use <syntax> and <description>)")
        private String helpEntryFormat = "<white>» <gray><syntax></gray> - <description></white>";
    }

    @Getter
    public static class SpeedMessages extends OkaeriConfig {

        @Comment("Sent to a player setting their own speed (use <type> and <value>)")
        private String setSelf = "{prefix}<green>Your <type> speed is now <value>.</green>";

        @Comment("Sent to the sender setting others' speeds (use <type>, <value> and <targets>)")
        private String setSender = "{prefix}<green>Set <type> speed to <value> for <targets>.</green>";

        @Comment("Sent to each affected player (use <type> and <value>)")
        private String setTarget = "{prefix}<green>Your <type> speed was set to <value>.</green>";

        @Comment("Sent to a player resetting their own speed (use <type> and <value>)")
        private String resetSelf = "{prefix}<green>Your <type> speed was reset to <value>.</green>";

        @Comment("Sent to the sender resetting others' speeds (use <type>, <value> and <targets>)")
        private String resetSender = "{prefix}<green>Reset <type> speed to <value> for <targets>.</green>";

        @Comment("Sent to each affected player (use <type> and <value>)")
        private String resetTarget = "{prefix}<green>Your <type> speed was reset to <value>.</green>";

        @Comment("Sent when a speed is outside the 0-10 scale (use <input>)")
        private String invalidSpeed = "{prefix}<red>Invalid speed '<input>'. Use a number between 0 and 10.</red>";

        @Comment("Sent when console runs a speed command without a target selector")
        private String consoleRequiresTarget = "{prefix}<red>Please specify a target player.</red>";

        @Comment("Sent when targeting others without the jflyspeed.other permission")
        private String missingOtherPermission = "{prefix}<red>You cannot change other players' speeds.</red>";
    }
}