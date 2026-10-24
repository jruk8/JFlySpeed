# Configuration

The plugin writes `config.yml`, `messages.yml` and `sounds.yml` into its data
folder (`plugins/JFlySpeed/`) on first start. Edit a file and run
`/jflyspeed reload` to apply the changes without restarting the server
(command registration is the exception — see `commands` below).

## config.yml

```yaml
message-format: MINIMESSAGE
placeholders:
  enabled: true
  identifier: jflyspeed
  command: version
  labels:
    - jflyspeed
    - jf
storage:
  db-file: jflyspeed.db
speed:
  default-fly-speed: 5.5
  default-walk-speed: 6.0
commands:
  flyspeed: true
  walkspeed: true
  speed: true
```

### `message-format`

Selects the text format used across the plugin: `MINIMESSAGE`
(Adventure MiniMessage, the default) or `LEGACY` (ampersand codes).

### `placeholders`

Settings for the PlaceholderAPI expansion (see
[placeholders.md](placeholders.md)):

- `enabled` - whether the expansion and its listener are registered.
- `identifier` - the placeholder prefix; `jflyspeed` produces
  `%jflyspeed_authors_command_uses%`.
- `command` - the subcommand whose executions are counted.
- `labels` - the base command labels (and aliases) that count as the
  plugin's command when followed by `command`.

### `storage`

- `db-file` - name of the SQLite database file created in the plugin's data
  folder. The database holds generic per-player and server-wide counters
  (tables `player_stats` and `global_stats`). Speeds themselves are not
  stored there — they live on the player, like vanilla.

### `speed`

- `default-fly-speed` / `default-walk-speed` - the values `reset` restores,
  on the user-facing 0–10 scale (`bukkit = scale / 5 - 1`, so `0 → -1`,
  `5 → 0`, `10 → 1`). The defaults `5.5`/`6.0` are the vanilla equivalents
  of Bukkit `0.1`/`0.2`. Misconfigured values are clamped into range.

### `commands`

Each toggle registers (or hides) its standalone root command. The
`/jflyspeed` subcommands always exist, so e.g. `flyspeed: false` keeps the
feature available as `/jflyspeed flyspeed` (or `/jf flyspeed`) only.
Toggles apply on restart; `/jflyspeed reload` does not re-register commands.

## messages.yml

The plugin messages, in MiniMessage or legacy format depending on
`message-format`:

- `core.prefix` - prefix prepended via the `{prefix}` placeholder.
- `core.reload-success` - message after a successful reload (use `<time>`).
- `core.help-header` / `core.help-entry-format` - help command layout
  (use `<syntax>` and `<description>`).
- `core.version-success` - version command output (use `<version>` and
  `<authors>`).
- `speed.set-self` / `speed.reset-self` - feedback for self-targeting
  changes (use `<type>` and `<value>`).
- `speed.set-sender` / `speed.reset-sender` - summary for the sender when
  others are targeted (use `<type>`, `<value>` and `<targets>`).
- `speed.set-target` / `speed.reset-target` - notification sent to each
  affected player (use `<type>` and `<value>`).
- `speed.invalid-speed` - rejected out-of-range value (use `<input>`).
- `speed.console-requires-target` - console ran a speed command without a
  target.
- `speed.missing-other-permission` - targeting others without
  `jflyspeed.other`.

Messages can embed PlaceholderAPI placeholders (`%player_name%`) when
PlaceholderAPI is installed.

## sounds.yml

Sound effects for command feedback. Each entry supports `enabled`, a
namespaced `sound` key, `pitch` and `volume`
([sound explorer](https://mudkipdev.github.io/minecraft-sound-explorer/)):

- `success-sound` - played on successful commands.
- `error-sound` - played on failed commands.

## Storage (SQLite)

The database file (default `jflyspeed.db`) lives in the plugin's data folder
and is opened on enable, reloaded on `/jflyspeed reload`, and closed on plugin
disable. The JDBC driver comes from the Minecraft server itself, so the plugin
jar stays small; if your server distribution does not bundle SQLite, the
plugin logs a warning and runs without state persistence.
