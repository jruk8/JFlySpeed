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

        @Comment("Format for author command success")
        private String authorSuccess = "{prefix}Version <version>\n<white>» <gray>Authors:</gray> <authors>";

        @Comment("Format for each help entry (use <syntax> and <description>)")
        private String helpEntryFormat = "<white>» <gray><syntax></gray> - <description></white>";
    }
}