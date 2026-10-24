# Changelog
## [1.2.0] - 2026-10-24

### Features

- Opt-out bStats metrics toggle

> Add a metrics.enabled flag (default true) following the sibling
> convention: anonymous open-source statistics with no personal data,
> read once at startup so disabling needs a restart.

- Toggle fly mode via /fly and /jfs fly

> /fly with no arguments toggles the own fly mode; on/off sets it
> explicitly, optionally for other targets. A bare selector toggles the
> whole group (off when everyone can already fly, otherwise on).
> Targeting others needs jflyspeed.other plus the new jflyspeed.fly
> node. The fly and walk labels move from the speed aliases to the new
> command, which is root level by default with its own toggle.

- /speed value follows the executor current mode

> Revert the both-channels value mode: a bare /speed value sets the
> channel matching the executor flight state (fly while flying, walk
> otherwise) with a runtime permission check, and console must name the
> mode explicitly. The both-set executor and its messages are gone.


### Miscellaneous

- Update for v1.1.0 [skip ci]

## [1.1.0] - 2026-10-24

### Features

- Single reusable no-permission message

> Replace the other-targeting message and the silent bare-root denial
> with one core.noPermission key naming the missing node.


### Miscellaneous

- Update for v1.0.0 [skip ci]

## [1.0.0] - 2026-10-24

### Bug Fixes

- Send no-targets when selector resolves to nobody

- Declare PlaceholderAPI as optional joined-classpath dependency


### Build & CI

- Name the release secrets in the publish workflow


### Documentation

- Document speed commands, scale and permissions

- Document messages, sounds, colors, reload and update checker

- Add Zensical site config

- Add site overrides and brand stylesheet

- Add link-checker config

- Deploy docs with standalone Zensical workflow

- Rewrite README in the JManhunt style

- Rewrite the contributing guide

- Rewrite the agent guide

- Use the real JFlyspeed banner

- Refresh JFlyspeed banner

- Document the 1.0-normal speed scale

- Refresh JFlySpeed banner

- Use absolute links for Modrinth/Hangar pasting

- Sync configuration, commands and README with the rework

> Document the nested command toggles with channel aliases, the value
> completion defaults, the /speed value mode and zero-argument resets,
> the {mode}/<old> reset placeholders and the new mode error keys.

- Update README.md

- Update README.md

- Update README.md

- Update README.md

- Update README.md

- CamelCase keys, {} placeholders, both-set, jfs, help permission

> Rewrite the configuration reference in the camelCase the plugin writes,
> with updateChecker first, {} placeholders, the was-suffix and set-both
> keys, and the mode keys gone. Commands, permissions, README, index and
> getting-started cover the both-set value mode, bare-root help, the jfs
> main alias and the help permission.


### Features

- Add speed scale conversion helpers

- Add selector tab-completion helper

- Add speed permissions and types

- Add speed defaults and command toggles

- Add speed messages

- Port commands from Cloud to Brigadier

> Replace the Incendo Cloud annotation commands with Paper-native Brigadier
> nodes registered via LifecycleEvents.COMMANDS: flyspeed, walkspeed and the
> unified speed command (value|reset plus optional player selector), plus
> help, version and reload under the jflyspeed root (alias jf). Cloud is
> excluded from all configurations.

- Add message color palette

- Add update-checker settings

- Resolve color palette when rendering

- Add Modrinth release parsing and version comparison

- Rework feedback messages, sounds and reload

- Wire the update checker into the plugin

- Use 1.0-normal piecewise scale without reverse speeds

- Clamp out-of-range input silently instead of erroring

- Usage tracking, channel aliases, value mode, zero-arg reset

> Replace the authors-command hook with jf/fly/walk usage counters wired
> through every speed execution, and drop the placeholders config block.
> Add per-channel root aliases (fspeed/fs/fly, wspeed/ws/walk) that also
> work under /jflyspeed, a /speed value mode using the executor flight
> state, zero-argument self reset on all three commands, and configurable
> value completion defaults after reset. Reset feedback names the previous
> value; the multi-target summary omits it since targets may differ.

