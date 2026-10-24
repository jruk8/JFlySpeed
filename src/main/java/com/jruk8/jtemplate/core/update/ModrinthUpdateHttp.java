package com.jruk8.jtemplate.core.update;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;

/**
 * Fetches project versions from the Modrinth API over HTTPS.
 */
public final class ModrinthUpdateHttp implements UpdateCheckHttp {

    private static final int TIMEOUT_MILLIS = 10_000;
    private static final Pattern VERSION_NUMBER = Pattern.compile("\"version_number\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern VERSION_TYPE = Pattern.compile("\"version_type\"\\s*:\\s*\"([^\"]+)\"");

    private final String userAgent;

    /**
     * Constructs the adapter. Modrinth requires callers to identify
     * themselves, so the plugin version goes into the User-Agent header.
     *
     * @param pluginVersion the running plugin version
     */
    public ModrinthUpdateHttp(String pluginVersion) {
        this.userAgent = "JFlySpeed/" + pluginVersion + " (update-checker)";
    }

    @Override
    public Optional<ModrinthRelease> fetchLatest(String slug) throws Exception {
        HttpsURLConnection connection = (HttpsURLConnection) URI.create(
                "https://api.modrinth.com/v2/project/" + slug + "/version")
                .toURL().openConnection();
        connection.setConnectTimeout(TIMEOUT_MILLIS);
        connection.setReadTimeout(TIMEOUT_MILLIS);
        connection.setRequestProperty("User-Agent", userAgent);
        try {
            if (connection.getResponseCode() < 200 || connection.getResponseCode() >= 300) {
                throw new IOException("Modrinth versions returned HTTP " + connection.getResponseCode());
            }
            try (InputStream body = connection.getInputStream()) {
                return parseVersions(new String(body.readAllBytes(), StandardCharsets.UTF_8), slug);
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * Picks the newest release-type version out of a Modrinth versions
     * response. Pure for tests.
     *
     * @param json the raw versions array
     * @param slug the project slug, used to build the project page URL
     * @return the newest release, or empty when the response holds none
     */
    static Optional<ModrinthRelease> parseVersions(String json, String slug) {
        String best = null;
        for (String chunk : json.split("\"project_id\"")) {
            String number = firstMatch(VERSION_NUMBER, chunk);
            String type = firstMatch(VERSION_TYPE, chunk);
            if (number == null || !"release".equals(type)) {
                continue;
            }
            if (best == null || UpdateVersion.parse(number).isNewerThan(UpdateVersion.parse(best))) {
                best = number;
            }
        }
        if (best == null) {
            return Optional.empty();
        }
        return Optional.of(new ModrinthRelease(best, "https://modrinth.com/plugin/" + slug));
    }

    private static String firstMatch(Pattern pattern, String text) {
        Matcher matcher = pattern.matcher(text);
        return matcher.find() ? matcher.group(1) : null;
    }
}
