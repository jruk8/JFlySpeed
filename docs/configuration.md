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
  default-fly-speed: 1.0
  default-walk-speed: 1.0
commands:
  flyspeed: true
  walkspeed: true
  speed: true
colors:
  plain-color: '<#b8d8e0>'
  brand-color: '<#84b8db>'
  highlight-color: '<#ebf1f2>'
  error-color: '<#e0867e>'
update-checker:
  enabled: true
  project-slug: jflyspeed
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
  on the user-facing 0–10 scale. `1.0` is vanilla normal on both channels,
  `0.0` is frozen (Bukkit `0`), `10` is the Bukkit maximum (`1.0`); there
  are no reverse speeds. Fly maps `0–1` to Bukkit `0–0.1` and `1–10` to
  Bukkit `0.1–1`; walk maps `0–1` to Bukkit `0–0.2` and `1–10` to Bukkit
  `0.2–1`. Misconfigured values are clamped into range.

### `commands`

Each toggle registers (or hides) its standalone root command. The
`/jflyspeed` subcommands always exist, so e.g. `flyspeed: false` keeps the
feature available as `/jflyspeed flyspeed` (or `/jf flyspeed`) only.
Toggles apply on restart: Paper registers commands once during enable and
offers no unregister API, so `/jflyspeed reload` cannot re-register them —
it tells you when toggles changed and a restart is needed.

### `colors`

The message color palette. Every `messages.yml` entry uses these via
`{plain-color}`, `{brand-color}`, `{highlight-color}` and `{error-color}`,
resolved when the message is rendered — editing a color and reloading is
enough, the messages themselves never hardcode colors.

### `update-checker`

- `enabled` - checks Modrinth for new releases on enable and on reload.
  The check runs asynchronously; findings go to the console and in-game to
  players holding `jflyspeed.reload` (on join and via `/jflyspeed version`).
- `project-slug` - the Modrinth project slug. Placeholder until JFlySpeed
  is published — then it must be replaced with the real slug.

## messages.yml

The plugin messages, in MiniMessage or legacy format depending on
`message-format`. Every message starts with `{prefix}`, and colors come
from the `config.yml` palette:

- `core.prefix` - prefix prepended via the `{prefix}` placeholder.
- `core.reload-success` / `core.reload-failure` - reload outcome
  (use `<time>`).
- `core.reload-restart-required` - shown when command toggles changed and
  need a restart to apply.
- `core.help-header` / `core.help-entry-format` - help command layout
  (use `<syntax>` and `<description>`).
- `core.version-success` - version command output (use `<version>` and
  `<authors>`).
- `core.update-available` - update notice (use `<latest>`, `<current>`
  and `<url>`).
- `speed.set-self` / `speed.reset-self` - feedback for self-targeting
  changes (use `<type>` and `<value>`).
- `speed.set-other` / `speed.reset-other` - summary for the sender when
  one other player is targeted (use `<type>`, `<value>` and `<targets>`).
- `speed.set-multiple` / `speed.reset-multiple` - summary for the sender
  when several players are targeted (use `<type>`, `<value>`, `<count>`
  and `<targets>`).
- `speed.set-target` / `speed.reset-target` - notification sent to each
  affected player (use `<type>` and `<value>`).
- `speed.no-targets` - the target selector matched no players.
- `speed.invalid` - the speed value is not a number (use `<input>`).
  Values outside the 0–10 scale are clamped silently instead of erroring.
- `speed.console-needs-target` - console ran a speed command without a
  target.
- `speed.no-permission` - targeting others without `jflyspeed.other`.

Messages can embed PlaceholderAPI placeholders (`%player_name%`) when
PlaceholderAPI is installed.

## sounds.yml

Sound effects for command feedback. Each entry supports `enabled`, a
namespaced `sound` key, `pitch` and `volume`
([sound explorer](https://mudkipdev.github.io/minecraft-sound-explorer/)):

- `neutral` - neutral feedback for speed changes, heard by the executor
  and every affected player.
- `reset` - feedback for speed resets, heard by the executor and every
  affected player.
- `reload` - played when the configuration is reloaded.
- `help` - played when the help menu is shown.
- `error` - played on failed commands (invalid input, missing permission,
  failed reload).

## Storage (SQLite)

The database file (default `jflyspeed.db`) lives in the plugin's data folder
and is opened on enable, reloaded on `/jflyspeed reload`, and closed on plugin
disable. The JDBC driver comes from the Minecraft server itself, so the plugin
jar stays small; if your server distribution does not bundle SQLite, the
plugin logs a warning and runs without state persistence.
