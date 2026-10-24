# Configuration

The plugin writes `config.yml`, `messages.yml` and `sounds.yml` into its data
folder (`plugins/JTemplate/`) on first start. Edit a file and run
`/jtemplate reload` to apply the changes without restarting the server.

## config.yml

```yaml
message-format: MINIMESSAGE
placeholders:
  enabled: true
  identifier: jtemplate
  command: authors
  labels:
    - jtemplate
    - jt
storage:
  db-file: jtemplate.db
```

### `message-format`

Selects the text format used across the plugin: `MINIMESSAGE`
(Adventure MiniMessage, the default) or `LEGACY` (ampersand codes).

### `placeholders`

Settings for the PlaceholderAPI expansion (see
[placeholders.md](placeholders.md)):

- `enabled` - whether the expansion and its listener are registered.
- `identifier` - the placeholder prefix; `jtemplate` produces
  `%jtemplate_authors_command_uses%`.
- `command` - the subcommand whose executions are counted.
- `labels` - the base command labels (and aliases) that count as the
  plugin's command when followed by `command`.

### `storage`

- `db-file` - name of the SQLite database file created in the plugin's data
  folder. The database holds generic per-player and server-wide counters
  (tables `player_stats` and `global_stats`).

## messages.yml

The plugin messages, in MiniMessage or legacy format depending on
`message-format`:

- `core.prefix` - prefix prepended via the `{prefix}` placeholder.
- `core.reload-success` - message after a successful reload (use `<time>`).
- `core.help-header` / `core.help-entry-format` - help command layout
  (use `<syntax>` and `<description>`).
- `core.author-success` - authors command output (use `<version>` and
  `<authors>`).

Messages can embed PlaceholderAPI placeholders (`%player_name%`) when
PlaceholderAPI is installed.

## sounds.yml

Sound effects for command feedback. Each entry supports `enabled`, a
namespaced `sound` key, `pitch` and `volume`
([sound explorer](https://mudkipdev.github.io/minecraft-sound-explorer/)):

- `success-sound` - played on successful commands.
- `error-sound` - played on failed commands.

## Storage (SQLite)

The database file (default `jtemplate.db`) lives in the plugin's data folder
and is opened on enable, reloaded on `/jtemplate reload`, and closed on plugin
disable. The JDBC driver comes from the Minecraft server itself, so the plugin
jar stays small; if your server distribution does not bundle SQLite, the
plugin logs a warning and runs without state persistence.
