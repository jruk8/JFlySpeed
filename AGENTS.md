# AGENTS.md — Developer guidance for AI agents

This file helps AI agents work efficiently in this repository.
It describes the structure, conventions and commands you must respect.

## Project

JPaperTemplate is a Paper plugin template built with Java 25 and Gradle using
the external `com.github.jruk8.plugin-conventions` plugin (see `build.gradle`).
The plugin itself is `JTemplate`, a small but complete plugin with commands,
configs, messages, sounds, PlaceholderAPI placeholders and SQLite storage.

Before writing code, read `README.md` and `docs/api.md`. They document the
module layout and the "how to extend" flows.

## Build & test

```shell
./gradlew build       # Windows: .\gradlew.bat build
```

`build` = compile + checkstyle (`checkstyleMain`, `checkstyleTest`) + unit tests.
Run only tests with `.\gradlew.bat test` when iterating; run `build` (a full
`clean` build) before finishing. Note on Windows: JVM/JDK warnings on stderr
cause non-zero PowerShell pipeline results, so check stdout for
`BUILD SUCCESSFUL` and `$LASTEXITCODE`.

## Architecture conventions

- Base package: `com.jruk8.jtemplate.core`, with one package per module:
  `commands`, `configs`, `messages`, `sounds`, `placeholders`, `storage`.
- Modules are wired together manually (pseudo dependency injection): each class
  receives only what it needs via its constructor, and a module-level
  registrar class (implementing `Bootstrap` and optionally `Reloadable`)
  composes them. `JTemplatePlugin.setupCoreModules()` is the composition root.
- Config classes are okaeri-configs (`eu.okaeri.configs.OkaeriConfig`) with
  Lombok `@Getter`/`@Setter`; sub-config objects are nested classes inside the
  parent config. Configs are loaded by `ConfigRegistrar` and reloaded via
  `Reloader`.
- Commands use Incendo Cloud annotations (`org.incendo.cloud.annotations`).
- Placeholders use PlaceholderAPI 2.11.x (`PlaceholderExpansion.onRequest`).
- Storage is raw JDBC against SQLite; the runtime driver is provided by the
  server (the plugin only declares it as `compileOnly`/`testImplementation`)
  — never add it as `implementation` unless you change that strategy.
- Avoid static state and singletons: getters/instances are created once and
  injected.

## Style (enforced by checkstyle)

- Max 45 lines per method, max 120 chars per line, max 750 lines per file.
- No star imports (including `javax.*`), no unused imports, braces required.
- Classes and methods small and focused; `var` for local variables is normal.
- `main` classes are in `com.jruk8.jtemplate.core.*`; tests mirror the same
  package paths under `src/test/java/com/jruk8/jtemplate/core`.

## Database

The SQLite file defaults to `jtemplate.db` in the plugin's data folder and is
configured under `storage.db-file`. Schema tables: `player_stats` (per-player
counters) and `global_stats` (server-wide counters). Extend `SqliteStorage`
with new stat keys instead of adding ad-hoc tables.