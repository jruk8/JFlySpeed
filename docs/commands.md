# Commands

All commands are available under `/jflyspeed` and its aliases (`/jfs`,
`/jf`). Running the root with no arguments shows the help listing. The
three speed commands additionally exist as standalone roots (`/flyspeed`,
`/walkspeed`, `/speed`) unless disabled in `config.yml` — see
[configuration.md](configuration.md).

| Command | Description | Permission |
| --- | --- | --- |
| `/flyspeed [<speed>\|reset] [targets]` | Set or reset fly speed (aliases `/fspeed`, `/fs`). | `jflyspeed.flyspeed` |
| `/walkspeed [<speed>\|reset] [targets]` | Set or reset walk speed (aliases `/wspeed`, `/ws`). | `jflyspeed.walkspeed` |
| `/speed <speed> [targets]` | Set both fly and walk speed. | `jflyspeed.flyspeed` + `jflyspeed.walkspeed` |
| `/speed fly\|walk [<speed>\|reset] [targets]` | Set or reset one speed channel. | `jflyspeed.flyspeed` / `jflyspeed.walkspeed` |
| `/speed reset [targets]` | Reset both fly and walk speed. | `jflyspeed.flyspeed` + `jflyspeed.walkspeed` |
| `/fly [[on\|off] [targets]]` | Toggle or set fly mode. | `jflyspeed.fly` |
| `/jflyspeed help [query]` | Shows the in-game command list. | — (entries are permission-filtered) |
| `/jflyspeed version` | Shows the plugin version and authors (plus a pending update notice for `jflyspeed.reload` holders). | `jflyspeed.version` |
| `/jflyspeed reload` | Reloads all configuration files. | `jflyspeed.reload` |

Examples:

```text
/flyspeed 7                 # set your own fly speed to 7
/fs 7                       # same, via the alias
/jfs flyspeed reset         # reset your own fly speed to the default
/fly                        # toggle your own fly mode
/fly off @a                 # disable fly mode for everyone
/flyspeed                   # same: no arguments resets your own speed
/speed 5                    # set your fly and walk speed to 5
/speed fly 5 @a             # set everyone's fly speed to 5
/jfs speed walk reset Steve # reset Steve's walk speed
/speed reset @p             # reset the nearest player's fly and walk speed
```

- **`<speed>`** is a number on the 0–10 scale, where `1.0` is vanilla
  normal speed and `0.0` is frozen (see the README).
  Non-numeric input is rejected with an error; values outside 0–10 are
  clamped silently. **`reset`** restores the configured default instead.
- **`[targets]`** accepts vanilla selectors (`@p`, `@a`, `@r`, `@s`) and
  player names. An omitted selector means yourself; console must always name
  a target. A selector matching nobody reports an error instead of failing
  silently. Targeting anyone but yourself needs `jflyspeed.other` plus the
  relevant speed permission.
- **`/speed <speed>`** and **`/speed reset`** act on both channels at
  once, so they need both `jflyspeed.flyspeed` and `jflyspeed.walkspeed`.
- **No arguments** resets your own speed: `/flyspeed` and `/walkspeed`
  reset their channel, `/speed fly` and `/speed walk` reset theirs, and
  bare `/speed` resets every channel you hold the permission for (both
  when permitted, otherwise just the one).
- **`/fly`** toggles fly mode: bare it flips your own, `on`/`off` sets
  it explicitly, and a selector applies either to others (needs
  `jflyspeed.other` plus `jflyspeed.fly`, like the speed commands).
  Toggling a group turns everyone off when all can already fly, otherwise
  everyone on.
- **Aliases** (`/fspeed`, `/fs`, `/wspeed`, `/ws`) work as standalone
  roots while their channel's root is enabled, and always under
  `/jflyspeed` (e.g. `/jfs fs 5`). They are configurable — see
  [configuration.md](configuration.md).
- **Tab completion** on `[targets]` lists the selectors first, then online
  player names with your own name first, filtered by what you already typed.
  Without `jflyspeed.other` you only get `@s` and your own name. Value
  arguments suggest `reset` first (after `fly`/`walk`/`reset` on `/speed`),
  then the configured defaults (`1`, `2`, `5`, `10`).
- **`help`** supports an optional query argument that filters the listed
  commands (`/jflyspeed help reset`).
- **`reload`** re-reads `config.yml`, `messages.yml` and `sounds.yml`, then
  reopens the SQLite database, re-registers the placeholders with the new
  settings, and re-runs the update check. A failed reload reports an error
  instead of success. It does not re-register commands: Paper offers no
  unregister API, so changed root toggles apply on restart and `reload`
  says so when it detects a change.

Speeds are applied straight to the player and are not persisted — they behave
exactly like vanilla across relogs and restarts.

## Adding a Command

1. Create a command class in a new subpackage of `core.commands` (e.g.
   `core.commands.user.fun`) exposing a `node()` method that returns a
   `LiteralArgumentBuilder<CommandSourceStack>` built with Paper's
   `Commands.literal(...)` / `Commands.argument(...)`, with `requires(...)`
   for its permissions.
2. Register it in the matching registrar (`UserCommandsRegistrar`,
   `AdminCommandsRegistrar` or `SpeedCommandsRegistrar`) with
   `root.then(new MyCommand(...).node())`, injecting only the services it
   needs (commonly `CommandContext`).
3. Add the permission to `paper-plugin.yml` and document the command in
   [permissions.md](permissions.md).

See [api.md](api.md) for the full walkthrough.
