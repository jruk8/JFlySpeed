# Commands

All commands are available under `/jtemplate` and its alias `/jt`.

| Command | Description | Permission |
| --- | --- | --- |
| `/jtemplate help [query]` | Shows the in-game command list. | `jtemplate.user.help` |
| `/jtemplate authors` | Shows the plugin version and authors. | `jtemplate.user.authors` |
| `/jtemplate reload` | Reloads all configuration files. | `jtemplate.admin.reload` |

- **`help`** supports an optional query argument that filters the listed
  commands (`/jtemplate help reload`).
- **`authors`** prints the plugin's version and author list from
  `plugin.yml`.
- **`reload`** re-reads `config.yml`, `messages.yml` and `sounds.yml`, then
  reopens the SQLite database and re-registers the placeholders with the new
  settings.

All permissions are structured under the parent nodes `jtemplate.user` and
`jtemplate.admin`; see [permissions.md](permissions.md) for defaults.

## Adding a Command

1. Create a command class in a new subpackage of `core.commands` (e.g.
   `core.commands.user.fun`), annotate it with
   `@Command("jtemplate|jt")` plus the subcommand, permission and description.
2. Register it in the matching registrar (`UserCommandsRegistrar` or
   `AdminCommandsRegistrar`) with `annotationParser.parse(new MyCommand(...))`,
   injecting only the services it needs (commonly `CommandContext`).
3. Add the permission documentation in [permissions.md](permissions.md).

See [api.md](api.md) for the full walkthrough.
