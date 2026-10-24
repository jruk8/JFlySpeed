package com.jruk8.jtemplate.core.update;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Update check engine: fetches the latest Modrinth release and notifies
 * when a newer version exists. Only Bukkit and JDK types are referenced
 * besides the notifier, so the engine stays easy to unit test.
 */
public final class UpdateCheckService {

    private final UpdateCheckHttp http;
    private final UpdateCheckNotifier notifier;
    private final Logger logger;
    private volatile UpdateAvailable pending;

    public UpdateCheckService(UpdateCheckHttp http, UpdateCheckNotifier notifier, Logger logger) {
        this.http = http;
        this.notifier = notifier;
        this.logger = logger;
    }

    /**
     * Returns the last found update, if any.
     *
     * @return the pending update
     */
    public Optional<UpdateAvailable> pendingUpdate() {
        return Optional.ofNullable(pending);
    }

    /**
     * Runs one check against the current version. Blocking: call off the
     * main thread. Failures log once and leave no pending update.
     *
     * @param settings the checker toggles
     * @param slug the Modrinth project slug
     * @param currentVersion the running plugin version
     */
    public void checkNow(UpdateCheckSettings settings, String slug, String currentVersion) {
        pending = null;
        if (!settings.enabled()) {
            return;
        }
        ModrinthRelease release;
        try {
            Optional<ModrinthRelease> fetched = http.fetchLatest(slug);
            if (fetched.isEmpty()) {
                return;
            }
            release = fetched.get();
        } catch (Exception exception) {
            logger.warning("Update check failed: " + exception.getMessage());
            return;
        }
        UpdateVersion current = UpdateVersion.parse(currentVersion);
        UpdateVersion latest = UpdateVersion.parse(release.versionNumber());
        if (!latest.isNewerThan(current)) {
            return;
        }
        pending = new UpdateAvailable(latest.toString(), current.toString(), release.pageUrl());
        logger.info("JFlyspeed " + latest + " is available (running " + current + "): " + release.pageUrl());
        notifier.notifyAdmins(pending);
    }
}
