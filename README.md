<div align="center">

![JFlyspeed banner](docs/assets/banner-1280x640.png)

<h3>JFlyspeed is an easy-to-use fly and walk speed plugin for 26.x Paper servers.</h3>

### [Wiki](https://jruk8.github.io/JFlyspeed/) · [GitHub](https://github.com/jruk8/JFlyspeed)

</div>

# Quick Start 🚀

1. Drop the jar into `plugins/` and restart the server.
2. Run **`/flyspeed 7`**.

**That's it!** Your fly speed is set on a friendly 0–10 scale. Try
`/walkspeed 6 Steve`, `/speed fly reset @a`, or `/jf help` for the rest.

# Features

- **0–10 speed scale** » Players type `/flyspeed 7`, converted linearly to
  Bukkit's `-1..1` range; out-of-range values are rejected with an error.
- **Vanilla selectors** » `@p`, `@a`, `@r`, `@s` plus player names, with
  custom tab completion (selectors first, your own name first).
- **Flexible roots** » `/flyspeed`, `/walkspeed` and `/speed` can each be
  toggled independently; `/jflyspeed` (alias `/jf`) always works.
- **Reloadable** » `/jflyspeed reload` re-reads every config and re-opens
  storage and placeholders without a restart.
- **Also for serious deployment** » MiniMessage messages, config-driven
  sounds, PlaceholderAPI placeholders, SQLite counters, and an async
  Modrinth update checker.

### Requirements

- Server Software » [Paper 26.2+](https://papermc.io/downloads/paper)
- Java Version » **Java 25**
- Dependencies » **None** (PlaceholderAPI is optional, only for the
  `%jflyspeed_...%` placeholders)

# Commands

| Command | Description |
| --- | --- |
| `/flyspeed <speed\|reset> [targets]` | Set or reset fly speed. |
| `/walkspeed <speed\|reset> [targets]` | Set or reset walk speed. |
| `/speed fly\|walk <speed\|reset> [targets]` | Set or reset one speed channel. |
| `/speed reset [targets]` | Reset both fly and walk speed. |
| `/jflyspeed <help\|version\|reload>` | Help, version, and config reload (alias `/jf`). |

An omitted target means yourself; console must always name one. `reset`
restores the configured defaults. Full table and examples live in
[commands.md](docs/commands.md).

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
`plugins/JFlyspeed/`:

- `speed` » default fly/walk speeds restored by `reset`.
- `commands` » the `/flyspeed`, `/walkspeed`, `/speed` root toggles
  (restart to apply).
- `colors` » the shared message color palette.
- `update-checker` » the async Modrinth update check (console log,
  join notice, `/version` line).
- `messages.yml` / `sounds.yml` » every message and the sound played
  for each feedback event.

Every value is documented in [configuration.md](docs/configuration.md).

# Build

```shell
./gradlew build            # macOS/Linux
.\gradlew.bat build        # Windows
```

`build` runs the tests and both checkstyle tasks; the jar lands in
`build/libs/`. Built with Java 25 and the
[jruk8 plugin-conventions](https://github.com/jruk8/plugin-conventions).

### Links

- [Documentation](https://jruk8.github.io/JFlyspeed/)
- [GitHub](https://github.com/jruk8/JFlyspeed)

## Contributing

See [CONTRIBUTING.md](CONTRIBUTING.md).

&copy; 2026 jruk8. Licensed under [AGPL-3.0](LICENSE).
