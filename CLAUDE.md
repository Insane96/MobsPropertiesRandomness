# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build          # Compile and package JAR
./gradlew runClient      # Run Minecraft client with mod loaded
./gradlew runServer      # Run dedicated server with mod loaded
./gradlew clean          # Clean build outputs
```

No test suite exists (`src/test/` is absent). Testing is done by running the game.

## Stack

- **Minecraft 1.21.1** via **NeoForge 21.1.219**
- **InsaneLib 2.4.21.0** — utility library by the same author; provides `MPRModifiable`, `MPRModifier`, `IWeightedRandom`, `ModNBTData`, and the config builder
- **Java 21**, Parchment mappings
- **GSON** for all JSON deserialization (not Minecraft's codec system, except in `MPRSetComponentFunction` where both are used)

## Architecture

The mod is a **data-driven mob modifier**: data packs define JSON files that describe conditions under which mobs spawn and what properties to apply to them.

### Data loading pipeline

Five reload listeners fire in order on `/reload` or world join (registered in `MPR.onAddReloadListener`):

1. `MPRRawPresetLoader.MODIFIER_LOADER` — loads `mobs_properties_randomness/presets/modifiers/**/*.json` into raw `MODIFIER_PRESETS`
2. `MPRRawPresetLoader.CONDITION_LOADER` — loads `mobs_properties_randomness/presets/conditions/**/*.json` into raw `CONDITION_PRESETS`
3. `MPRRawPresetLoader.FUNCTION_LOADER` — loads `mobs_properties_randomness/presets/functions/**/*.json` into raw `FUNCTION_PRESETS`
4. `MPRMobsPresetReloadListener` — loads `mobs_properties_randomness/presets/mobs/**/*.json` into `PRESETS: Map<ResourceLocation, MPRProperties>`
5. `MPRMobReloadListener` — loads `mobs_properties_randomness/mobs/**/*.json` into `MPR_MOBS: List<MPRMob>` (sorted by priority ascending)

Listeners 1-3 run first because condition/modifier/item-function JSON can reference a `"preset"` by id, resolved against these raw maps during deserialization (see `MPRCondition.deserializeList` / `MPRModifier.deserializeList`).

All listeners use the GSON instance built in `MPR.createGson()`, which registers all type adapters. The registry access (needed for codec-based deserialization) is stored at reload time via `AddReloadListenerEvent`.

### Registry pattern

All extensible types use static registry maps populated in `MPR` constructor:

| Registry class | Field lookup key | Base class |
|---|---|---|
| `ConditionsRegistry` | `"condition"` | `MPRCondition` |
| `PropertiesRegistry` | `"property"` | `MPRProperty` |
| `ItemFunctionsRegistry` | `"function"` | `MPRItemFunction` |
| `EventsRegistry` | `"event"` | `MPREvent` |
| `ModifiersRegistry` | `"modifier"` | `MPRModifier` |

To add a new type: create the class, add a `Serializer` inner class implementing both `JsonDeserializer` and `JsonSerializer`, and `register()` it in the registry's `init()` method.

### Serialization convention

Every concrete type has a `Serializer` static inner class implementing `JsonDeserializer<T>` and `JsonSerializer<T>`, annotated with `@JsonAdapter`. The class is registered in its registry. Deserialization reads from a `JsonObject`, calls `MPRConditionable.deserializeList(jObject, context)` for conditions, and ends serialization with `src.endSerialization(jObject, context)` (which appends conditions).

### Mob application pipeline

Triggered by `EntityJoinLevelEvent` in `MPRBase`:
1. Iterate `MPR_MOBS` sorted by priority (lowest first, highest applied last → wins)
2. For each mob: check entity type match and mob-level conditions, then call `property.tryApply(entity)` on each property
3. Each property checks its own conditions before calling `apply()`
4. Entity is marked `PROCESSED` via `ModNBTData` to prevent re-application

Five event handlers cover five priority levels (LOWEST → HIGHEST), allowing ordering between mobs.

### Conditions and properties

- **Conditions** (`MPRCondition`): stateless checks on a `LivingEntity`. All conditions in a list are ANDed; `MPROrCondition` provides OR. Support `"inverted": true`.
- **Properties** (`MPRProperty`): stateful modifications applied to a `LivingEntity`. Each has its own condition list.
- **Item functions** (`MPRItemFunction`): modifications applied to an `ItemStack` during equipment assignment. Registered in `ItemFunctionsRegistry`.

### MPRRange / MPRModifiableValue

Numeric values in JSON can be either a plain number or `{"min": x, "max": y}` (resolved randomly at apply time). Both extend `MPRModifiable` which supports a `modifiers` list (difficulty, deepness, distance from spawn, time played, condition-based) that chain-modify the final value. Always use `MPRRange` for user-facing numeric fields.

### `set_component` item function

`MPRSetComponentFunction` combines GSON (for data pack parsing) with Minecraft's codec system (for applying component values). At deserialization time, `buildResolver()` recursively builds a `Function<LivingEntity, JsonElement>` tree. Any `{"#range": <MPRRange>}` node in the component JSON is replaced by a resolver that calls `MPRRange.getDoubleBetween(living)` at apply time. The resolved `JsonElement` is then parsed by `DataComponentType.codec()` via `RegistryOps<JsonElement>`.

### Events system

`MPREvent` properties register listeners in `MPREvent.LOADED_EVENTS`. When triggered (attack, damage, death, kill, tick, target change), they apply a nested list of properties — either to `THIS` entity or `OTHER` (attacker/victim). Active events are tracked per-entity via `ModNBTData`.

## Key file locations

- `MPR.java` — mod entry point, GSON setup, registry initialization
- `data/MPRMobReloadListener.java` / `MPRMobsPresetReloadListener.java` / `MPRRawPresetLoader.java` — data pack loading
- `data/json/MPRMob.java` — top-level mob definition
- `data/json/condition/MPRCondition.java` — condition base class
- `data/json/property/MPRProperty.java` — property base class
- `data/json/property/equipment/` — item functions and equipment property
- `data/json/util/modifiable/MPRRange.java` — randomizable numeric value
- `feature/MPRBase.java` — main event listener (mob application trigger)
- `src/main/resources/example_data_pack/` — reference data pack showing JSON format

## Data Pack Structure

Files belong under `data/<namespace>/mobs_properties_randomness/`:
- `mobs/**/*.json` — mob definitions (`MPRMob`)
- `presets/**/*.json` — reusable preset property groups (`MPRProperties`)

## Minecraft/NeoForge Sources

The decompiled Java sources for Minecraft/NeoForge (1.21.1, `net.neoforged.moddev` plugin) are already extracted to `C:\Users\delvi\.gradle\mc-sources\1.21.1-neoforge\` (normal package layout, e.g. `net/minecraft/world/entity/LivingEntity.java`) — read directly from there with Read/Grep/Glob instead of asking the user.

If missing or needing regeneration, the source jar is in the NeoForm cache at `~/.gradle/caches/neoformruntime/intermediate_results/mergeWithSources_*_output.jar` (pick the most recent by date) — extract it with `unzip` into the folder above, discarding the `.class` files.