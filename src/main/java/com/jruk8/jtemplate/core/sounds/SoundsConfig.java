package com.jruk8.jtemplate.core.sounds;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import eu.okaeri.configs.annotation.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Header({
        "################################",
        "#            Sounds            #",
        "################################",
        "",
        "Sounds configuration for plugin sounds.",
        "You may customize each sound type below.",
        "Sounds are fully namespaced keys, see Mudkipdev's explorer:",
        "https://mudkipdev.github.io/minecraft-sound-explorer/",
        ""
})
public class SoundsConfig extends OkaeriConfig {

    @Comment("Sound played on successful command execution (e.g., help, reload).")
    private Sound successSound = new Sound(true, "block.note_block.pling", 1.0f, 1.0f);

    @Comment("Sound played on invalid command execution (e.g., invalid arguments).")
    private Sound errorSound = new Sound(true, "block.note_block.bass", 1.0f, 1.0f);

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sound extends OkaeriConfig {

        private boolean enabled = true;
        private String sound = "block.note_block.pling";
        private float pitch = 1.0f;
        private float volume = 1.0f;

        public Sound(boolean enabled, String sound) {
            this(enabled, sound, 1.0f, 1.0f);
        }
    }
}