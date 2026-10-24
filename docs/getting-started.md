![Banner](assets/banner-1280x640.png)
# Getting Started

## Requirements

- Paper 26.2 or newer
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

`/jf` is a built-in alias for `/jflyspeed`. Speeds use the 0–10 scale (see
the README); `reset` restores the configured defaults. The placeholders
count how often `/jflyspeed version` is executed (per player and globally):
run it a couple of times, install PlaceholderAPI, and try
`%jflyspeed_authors_command_uses%` in a scoreboard or chat.

## Next Steps

- [Commands](commands.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
