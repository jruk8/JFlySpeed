![Banner](docs/assets/banner-1280x640.png)
# JFlySpeed

Fly and walk speed control for **Paper** servers, built with Java 25, Gradle
and the [jruk8 plugin-conventions](https://github.com/jruk8/plugin-conventions).

Players set speeds on a friendly **0–10 scale** (`/flyspeed 7`), admins can
target anyone with vanilla selectors (`/speed fly 5 @a`), and every message,
default and root command is configurable.

## Requirements

- **Java 25**
- **Paper 26.2+**
- [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) — only needed for the `%jflyspeed_...%` placeholders

## Commands

| Command | Description |
| --- | --- |
| `/flyspeed <speed\|reset> [targets]` | Set or reset your (or others') fly speed. |
| `/walkspeed <speed\|reset> [targets]` | Set or reset your (or others') walk speed. |
| `/speed fly\|walk <speed\|reset> [targets]` | Set or reset one speed channel. |
| `/speed reset [targets]` | Reset both fly and walk speed. |
| `/jflyspeed <subcommand>` | Same commands under one root (alias `/jf`): `flyspeed`, `walkspeed`, `speed`, `reload`, `help`, `version`. |

Each of `/flyspeed`, `/walkspeed` and `/speed` can be disabled in
`config.yml`; the `/jflyspeed` subcommands always exist, so disabling a root
keeps the feature available under `/jflyspeed` only (e.g. `/jf speed fly 5
@a`). Root toggles apply on restart.

## The 0–10 speed scale

Players never touch Bukkit's `-1..1` range. Speeds are entered on a 0–10
scale, converted linearly (`bukkit = scale / 5 - 1`):

| Scale | Bukkit | Notes |
| --- | --- | --- |
| `0` | `-1` | Full reverse |
| `5` | `0` | Stand still |
| `5.5` | `0.1` | Vanilla fly default (`reset`) |
| `6` | `0.2` | Vanilla walk default (`reset`) |
| `10` | `1` | Full speed |

Values outside 0–10 are rejected with an error. `reset` restores the
configured defaults (`speed.default-fly-speed`, `speed.default-walk-speed`).

Targets accept vanilla selectors (`@p`, `@a`, `@r`, `@s`) and player names;
an omitted selector means yourself, and console must always name a target.
Speeds are applied straight to the player (`setFlySpeed`/`setWalkSpeed`) and
are not persisted — they behave exactly like vanilla across relogs.

## Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jflyspeed.flyspeed` | Change fly speed. | OP |
| `jflyspeed.walkspeed` | Change walk speed. | OP |
| `jflyspeed.other` | Target other players (needs the matching speed permission too). | OP |
| `jflyspeed.reload` | Use `/jflyspeed reload`. | OP |
| `jflyspeed.version` | Use `/jflyspeed version`. | Everyone |

Targeting others needs `jflyspeed.other` plus the relevant speed permission;
a selector resolving only to yourself (e.g. `@s`) needs just the speed
permission. `/speed reset` resets both channels, so it needs both
`jflyspeed.flyspeed` and `jflyspeed.walkspeed`.

## Module overview

Everything lives under `com.jruk8.jtemplate.core` in small, self-contained
packages. Each module owns its config class, its services and a registrar that
plugs it into the plugin.

| Package | Purpose |
| --- | --- |
| `core.commands` | Brigadier commands (`/jflyspeed`, alias `/jf`, plus standalone roots). |
| `core.configs` | okaeri-configs setup that loads `config.yml`, `messages.yml`, `sounds.yml`. |
| `core.messages` | MiniMessage / Legacy message parsing and dispatching. |
| `core.sounds` | Config-driven sound playback. |
| `core.placeholders` | PlaceholderAPI expansion that counts command executions. |
| `core.storage` | Generic SQLite key/value storage (`jflyspeed.db`). |

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

## Documentation

- [Getting Started](docs/getting-started.md)
- [Commands](docs/commands.md)
- [Permissions](docs/permissions.md)
- [Configuration](docs/configuration.md)
- [Placeholders](docs/placeholders.md)
- [Extending the plugin (API)](docs/api.md)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

&copy; 2026 jruk8. Licensed under [GPL-3.0](LICENSE).
