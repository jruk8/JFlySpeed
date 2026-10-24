<div align="center">

![JFlySpeed banner](https://raw.githubusercontent.com/jruk8/JFlySpeed/main/docs/assets/banner-1280x640.png)

<h3>JFlySpeed is a modern fly and walk speed plugin for 26.x Paper servers.</h3>

### [Modrinth](https://modrinth.com/project/jflyspeed) · [Wiki](https://jruk8.github.io/JFlySpeed/) · [GitHub](https://github.com/jruk8/JFlySpeed)

</div>

# Quick Start 🚀

1. Drop the jar into `plugins/` and restart the server.
2. Run **`/flyspeed 2`**.

**That's it!** Your fly speed is set on a friendly 0–10 scale where
`1.0` is normal vanilla speed. Try `/walkspeed 2 Steve`,
`/speed fly reset @a`, or `/jf help` for the rest.

# Features

- **0–10 speed scale** » `1.0` is vanilla normal, `0.0` is frozen, `10`
  is the Bukkit maximum; values outside 0–10 are clamped silently.
- **Vanilla selectors** » `@p`, `@a`, `@r`, `@s` plus player names, with
  custom tab completion (selectors first, your own name first).
- **Flexible roots** » `/flyspeed`, `/walkspeed` and `/speed` can each be
  toggled independently, with configurable aliases (`/fly`, `/ws`, ...);
  `/jflyspeed` (alias `/jf`) always works.
- **Reloadable** » `/jflyspeed reload` re-reads every config and re-opens
  storage and placeholders without a restart.
- **Also for serious deployment** » MiniMessage messages, config-driven
  sounds, PlaceholderAPI placeholders, SQLite counters, and an async
  GitHub update checker.

### Requirements

- Server Software » [Paper 26.2+](https://papermc.io/downloads/paper) (Folia supported)
- Java Version » **Java 25**
- Dependencies » **None** (PlaceholderAPI is optional, only for the
  `%jf_...%` placeholders)

# Commands

| Command | Description |
| --- | --- |
| `/flyspeed [<speed>\|reset] [targets]` | Set or reset fly speed (aliases `/fspeed`, `/fs`, `/fly`). |
| `/walkspeed [<speed>\|reset] [targets]` | Set or reset walk speed (aliases `/wspeed`, `/ws`, `/walk`). |
| `/speed <speed> [targets]` | Set speed for your current mode (flying → fly, else walk). |
| `/speed fly\|walk [<speed>\|reset] [targets]` | Set or reset one speed channel. |
| `/speed reset [targets]` | Reset both fly and walk speed. |
| `/jflyspeed <help\|version\|reload>` | Help, version, and config reload (alias `/jf`). |

An omitted target means yourself; console must always name one. No
arguments resets your own speed, and `reset` restores the configured
defaults. Value arguments tab-complete `reset` first, then the configured
defaults (`1`, `2`, `5`, `10`). Full table and examples live in
[commands.md](https://github.com/jruk8/JFlySpeed/blob/main/docs/commands.md).

# Permissions

| Permission | Description | Default |
| --- | --- | --- |
| `jflyspeed.flyspeed` | Change fly speed. | OP |
| `jflyspeed.walkspeed` | Change walk speed. | OP |
| `jflyspeed.other` | Target other players (needs the matching speed permission too). | OP |
| `jflyspeed.reload` | Use `/jflyspeed reload`. | OP |
| `jflyspeed.version` | Use `/jflyspeed version`. | Everyone |

# Configuration

First start generates `config.yml`, `messages.yml` and `sounds.yml` in
`plugins/JFlySpeed/`:

- `speed` » default fly/walk speeds restored by `reset`.
- `commands` » the `/flyspeed`, `/walkspeed`, `/speed` root toggles
  (restart to apply).
- `aliases` » extra labels for the `/jflyspeed` root (restart to apply).
- `colors` » the shared message color palette.
- `update-checker` » the async GitHub update check (console log,
  join notice, `/version` line).
- `messages.yml` / `sounds.yml` » every message and the sound played
  for each feedback event.

Every value is documented in [configuration.md](https://github.com/jruk8/JFlySpeed/blob/main/docs/configuration.md).

# Build

```shell
./gradlew build            # macOS/Linux
.\gradlew.bat build        # Windows
```

`build` runs the tests and both checkstyle tasks; the jar lands in
`build/libs/`. Built with Java 25 and the
[jruk8 plugin-conventions](https://github.com/jruk8/plugin-conventions).

### Links

- [Documentation](https://jruk8.github.io/JFlySpeed/)
- [GitHub](https://github.com/jruk8/JFlySpeed)

## Contributing

See [CONTRIBUTING.md](https://github.com/jruk8/JFlySpeed/blob/main/CONTRIBUTING.md).

&copy; 2026 jruk8. Licensed under [AGPL-3.0](https://github.com/jruk8/JFlySpeed/blob/main/LICENSE).
