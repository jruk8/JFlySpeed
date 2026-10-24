# Placeholders

PlaceholderAPI support is handled by the `core.placeholders` module. When
PlaceholderAPI is installed and `placeholders.enabled` is true, the plugin
registers an expansion using the configured `placeholders.identifier`
(default `jflyspeed`). The expansion is re-registered on `/jflyspeed reload`
so config changes apply immediately, and unregistered when the plugin
disables.

## Usage Placeholders

| Placeholder | Type | Description |
| --- | --- | --- |
| `%jflyspeed_authors_command_uses%` | INTEGER | Number of times the player has executed `/jflyspeed version`. |
| `%jflyspeed_authors_command_uses_global%` | INTEGER | Server-wide total number of `/jflyspeed version` executions. |

The count is recorded whenever a player runs a command matching
`placeholders.labels` + `placeholders.command` (e.g. `/jflyspeed version` or
`/jf version`). Counts are stored in the SQLite database, so they survive
restarts and are separate per player.

## Configuration

```yaml
placeholders:
  enabled: true
  identifier: jflyspeed
  command: version
  labels:
    - jflyspeed
    - jf
```

## Adding Your Own Placeholders

The intended extension point is `AuthorsCommandUsageExpansion.onRequest()`;
add a new switch case per placeholder:

```java
case "my_stat" -> String.valueOf(tracker.playerUses(player.getUniqueId()));
```

Track new state from anywhere via the injected services:
`PlaceholdersBootstrap` provides a `CommandUsageTracker` around the shared
`SqliteStorage`, or use `SqliteStorage` directly with your own stat key. A
full walkthrough (including new stat keys and listeners) lives in
[api.md](api.md).