- Check GitHub releases with Folia-safe scheduling

> Poll the GitHub releases API instead of Modrinth; the Modrinth slug
> becomes a static internal used only for the notice link. Run the fetch
> on the async scheduler and hop to the global-region scheduler for the
> player broadcast so no Bukkit or PlaceholderAPI calls happen off-thread,
> and mark the plugin folia-supported.

- Was-suffix on sets, both-set value mode, one-decimal old

> Show the shared was-suffix key on single-channel sets when the old
> value rounded to one decimal differs from the new one, and reuse the
> same key for the always-shown reset suffix. Previous values render
> with one decimal. A bare /speed value now sets both channels (needs
> both permissions, like /speed reset) with its own set-both messages
> instead of inferring one channel, so the mode error keys are gone.
> The Modrinth notice link uses the canonical /project/ path.

- Bare root shows concise jfs help, linked authors

> Running /jflyspeed (or any root alias) with no arguments shows the help
> listing. Help entries use the main /jfs alias and no longer duplicate
> the standalone roots. Known authors link to their pages via an internal
> name-to-URL map, and the plugin author is now jruk.

- ASCII header, update-checker first, jfs default alias

> Adopt the playtest config header (ASCII logo, spacing, tagline). The
> update checker is now the first section, and the root aliases default
> to jfs and jf with jfs as the main alias. Set the real bStats plugin
> id 34924.

- Jflyspeed.help permission, blue default error color

> Gate /jflyspeed help (and the bare-root listing) behind jflyspeed.help,
> defaulting to everyone like version. Change the default error color to
> #415ad9.


### Miscellaneous

- Declare permissions in paper-plugin.yml

- Rename plugin identity to JFlyspeed

- License JFlyspeed under AGPLv3

- Remove committed build output

> Drop the compiled plugin-conventions classes and manifest accidentally tracked at the repo root.

- Rename project JFlyspeed to JFlySpeed


### Refactor

- Strict MiniMessage with short color tags and reset wordings **[BREAKING]**

> Drop the configurable message format and the legacy branch: output is
> strictly MiniMessage, with the internal legacy-code cleanup kept as an
> after-pass. Rename the palette placeholders to {plain}, {brand}, {hl}
> and {error}, highlight values and player names in every default, use
> the exact reset wordings with the previous value, and add the mode
> error keys the speed value-mode needs.

- {} placeholders, newline escapes, playtest defaults **[BREAKING]**

> All plugin placeholders use {braces} end to end; angle brackets are
> reserved for raw MiniMessage and pass through untouched (literal
> brackets in defaults are backslash-escaped). MessageParser converts
> backslash-n escapes to real newlines, and the unused TagResolver
> plumbing, raw-string overloads and broadcast helper are gone. Adopt
> the playtested defaults: leading newlines on reload and help, the new
> prefix-less help entry format, the version block with Modrinth, GitHub
> and Donate links, and a prefix with a trailing space so consumers no
> longer add their own. HelpCommand now receives the plugin version so
> the header parses.


### Testing

- Expect the JFlyspeed prefix

## [1.1.0] - 2026-08-17

### Features

- Placeholders, storage (db)


### Miscellaneous

- Update for v1.0.5 [skip ci]


## [Unreleased]

### Features

- Add template PlaceholderAPI expansion with `authors_command_uses` and
  `authors_command_uses_global` placeholders and a configurable identifier
- Add reusable SQLite storage module (`jtemplate.db`) with generic
  per-player and global counters
- Add storage and placeholder config sections to `config.yml`

### Documentation

- Rewrite README and all docs as template documentation (replacing copied
  third-party content)
- Add AI/developer guidance to AGENTS.md

## [1.0.5] - 2026-08-17

### Bug Fixes

- Checkstyle .* import

- Test import


### Miscellaneous

- Bump plugin-conventions to 1.4.0 and remove useless parts

## [1.0.4] - 2026-08-16

### Bug Fixes

- Ci


### Build & CI

- Fix (please)

## [1.0.3] - 2026-08-16

### Bug Fixes

- Ci

