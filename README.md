![Banner](docs/assets/banner-1280x640.png)
# JPaperTemplate

A clean, buildable starting point for **Paper plugins** using Java 25, Gradle
and the [jruk8 plugin-conventions](https://github.com/jruk8/plugin-conventions).

It intentionally ships as a small, working plugin — commands, configs, sounds,
messages, PlaceholderAPI placeholders and SQLite storage — so you can see the
intended architecture in action and extend it, or delete parts you do not need.

## Requirements

- **Java 25**
- **Paper 26.2+**
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) — only needed for the `%jtemplate_...%` placeholders

## Module overview

Everything lives under `com.jruk8.jtemplate.core` in small, self-contained
packages. Each module owns its config class, its services and a registrar that
plugs it into the plugin.

| Package | Purpose |
| --- | --- |
| `core.commands` | Incendo Cloud commands (`/jtemplate`, alias `/jt`): `help`, `authors`, `reload`. |
| `core.configs` | okaeri-configs setup that loads `config.yml`, `messages.yml`, `sounds.yml`. |
| `core.messages` | MiniMessage / Legacy message parsing and dispatching. |
| `core.sounds` | Config-driven sound playback. |
| `core.placeholders` | PlaceholderAPI expansion template that counts command executions. |
| `core.storage` | Generic SQLite key/value storage (`jtemplate.db`). |

## Build, test, checkstyle

```shell
./gradlew build            # macOS/Linux
.\gradlew.bat build        # Windows
```

`build` runs the tests (`src/test/java` mirrors the main package layout) and
both checkstyle tasks. The plugin jar is written to `build/libs/`.

Style rules enforced by checkstyle:

- Methods are limited to 45 lines, lines to 120 chars
- No star imports
- Files are capped at 750 lines — keep classes small

## Start a new plugin

1. **Find & replace the name everywhere.** Do a project-wide find & replace of
   `jtemplate` → *yourname* and `JTemplate` → *YourName* (covers the base
   package, command labels, placeholder identifiers, the default database name
   and this documentation). Optionally rename the `com/jruk8/jtemplate`
   folders to match.
2. Update `gradle.properties`: `pluginGroup` (base package), `pluginName`,
   `pluginMain` (fully qualified main class), `pluginAuthor`,
   `pluginDescription`, `pluginWebsite` and optionally `paperApiVersion`.
3. Update `rootProject.name` in `settings.gradle`.
4. Tweak the message prefix in `MessagesConfig` (`core/messages`) if desired.
5. Delete the template bits you do not need — see
   [Extending the template](docs/api.md) for what plugs in where.

## Documentation

- [Getting Started](docs/getting-started.md)
- [Commands](docs/commands.md)
- [Permissions](docs/permissions.md)
- [Configuration](docs/configuration.md)
- [Placeholders](docs/placeholders.md)
- [Extending the template (API)](docs/api.md)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

&copy; 2026 jruk8. Licensed under [GPL-3.0](LICENSE).