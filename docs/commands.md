---
icon: material/console
---

# Commands

All commands are available under `/jflyspeed` and its aliases (`/jfs`,
`/jf`). Running the root with no arguments shows the help listing. The
three speed commands additionally exist as standalone roots (`/flyspeed`,
`/walkspeed`, `/speed`) unless disabled in `config.yml`; see
[configuration.md](configuration.md).

| Command | Description | Permission |
| --- | --- | --- |
| `/flyspeed [<speed>\|reset] [targets]` | Set or reset fly speed (aliases `/fspeed`, `/fs`). | `jflyspeed.flyspeed` |
| `/walkspeed [<speed>\|reset] [targets]` | Set or reset walk speed (aliases `/wspeed`, `/ws`). | `jflyspeed.walkspeed` |
| `/speed <speed> [targets]` | Set speed for your current mode (flying → fly, else walk). | the inferred channel's node |
| `/speed fly\|walk [<speed>\|reset] [targets]` | Set or reset one speed channel. | `jflyspeed.flyspeed` / `jflyspeed.walkspeed` |
| `/speed reset [targets]` | Reset both fly and walk speed. | `jflyspeed.flyspeed` + `jflyspeed.walkspeed` |
| `/fly [[on\|off] [targets]]` | Toggle or set fly mode. | `jflyspeed.fly` |
| `/jflyspeed help [query]` | Shows the in-game command list. | Permission-filtered entries |
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
/speed 5                    # set your fly or walk speed to 5, whichever fits your current mode
/speed fly 5 @a             # set everyone's fly speed to 5
/jfs speed walk reset Steve # reset Steve's walk speed
/speed reset @p             # reset the nearest player's fly and walk speed
```

- **`<speed>`** is a number on the 0-10 scale, where `1.0` is vanilla
  normal speed and `0.0` is frozen (see the README).
  Non-numeric input is rejected with an error; values outside 0-10 are
  clamped silently. **`reset`** restores the configured default instead.
- **`[targets]`** accepts vanilla selectors (`@p`, `@a`, `@r`, `@s`) and
  player names. An omitted selector means yourself; console must always name
  a target. A selector matching nobody reports an error instead of failing
  silently. Targeting anyone but yourself needs `jflyspeed.other` plus the
  relevant speed permission.
- **`/speed reset`** resets both channels at once, so it needs both
  `jflyspeed.flyspeed` and `jflyspeed.walkspeed`.
- **`/speed <speed>`** picks the channel from your current mode: flying
  means fly speed, anything else means walk speed, and needs that
  channel's permission. Console has no mode, so it must name `fly` or
  `walk` explicitly.
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
  `/jflyspeed` (e.g. `/jfs fs 5`). They are configurable; see
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
  instead of success. Changes to command roots and aliases take effect
  after a restart; `reload` reports when a restart is needed.

Speeds are applied to the player and are not persisted, matching vanilla
behavior across relogs and restarts.
