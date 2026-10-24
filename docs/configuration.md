# Configuration

The plugin writes `config.yml`, `messages.yml` and `sounds.yml` into its data
folder (`plugins/JFlySpeed/`) on first start. Edit a file and run
`/jflyspeed reload` to apply the changes without restarting the server
(command registration is the exception — see `commands` below). Missing
keys are re-added with defaults; unknown keys are removed.

## config.yml

```yaml
updateChecker:
  enabled: true
aliases:
  - jfs
  - jf
storage:
  dbFile: jflyspeed.db
speed:
  defaultFlySpeed: 1.0
  defaultWalkSpeed: 1.0
  valueSuggestions:
    - 1.0
    - 2.0
    - 5.0
    - 10.0
commands:
  flyspeed:
    enabled: true
    aliases:
      - fspeed
      - fs
      - fly
  walkspeed:
    enabled: true
    aliases:
      - wspeed
      - ws
      - walk
  speed:
    enabled: true
    aliases: []
colors:
  plainColor: '<#b8d8e0>'
  brandColor: '<#84b8db>'
  highlightColor: '<#ebf1f2>'
  errorColor: '<#415ad9>'
```

### `updateChecker`

- `enabled` - checks GitHub releases for new versions on enable and on
  reload. The check runs asynchronously; findings go to the console and
  in-game to players holding `jflyspeed.reload` (on join and via
  `/jflyspeed version`). Update notices link both the GitHub release and
  the Modrinth project page.

### `aliases`

Extra labels for the `/jflyspeed` root command (`/jfs` is the main alias;
`/jf` also works). Running the root with no arguments shows the help
listing. Blanks, duplicates and the `jflyspeed` label itself are ignored.
Applies on restart, like the command toggles.

### `storage`

- `dbFile` - name of the SQLite database file created in the plugin's data
  folder. The database holds generic per-player and server-wide counters
  (tables `player_stats` and `global_stats`). Speeds themselves are not
  stored there — they live on the player, like vanilla.

### `speed`

- `defaultFlySpeed` / `defaultWalkSpeed` - the values `reset` restores,
  on the user-facing 0–10 scale. `1.0` is vanilla normal on both channels,
  `0.0` is frozen (Bukkit `0`), `10` is the Bukkit maximum (`1.0`); there
  are no reverse speeds. Fly maps `0–1` to Bukkit `0–0.1` and `1–10` to
  Bukkit `0.1–1`; walk maps `0–1` to Bukkit `0–0.2` and `1–10` to Bukkit
  `0.2–1`. Misconfigured values are clamped into range.
- `valueSuggestions` - the defaults offered by tab-completion for speed
  values, in order. Whole numbers display without decimals; non-finite
  entries are dropped and out-of-range ones clamped.

### `commands`

Each channel has an `enabled` toggle registering (or hiding) its
standalone root command, plus `aliases` — extra labels for that root
(e.g. `/fly`, `/ws`). The `/jflyspeed` subcommands always exist, so e.g.
`flyspeed.enabled: false` keeps the feature available as
`/jflyspeed flyspeed` (or `/jfs flyspeed`) only, while the channel aliases
stay reachable under `/jflyspeed` regardless of the toggle. Aliases
colliding with a root name, a `/jflyspeed` subcommand or another alias are
ignored. Toggles and aliases apply on restart: Paper registers commands
once during enable and offers no unregister API, so `/jflyspeed reload`
cannot re-register them — it tells you when toggles changed and a restart
is needed.

### `colors`

The message color palette. Every `messages.yml` entry uses these via
`{plain}`, `{brand}`, `{hl}` and `{error}`, resolved when the message is
rendered — editing a color and reloading is enough, the messages
themselves never hardcode colors.

## messages.yml

The plugin messages, always in MiniMessage format. Every message starts
with `{prefix}` (which includes its trailing space), and colors come
from the `config.yml` palette. Placeholders use `{braces}`; angle
brackets are raw MiniMessage, and `\n` breaks the line:

- `prefix` - prefix prepended via the `{prefix}` placeholder.
- `core.reloadSuccess` / `core.reloadFailure` - reload outcome
  (use `{time}`).
- `core.reloadRestartRequired` - shown when command toggles changed and
  need a restart to apply.
- `core.helpHeader` / `core.helpEntryFormat` - help command layout
  (use `{version}` for the header; `{syntax}` and `{description}` per entry).
- `core.versionSuccess` - version command output (use `{version}` and
  `{authors}`; linked author names arrive pre-formatted).
- `core.updateAvailable` - update notice (use `{latest}`, `{current}`,
  `{url}` and `{modrinth-url}`).
- `speed.setSelf` - feedback for self-targeting sets (use `{type}`,
  `{value}` and `{old}`).
- `speed.resetSelf` - feedback for self-targeting resets (use `{mode}`
  and `{old}`, the value before the reset).
- `speed.setOther` / `speed.resetOther` - summary for the sender when
  one other player is targeted (use `{type}`/`{value}`/`{old}` for sets;
  `{mode}`, `{targets}` and `{old}` for resets).
- `speed.setMultiple` / `speed.resetMultiple` - summary for the sender
  when several players are targeted (use `{type}`, `{targets}`,
  `{count}` and `{value}` for sets; `{mode}`, `{targets}` and `{count}`
  for resets, which omit `{old}` since targets may differ).
- `speed.setTarget` / `speed.resetTarget` - notification sent to each
  affected player (use `{type}`, `{value}` and `{old}` for sets; `{mode}`
  and `{old}` for resets).
- `speed.setBoth` - both-channels set, to yourself and to each
  affected player (use `{value}`).
- `speed.setBothOther` / `speed.setBothMultiple` - both-channels
  set summaries (use `{value}`, `{targets}` and `{count}` for several).
- `speed.wasSuffix` - ` (was {old})` suffix appended to single-channel
  set and reset messages (use `{old}`, rounded to one decimal). Sets show
  it only when the rounded old value differs from the new one.
- `speed.resetBoth` - both-channels reset, to yourself and to each
  affected player.
- `speed.resetBothOther` / `speed.resetBothMultiple` - both-channels
  reset summaries (use `{targets}` and `{count}` for several).
- `speed.noTargets` - the target selector matched no players.
- `speed.invalid` - the speed value is not a number (use `{input}`).
  Values outside the 0–10 scale are clamped silently instead of erroring.
- `speed.consoleNeedsTarget` - console ran a speed command without a
  target.
- `speed.noPermission` - targeting others without `jflyspeed.other`.

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
