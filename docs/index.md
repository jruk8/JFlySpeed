---
icon: material/home
---

![Banner](assets/banner-1280x640.png)
# JFlySpeed

Fly and walk speed control for Paper servers, with configurable commands,
customizable messages, sounds, optional PlaceholderAPI placeholders, and
usage statistics.

## Key Features

- **0-10 speed scale** » `1.0` is vanilla normal, `0.0` is frozen,
  `10` is the Bukkit maximum; values outside 0-10 are clamped silently.
- **Selectors** » vanilla `@p`, `@a`, `@r`, `@s` plus player names, with
  custom tab completion (selectors first, your own name first).
- **Flexible roots** » `/flyspeed`, `/walkspeed` and `/speed` can each be
  toggled independently; `/jflyspeed` (aliases `/jfs`, `/jf`) always works.
- **Placeholders** » a PlaceholderAPI expansion (`%jf_uses%`,
  `%jf_fly_uses%`, `%jf_walk_uses%`, plus per-player variants) counting
  command executions.
- **Usage statistics** » command counts tracked for each player and globally.
- **Reloadable** » `/jflyspeed reload` re-reads every config and re-opens
  the storage and placeholders.

## Documentation

- [Getting Started](getting-started.md)
- [Commands](commands.md)
- [Permissions](permissions.md)
- [Configuration](configuration.md)
- [Placeholders](placeholders.md)
- [Plugin API for developers](api.md)
