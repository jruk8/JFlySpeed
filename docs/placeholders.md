# Placeholders

PlaceholderAPI support is handled by the `core.placeholders` module. When
PlaceholderAPI is installed, the plugin registers an expansion with the
fixed identifier `jf`. The expansion is re-registered on `/jflyspeed
reload` and unregistered when the plugin disables. Nothing about the
placeholders is configurable.

## Usage Placeholders

| Placeholder | Type | Description |
| --- | --- | --- |
| `%jf_uses%` | INTEGER | Your total speed command executions. |
| `%jf_uses_<player>%` | INTEGER | Another player's total speed command executions. |
| `%jf_fly_uses%` | INTEGER | Your fly channel executions. |
| `%jf_fly_uses_<player>%` | INTEGER | Another player's fly channel executions. |
| `%jf_walk_uses%` | INTEGER | Your walk channel executions. |
| `%jf_walk_uses_<player>%` | INTEGER | Another player's walk channel executions. |

A use is recorded whenever a player successfully runs a speed command
(set or reset, including zero-argument resets, any target). Each execution
counts once overall plus once
per affected channel, so `/speed reset` (both channels) adds one to the
total and one to each channel counter. Executions by console are not
attributed to any player and are not counted. Counts live in the SQLite
database, so they survive restarts and are separate per player. Unknown
player names read as `0`.

## Adding Your Own Placeholders

The intended extension point is `SpeedUsageExpansion.onRequest()`; add a
new branch per placeholder:

```java
if ("my_stat".equals(params)) {
    return selfUses(player, uuid -> tracker.playerUses(uuid));
}
```

Track new state from anywhere via the injected services:
`JTemplatePlugin` builds one `CommandUsageTracker` around the shared
`SqliteStorage` and passes it to the commands (via `CommandsRegistrar`)
and the expansion (via `PlaceholdersBootstrap`). A full walkthrough
(including new stat keys) lives in [api.md](api.md).
