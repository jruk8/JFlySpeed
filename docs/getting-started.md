![Banner](assets/banner-1280x640.png)
# Getting Started

## Requirements

- Paper 26.2 or newer (Folia supported)
- Java 25

## Installation

1. Build the plugin (`.\\gradlew.bat build`) or download a release jar.
2. Place the jar in your server's `plugins/` folder.
3. Restart the server. The plugin generates `config.yml`, `messages.yml` and
   `sounds.yml` in `plugins/JFlySpeed/` and creates the SQLite database
   `jflyspeed.db`.
4. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/)
   to use the placeholders.

## Your First Commands

```text
/jflyspeed help
/flyspeed 7
/speed walk reset @a
/jflyspeed version
```

`/jfs` is the main alias for `/jflyspeed` (`/jf` also works). Speeds use the 0–10 scale (see
the README); `reset` restores the configured defaults. The placeholders
count your speed command executions: run a few commands, install
PlaceholderAPI, and try `%jf_uses%` in a scoreboard or chat.

## Next Steps

- [Commands](commands.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
