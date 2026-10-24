# Permissions

All permissions are declared in `paper-plugin.yml`.

| Permission | Description | Default |
| --- | --- | --- |
| `jflyspeed.flyspeed` | Change fly speed (`flyspeed`, `speed fly`), on self or — with `jflyspeed.other` — on others. | OP |
| `jflyspeed.walkspeed` | Change walk speed (`walkspeed`, `speed walk`), on self or — with `jflyspeed.other` — on others. | OP |
| `jflyspeed.other` | Target other players with the speed commands. Never sufficient on its own: changing someone else's fly speed needs `jflyspeed.flyspeed` plus this node. | OP |
| `jflyspeed.reload` | Use `/jflyspeed reload`. | OP |
| `jflyspeed.help` | Use `/jflyspeed help`. | Everyone |
| `jflyspeed.version` | Use `/jflyspeed version`. | Everyone |

Notes:

- Changing your own speed needs just the matching speed permission. A
  selector resolving only to yourself (e.g. `@s`) counts as self-targeting.
- `/speed <value>` and `/speed reset` act on both channels, so they need
  both `jflyspeed.flyspeed` and `jflyspeed.walkspeed` (plus
  `jflyspeed.other` when targeting others). Bare `/speed` resets only the
  channels the sender holds the permission for.
- `/jflyspeed help` entries are shown only to senders holding the matching
  permission.
