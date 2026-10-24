package com.jruk8.jtemplate.core.configs;

import com.jruk8.jtemplate.core.commands.speed.SpeedValues;
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
        "#      ██╗ ███████╗██╗     ██╗   ██╗███████╗██████╗ ███████╗███████╗██████╗",
        "#      ██║ ██╔════╝██║     ╚██╗ ██╔╝██╔════╝██╔══██╗██╔════╝██╔════╝██╔══██╗",
        "#      ██║ █████╗  ██║      ╚████╔╝ ███████╗██████╔╝█████╗  █████╗  ██║  ██║",
        "# ██   ██║ ██╔══╝  ██║       ╚██╔╝  ╚════██║██╔═══╝ ██╔══╝  ██╔══╝  ██║  ██║",
        "# ╚█████╔╝ ██║     ███████╗   ██║   ███████║██║     ███████╗███████╗██████╔╝",
        "#  ╚════╝  ╚═╝     ╚══════╝   ╚═╝   ╚══════╝╚═╝     ╚══════╝╚══════╝╚═════╝",
        "",
        "# 🏃‍♂️ A lightweight plugin for /flyspeed and /walkspeed.",
        ""
})
public class PluginConfig extends OkaeriConfig {

    @Comment("GitHub update checker settings.")
    private UpdateChecker updateChecker = new UpdateChecker();

    @Comment({
            "Extra labels for the /jflyspeed root command (shortcuts like /jfs).",
            "Applies on restart, like the command toggles below."
    })
    private List<String> aliases = List.of("jfs", "jf");

    @Comment({
            "SQLite storage settings.",
            "The database file is created inside the plugin's data folder."
    })
    private Storage storage = new Storage();

    @Comment({
            "Speed settings. Players use a 0-10 scale where 1.0 is vanilla normal,",
            "0.0 is frozen, and out-of-range input is clamped silently (fly maps 0-1",
            "to Bukkit 0-0.1 and 1-10 to Bukkit 0.1-1; walk maps 0-1 to Bukkit 0-0.2",
            "and 1-10 to Bukkit 0.2-1, with no reverse speeds)."
    })
    private Speed speed = new Speed();

    @Comment({
            "Standalone root commands: each channel registers (or hides) its root,",
            "with extra labels under 'aliases' (also reachable under /jflyspeed).",
            "Applies on restart; /jflyspeed reload reports changed toggles instead."
    })
    private Commands commands = new Commands();

    @Comment({
            "Color palette used by every message via {plain}, {brand}, {hl} and",
            "{error}. Values are MiniMessage color tags."
    })
    private Colors colors = new Colors();

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
                "1.0 = vanilla normal (Bukkit 0.1); 0.0 = frozen."})
        private double defaultFlySpeed = SpeedValues.DEFAULT_FLY_SCALE;

        @Comment({"Default walk speed on the 0-10 scale, restored by 'reset'.",
                "1.0 = vanilla normal (Bukkit 0.2); 0.0 = frozen."})
        private double defaultWalkSpeed = SpeedValues.DEFAULT_WALK_SCALE;

        @Comment({"Tab-completion defaults offered for speed values, in order.",
                "Whole numbers display without decimals."})
        private List<Double> valueSuggestions = List.of(1.0, 2.0, 5.0, 10.0);
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Commands extends OkaeriConfig {

        @Comment("Standalone /flyspeed root command and its extra labels.")
        private Channel flyspeed = new Channel(List.of("fspeed", "fs", "fly"));

        @Comment("Standalone /walkspeed root command and its extra labels.")
        private Channel walkspeed = new Channel(List.of("wspeed", "ws", "walk"));

        @Comment("Standalone /speed root command.")
        private Channel speed = new Channel(List.of());
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Channel extends OkaeriConfig {

        @Comment("Registers the standalone root command for this channel.")
        private boolean enabled = true;

        @Comment("Extra labels for the root command, also reachable under /jflyspeed.")
        private List<String> aliases = List.of();

        public Channel() {
        }

        public Channel(List<String> aliases) {
            this.aliases = aliases;
        }
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Colors extends OkaeriConfig {

        @Comment("MiniMessage color tag for normal message bodies, used via {plain}.")
        private String plainColor = "<#b8d8e0>";

        @Comment("MiniMessage color tag for brand accents, used via {brand}.")
        private String brandColor = "<#84b8db>";

        @Comment("MiniMessage color tag for highlighted words, used via {hl}.")
        private String highlightColor = "<#ebf1f2>";

        @Comment("MiniMessage color tag for errors, used via {error}.")
        private String errorColor = "<#415ad9>";
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class UpdateChecker extends OkaeriConfig {

        @Comment({
                "Checks GitHub releases for new JFlySpeed versions on enable and on reload.",
                "The check runs asynchronously; findings go to the console and to",
                "players holding jflyspeed.reload (on join and via /jflyspeed version)."
        })
        private boolean enabled = true;
    }
}