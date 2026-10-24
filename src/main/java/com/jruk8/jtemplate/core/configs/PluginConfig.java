package com.jruk8.jtemplate.core.configs;

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
            "Placeholders are exposed as %<identifier>_<name>%, e.g. %jtemplate_authors_command_uses%."
    })
    private Placeholders placeholders = new Placeholders();

    @Comment({
            "SQLite storage settings.",
            "The database file is created inside the plugin's data folder."
    })
    private Storage storage = new Storage();

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Placeholders extends OkaeriConfig {

        @Comment("Enables the PlaceholderAPI expansion (requires PlaceholderAPI on the server).")
        private boolean enabled = true;

        @Comment({"Identifier prefix shared by all placeholders.",
                "Example: with 'jtemplate', the authors placeholder is %jtemplate_authors_command_uses%."})
        private String identifier = "jtemplate";

        @Comment("The command that the template placeholders track (without a leading slash).")
        private String command = "authors";

        @Comment("Base command labels (and aliases) that count as executions of the plugin's command.")
        private List<String> labels = List.of("jtemplate", "jt");
    }

    @Getter
    @Setter
    @SuppressWarnings("FieldMayBeFinal")
    public static class Storage extends OkaeriConfig {

        @Comment("Name of the SQLite database file created in the plugin's data folder.")
        private String dbFile = "jtemplate.db";
    }
}