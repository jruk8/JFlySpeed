# Permissions

## User Commands (`jtemplate.user`)

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.user` | Parent node for user commands. | Everyone |
| `jtemplate.user.help` | Use `/jtemplate help`. | Everyone |
| `jtemplate.user.authors` | Use `/jtemplate authors`. | Everyone |

## Admin Commands (`jtemplate.admin`)

| Permission | Description | Default |
| --- | --- | --- |
| `jtemplate.admin` | Parent node for admin commands. | OP |
| `jtemplate.admin.reload` | Use `/jtemplate reload`. | OP |

Permissions are declared on the command classes with Incendo Cloud's
`@Permission` annotation; see [commands.md](commands.md).
