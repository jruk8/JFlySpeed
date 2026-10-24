![Banner](assets/banner-1280x640.png)
# JFlySpeed

Fly and walk speed control for Paper servers: Brigadier commands,
okaeri-configs, MiniMessage messaging, sounds, PlaceholderAPI placeholders
and SQLite storage -- all wired without a DI framework.

## Key Features

- **0-10 speed scale** -- `1.0` is vanilla normal, `0.0` is frozen,
  `10` is the Bukkit maximum; values outside 0-10 are clamped silently.
- **Selectors** -- vanilla `@p`, `@a`, `@r`, `@s` plus player names, with
  custom tab completion (selectors first, your own name first).
- **Flexible roots** -- `/flyspeed`, `/walkspeed` and `/speed` can each be
  toggled independently; `/jflyspeed` (alias `/jf`) always works.
- **Small modules** -- one package per concern, each self-contained and easy
  to delete or extend.
- **Manual wiring** - a registrar per module (`Bootstrap`) composes services;
  `JTemplatePlugin` is the single composition root.
- **Placeholders** - a PlaceholderAPI expansion counting command executions,
  with a configurable identifier.
- **SQLite storage** -- generic per-player / global counters in
  `jflyspeed.db`, driven by the server's bundled JDBC driver.
- **Reloadable** -- `/jflyspeed reload` re-reads every config and re-opens
  the storage and placeholders.

## Documentation

- [Getting Started](getting-started.md)
- [Commands](commands.md)
- [Permissions](permissions.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
- [Extending the plugin](api.md)

## Build

Java 25 is required.

```text
./gradlew build
```

The plugin jar is written to `build/libs/`. See the root
[README](../README.md) for the full overview.
