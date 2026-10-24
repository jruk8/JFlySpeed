package com.jruk8.jtemplate.core.configs;

import com.jruk8.jtemplate.core.commands.speed.SpeedValues;
import com.jruk8.jtemplate.core.messages.MessageFormat;
import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Comment;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@SuppressWarnings("FieldMayBeFinal")
@Header({
        "################################",
        "#         Plugin Config        #",
        "################################",
        ""
})
public class PluginConfig extends OkaeriConfig {

    @Comment({
            "Configures which format to use for messages.",
            "Supported values: MINIMESSAGE, LEGACY",
            "Default: MINIMESSAGE"
    })
    private MessageFormat messageFormat = MessageFormat.MINIMESSAGE;

    @Comment({
            "PlaceholderAPI placeholder settings.",
            "Placeholders are exposed as %<identifier>_<name>%, e.g. %jflyspeed_authors_command_uses%."
    })
    private Placeholders placeholders = new Placeholders();

    @Comment({
            "SQLite storage settings.",
            "The database file is created inside the plugin's data folder."
    })
    private Storage storage = new Storage();

    @Comment({
            "Speed settings.",
            "Players use a 0-10 scale, converted linearly to Bukkit's -1..1 range",
            "(bukkit = scale / 5 - 1, so 0 -> -1, 5 -> 0 and 10 -> 1).",
            "Values outside 0-10 are rejected with an error; the defaults below are clamped."
    })
    private Speed speed = new Speed();

    @Comment({
            "Standalone root commands. Each toggle registers (or hides) its root command.",
            "The /jflyspeed subcommands always exist, so disabling a root keeps the",
            "feature available under /jflyspeed (alias /jf) only.",
            "Toggles apply on restart; /jflyspeed reload does not re-register commands."
    })
    private Commands commands = new Commands();

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Placeholders extends OkaeriConfig {

        @Comment("Enables the PlaceholderAPI expansion (requires PlaceholderAPI on the server).")
        private boolean enabled = true;

        @Comment({"Identifier prefix shared by all placeholders.",
                "Example: with 'jflyspeed', the usage placeholder is %jflyspeed_authors_command_uses%."})
        private String identifier = "jflyspeed";

        @Comment("The command that the placeholders track (without a leading slash).")
        private String command = "version";

        @Comment("Base command labels (and aliases) that count as executions of the plugin's command.")
        private List<String> labels = List.of("jflyspeed", "jf");
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Storage extends OkaeriConfig {

        @Comment("Name of the SQLite database file created in the plugin's data folder.")
        private String dbFile = "jflyspeed.db";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Speed extends OkaeriConfig {

        @Comment({"Default fly speed on the 0-10 scale, restored by 'reset'.",
                "Vanilla equivalent: 5.5 (= Bukkit 0.1)."})
        private double defaultFlySpeed = SpeedValues.DEFAULT_FLY_SCALE;

        @Comment({"Default walk speed on the 0-10 scale, restored by 'reset'.",
                "Vanilla equivalent: 6.0 (= Bukkit 0.2)."})
        private double defaultWalkSpeed = SpeedValues.DEFAULT_WALK_SCALE;
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Commands extends OkaeriConfig {

        @Comment("Registers the standalone /flyspeed root command.")
        private boolean flyspeed = true;

        @Comment("Registers the standalone /walkspeed root command.")
        private boolean walkspeed = true;

        @Comment("Registers the standalone /speed root command.")
        private boolean speed = true;
    }
}