# More Presets — Design Reference

Adds **modifier presets**, **condition presets**, and **function presets**: named, reusable JSON arrays that can be spliced inline into any modifier, condition, or item function array alongside other items.

## Motivation

Three levels of repetition appear in real data packs that the current preset system cannot address:

1. The same modifier or group of modifiers repeated across multiple `amount` / `lvl` / `chance` fields.
2. The same condition or group of conditions repeated across multiple properties or item functions.
3. Conditions nested inside `condition` modifiers repeated identically in many modifier lists.
4. The same item function or group of functions repeated across multiple equipment slots.

With both features these can all be factored out into named groups and composed freely with inline items.

## Data pack structure

```
data/<namespace>/mobs_properties_randomness/
  modifier_presets/**/*.json   # JSON array of modifier objects
  condition_presets/**/*.json  # JSON array of condition objects
  function_presets/**/*.json   # JSON array of item function objects
```

A modifier preset file is a JSON array (one or more modifiers):

```json
[
    { "modifier": "deepness", "operation": "add", "amount_per_step": 0.5, "step": 64, "starting_y": 64 }
]
```

A condition preset file is a JSON array (one or more conditions):

```json
[
    { "condition": "moon_phase", "phases": ["full_moon"] },
    { "condition": "light_level", "light_level": { "min": 12, "max": 15 }, "type": "sky" }
]
```

A function preset file is a JSON array (one or more item functions):

```json
[
    { "function": "enchant", "type": "with_levels", "lvl": { "min": 1, "max": 10, "modifiers": [{ "preset": "ns:night_scaling" }] }, "conditions": [{ "preset": "ns:enchant_chance" }] }
]
```

## JSON usage

Reference a preset inside any array using `{ "preset": "<namespace>:<name>" }`. All items from the preset array are spliced into that position. Preset references and inline items can be freely mixed:

```json
"modifiers": [
    { "preset": "ns:night_scaling" },
    { "modifier": "difficulty", "operation": "add", "hard": 0.1 }
]
```

```json
"conditions": [
    { "preset": "ns:full_moon_bright" },
    { "condition": "dimension", "dimensions": ["minecraft:overworld"] }
]
```

```json
"functions": [
    { "preset": "ns:night_enchant" },
    { "function": "set_drop_chance", "drop_chance": 1.0 }
]
```

A single preset reference expands a whole group; multiple presets can be referenced in the same array:

```json
"modifiers": [
    { "preset": "ns:night_scaling" },
    { "preset": "ns:difficulty_scaling" }
]
```

Presets can reference each other. A condition preset's `chance.modifiers` can reference a modifier preset, and a modifier preset's `condition` modifier can reference a condition preset:

```json
// modifier_presets/ns/night_scaling.json
[
    { "modifier": "condition", "operation": "multiply", "value": 2, "conditions": [{ "preset": "ns:full_moon_bright" }] },
    { "modifier": "condition", "operation": "multiply", "value": 2, "conditions": [{ "preset": "ns:dark_sky" }] }
]

// condition_presets/ns/enchant_chance.json
[
    { "condition": "chance", "chance": { "value": 0.15, "modifiers": [{ "preset": "ns:night_chance_bonus" }] } }
]
```

## Implementation

### New files

| File | Purpose |
|---|---|
| `data/MPRModifierPresetReloadListener.java` | Loads `modifier_presets/**/*.json` into `Map<ResourceLocation, JsonElement>` |
| `data/MPRConditionPresetReloadListener.java` | Loads `condition_presets/**/*.json` into `Map<ResourceLocation, JsonElement>` |
| `data/MPRFunctionPresetReloadListener.java` | Loads `function_presets/**/*.json` into `Map<ResourceLocation, JsonElement>` |

Both listeners store **raw `JsonElement`s**, not deserialized objects. Deserialization happens in-context when the mob/preset files that reference them are parsed — this avoids any chicken-and-egg issue with the GSON `JsonDeserializationContext`.

### Modified files

**`MPRModifier.deserializeList()`** — when an element has `"preset"`, retrieve the preset array and recursively deserialize its contents into the result list at that position:

```java
for (JsonElement jsonElement : aModifiers) {
    JsonObject jObj = jsonElement.getAsJsonObject();
    if (jObj.has("preset")) {
        ResourceLocation id = MPR.locationFrom(GsonHelper.getAsString(jObj, "preset"));
        JsonElement preset = MPRModifierPresetReloadListener.MODIFIER_PRESETS.get(id);
        if (preset == null)
            throw new JsonParseException("Modifier preset '%s' not found".formatted(id));
        // splice: deserialize the preset array and add all results
        modifiers.addAll(deserializeList(preset.getAsJsonArray(), memberName, context));
        continue;
    }
    // existing type lookup continues unchanged
}
```

**`MPRCondition.deserializeList(JsonArray, context)`** — same splice pattern; this single overload is what all call sites ultimately reach:

```java
for (JsonElement jsonElement : aConditions) {
    JsonObject jObj = jsonElement.getAsJsonObject();
    if (jObj.has("preset")) {
        ResourceLocation id = MPR.locationFrom(GsonHelper.getAsString(jObj, "preset"));
        JsonElement preset = MPRConditionPresetReloadListener.CONDITION_PRESETS.get(id);
        if (preset == null)
            throw new JsonParseException("Condition preset '%s' not found".formatted(id));
        // splice: deserialize the preset array and add all results
        conditions.addAll(deserializeList(preset.getAsJsonArray(), context));
        continue;
    }
    // existing type lookup continues unchanged
}
```

**`MPRItemFunction.deserializeList()`** — same splice pattern:

```java
for (JsonElement jsonElement : aFunctions) {
    JsonObject jObj = jsonElement.getAsJsonObject();
    if (jObj.has("preset")) {
        ResourceLocation id = MPR.locationFrom(GsonHelper.getAsString(jObj, "preset"));
        JsonElement preset = MPRFunctionPresetReloadListener.FUNCTION_PRESETS.get(id);
        if (preset == null)
            throw new JsonParseException("Function preset '%s' not found".formatted(id));
        // splice: deserialize the preset array and add all results
        itemFunctions.addAll(deserializeList(preset.getAsJsonArray(), memberName, context));
        continue;
    }
    // existing type lookup continues unchanged
}
```

**`MPR.onAddReloadListener()`** — registration order determines `apply()` execution order:

```java
event.addListener(MPRModifierPresetReloadListener.INSTANCE);
event.addListener(MPRConditionPresetReloadListener.INSTANCE);
event.addListener(MPRFunctionPresetReloadListener.INSTANCE);
event.addListener(MPRPresetReloadListener.INSTANCE);
event.addListener(MPRMobReloadListener.INSTANCE);
```

Modifier, condition, and function presets must be registered first so their maps are populated before mob/preset deserialization runs.

## What this does not solve

Per-slot repetition (`head`, `chest`, `legs`, `feet`, `mainhand` each declaring the same item function) is a separate problem. A potential future solution would be a new `equipment_set` property that applies one function definition to multiple slots, but that is out of scope here.
