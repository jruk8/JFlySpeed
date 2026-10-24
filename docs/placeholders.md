---
icon: material/variable
---

# Placeholders

Install PlaceholderAPI to use these placeholders. Their identifier is `jf`.
Running `/jflyspeed reload` refreshes the placeholders.

## Usage Placeholders

| Placeholder | Type | Description |
| --- | --- | --- |
| `%jf_uses%` | INTEGER | Your total speed command executions. |
| `%jf_uses_<player>%` | INTEGER | Another player's total speed command executions. |
| `%jf_fly_uses%` | INTEGER | Your fly channel executions. |
| `%jf_fly_uses_<player>%` | INTEGER | Another player's fly channel executions. |
| `%jf_walk_uses%` | INTEGER | Your walk channel executions. |
| `%jf_walk_uses_<player>%` | INTEGER | Another player's walk channel executions. |

A use is recorded whenever a player successfully runs a speed command
(set or reset, including zero-argument resets, any target). Each execution
counts once overall plus once
per affected channel, so `/speed reset` (both channels) adds one to the
total and one to each channel counter. Executions by console are not
attributed to any player and are not counted. Counts live in the SQLite
database, so they survive restarts and are separate per player. Unknown
player names read as `0`.
