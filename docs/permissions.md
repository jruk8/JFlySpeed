# Permissions

All permissions are declared in `paper-plugin.yml`.

| Permission | Description | Default |
| --- | --- | --- |
| `jflyspeed.flyspeed` | Change fly speed (`flyspeed`, `speed fly`), on self or — with `jflyspeed.other` — on others. | OP |
| `jflyspeed.walkspeed` | Change walk speed (`walkspeed`, `speed walk`), on self or — with `jflyspeed.other` — on others. | OP |
| `jflyspeed.other` | Target other players with the speed commands. Never sufficient on its own: changing someone else's fly speed needs `jflyspeed.flyspeed` plus this node. | OP |
| `jflyspeed.reload` | Use `/jflyspeed reload`. | OP |
| `jflyspeed.version` | Use `/jflyspeed version`. | Everyone |

Notes:

- Changing your own speed needs just the matching speed permission. A
  selector resolving only to yourself (e.g. `@s`) counts as self-targeting.
- `/speed reset` resets both channels, so it needs both `jflyspeed.flyspeed`
  and `jflyspeed.walkspeed` (plus `jflyspeed.other` when targeting others).
- `/jflyspeed help` has no permission of its own: everyone may run it, and
  each entry is shown only to senders holding the matching permission.
