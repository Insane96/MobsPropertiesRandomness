# Possible refactorings

Findings from a full-project scan. Ordered by value.

## 1. ~~Five registries are the same class with the type hardcoded~~ (done)

Added `data/json/MPRRegistry.java` — a generic class holding the `Map`, `register`,
`get(ResourceLocation)`, and `getId(Class)` logic. Each registry now holds a
`public static final MPRRegistry<T> REGISTRY` instance and keeps only its `init()` plus
two one-line static forwarding methods. `EventsRegistry.getId(Class)` was renamed to
`get(Class)` to match the others; its one caller in `MPREvent.java` was updated.

## 2. ~~Six near-identical enum serializers~~ (done)

Replaced with `data/serializer/NameEnumSerializer.java` — a single generic class
taking a `Function<String,T>` decoder and `Function<T,String>` encoder. The six
individual serializer files were deleted; `MPR.createGson()` now uses inline
`NameEnumSerializer<>` instances with method references.

## 3. Duplicated preset-resolving list deserialization

- `MPRCondition.deserializeList(JsonArray, JsonDeserializationContext, Set<ResourceLocation>)`
  — `data/json/condition/MPRCondition.java:60-84`
- `MPRModifier.deserializeList` — `data/json/util/modifiable/MPRModifier.java:57-81`

Structurally identical: same circular-preset-reference guard via a
`Set<ResourceLocation> resolving`, same recursive preset expansion, same
registry-lookup-with-warn-and-skip. Only the registry, preset map, and exception
wording differ. Extract the shared shape into one generic helper (e.g. in
`SerializerUtils`) parameterized by the registry lookup and preset map.

## 4. The two mob/preset reload listeners duplicate their scan loop

- `MPRMobReloadListener.scanDirectory` — `data/MPRMobReloadListener.java:49-68`
- `MPRMobsPresetReloadListener.scanDirectory` — `data/MPRMobsPresetReloadListener.java:51-71`

Near-identical copies: list resources, read+parse each, detect duplicate IDs,
catch-and-count errors — plus the "skip filenames starting with `_`" logic and the
static `errorCount` field pattern repeated in both `apply()` methods.
`MPRRawPresetLoader` (`data/MPRRawPresetLoader.java`) already solved this exact
problem: it's one class parameterized by `directory` and `target` map, instantiated
three times (`MODIFIER_LOADER`, `CONDITION_LOADER`, `FUNCTION_LOADER`). Apply the same
treatment here — a shared abstract base or a parameterized helper.

## 5. Minor cleanup

`MPRBase.onItemAttributeModifierEvent` — `feature/MPRBase.java:154-157` — is a
`@SubscribeEvent` handler with an empty body. Either dead code to remove, or
unfinished work that should say so.

## 6. Docs drift (already fixed)

CLAUDE.md was stale on: the InsaneLib version (said `2.0.0.4-alpha`, actual is
`2.4.21.0` per `gradle.properties`), the data-loading-pipeline description (named a
nonexistent `MPRPresetReloadListener` and omitted `MPRRawPresetLoader` entirely — the
real pipeline is 5 reload listeners, not 2), and a file path
(`module/base/feature/MPRBase.java` instead of `feature/MPRBase.java`). Corrected in
this pass.
