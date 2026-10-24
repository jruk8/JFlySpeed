# Placeholders

PlaceholderAPI support is handled by the `core.placeholders` module. When
PlaceholderAPI is installed and `placeholders.enabled` is true, the plugin
registers an expansion using the configured `placeholders.identifier`
(default `jtemplate`). The expansion is re-registered on `/jtemplate reload`
so config changes apply immediately, and unregistered when the plugin
disables.

## Template Placeholders

| Placeholder | Type | Description |
| --- | --- | --- |
| `%jtemplate_authors_command_uses%` | INTEGER | Number of times the player has executed `/jtemplate authors`. |
| `%jtemplate_authors_command_uses_global%` | INTEGER | Server-wide total number of `/jtemplate authors` executions. |

The count is recorded whenever a player runs a command matching
`placeholders.labels` + `placeholders.command` (e.g. `/jtemplate authors` or
`/jt authors`). Counts are stored in the SQLite database, so they survive
restarts and are separate per player.

## Configuration

```yaml
placeholders:
  enabled: true
  identifier: jtemplate
  command: authors
  labels:
    - jtemplate
    - jt
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
