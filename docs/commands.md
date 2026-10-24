# Commands

All commands are available under `/jflyspeed` and its alias `/jf`. The three
speed commands additionally exist as standalone roots (`/flyspeed`,
`/walkspeed`, `/speed`) unless disabled in `config.yml` — see
[configuration.md](configuration.md).

| Command | Description | Permission |
| --- | --- | --- |
| `/flyspeed <speed\|reset> [targets]` | Set or reset fly speed. | `jflyspeed.flyspeed` |
| `/walkspeed <speed\|reset> [targets]` | Set or reset walk speed. | `jflyspeed.walkspeed` |
| `/speed fly\|walk <speed\|reset> [targets]` | Set or reset one speed channel. | `jflyspeed.flyspeed` / `jflyspeed.walkspeed` |
| `/speed reset [targets]` | Reset both fly and walk speed. | `jflyspeed.flyspeed` + `jflyspeed.walkspeed` |
| `/jflyspeed help [query]` | Shows the in-game command list. | — (entries are permission-filtered) |
| `/jflyspeed version` | Shows the plugin version and authors. | `jflyspeed.version` |
| `/jflyspeed reload` | Reloads all configuration files. | `jflyspeed.reload` |

Examples:

```text
/flyspeed 7                # set your own fly speed to 7
/jf flyspeed reset         # reset your own fly speed to the default
/speed fly 5 @a            # set everyone's fly speed to 5
/jf speed walk reset Steve # reset Steve's walk speed
/speed reset @p            # reset the nearest player's fly and walk speed
```

- **`<speed>`** is a number on the 0–10 scale (see the README); values
  outside the scale are rejected with an error. **`reset`** restores the
  configured default instead.
- **`[targets]`** accepts vanilla selectors (`@p`, `@a`, `@r`, `@s`) and
  player names. An omitted selector means yourself; console must always name
  a target. Targeting anyone but yourself needs `jflyspeed.other` plus the
  relevant speed permission.
- **`/speed reset`** resets both channels at once, so it needs both
  `jflyspeed.flyspeed` and `jflyspeed.walkspeed`.
- **Tab completion** on `[targets]` lists the selectors first, then online
  player names with your own name first, filtered by what you already typed.
  Without `jflyspeed.other` you only get `@s` and your own name.
- **`help`** supports an optional query argument that filters the listed
  commands (`/jflyspeed help reset`).
- **`reload`** re-reads `config.yml`, `messages.yml` and `sounds.yml`, then
  reopens the SQLite database and re-registers the placeholders with the new
  settings. It does not re-register commands: root toggles apply on restart.

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
