package com.jruk8.jtemplate.core.update;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ModrinthUpdateHttpTest {

    private static final String VERSIONS = "["
            + "{\"id\":\"a\",\"project_id\":\"p\",\"version_number\":\"1.3.0\","
            + "\"dependencies\":[],\"version_type\":\"beta\"},"
            + "{\"id\":\"b\",\"project_id\":\"p\",\"version_number\":\"1.2.0\","
            + "\"dependencies\":[],\"version_type\":\"release\"},"
            + "{\"id\":\"c\",\"project_id\":\"p\",\"version_number\":\"1.1.0\","
            + "\"dependencies\":[],\"version_type\":\"release\"}"
            + "]";

    @Test
    @DisplayName("should pick the newest release and skip pre-releases")
    void shouldPickNewestRelease() {
        Optional<ModrinthRelease> release = ModrinthUpdateHttp.parseVersions(VERSIONS, "jflyspeed");

        assertTrue(release.isPresent());
        assertEquals("1.2.0", release.get().versionNumber());
        assertEquals("https://modrinth.com/plugin/jflyspeed", release.get().pageUrl());
    }

    @Test
    @DisplayName("should return empty when no release exists")
    void shouldReturnEmptyWithoutReleases() {
        assertTrue(ModrinthUpdateHttp.parseVersions("[]", "jflyspeed").isEmpty());
        assertTrue(ModrinthUpdateHttp.parseVersions("[{\"project_id\":\"p\",\"version_number\":\"2.0.0\","
                + "\"version_type\":\"alpha\"}]", "jflyspeed").isEmpty());
    }
}
