# 6.2.0.0
* Added event presets, in `data/<namespace>/mobs_properties_randomness/presets/events/`
  * An event preset contains a single event (the same JSON of an `event` property, without `"property": "event"`). `id` is optional and defaults to the preset id
  * Reference it with `{"property": "event", "preset": "namespace:path"}`. The property can still have its own `conditions` and `target`
  * Every reference shares the same event, so the preset can be used by multiple mobs/presets without id conflicts
* Added `in_combat` condition
  * An entity is considered in combat if it has been attacked by or has attacked another entity within the specified `seconds`.
* Implemented `mode` in `presets` properties (as in v4/v5)
  * `exclusive` (default): presets are applied first, and the other properties of the mob/preset are applied only if no preset was applied
  * `before`: presets are applied before the other properties
  * `after`: presets are applied after the other properties
* **Breaking**: `time_played` condition and modifier now use hours instead of minutes
  * `minutes_played` has been renamed to `hours_played`, `minutes` to `hours` and `amount_per_minutes` to `amount_per_hours`. E.g. `"minutes_played": {"min": 60}` must now be `"hours_played": {"min": 1}`
* Fixed mobs with preset nbt (e.g. summoned with `/mpr summon`) re-applying the preset every time they were loaded (chunk reload, server restart, dimension change)
* Fixed a crash when an `event` property had an unknown event type. The property is now skipped with a warning, like unknown properties
* Events with an `id` already used by another event now fail to load, instead of being silently ignored
* Fixed an event triggering multiple times when applied more than once to the same mob
* Fixed events not triggering while data packs were reloading, and a possible crash during `/reload`
* Fixed events still triggering on existing mobs after their mob/preset file failed to load on `/reload`
* Fixed a crash when all the items in `equipment` had a weight of 0 or less. Items with a weight of 0 or less are now never picked
* `lvl` is now required in `enchant` item functions with `type: with_levels`. Before, omitting it crashed the game when the item was generated
* Fixed `distance_from_spawn` modifier with `operation: multiply` setting the value to 0 at world spawn. It now works as documented: `value * (1 + increase)`
* Fixed `deepness` modifier with `operation: multiply` and no `starting_amount` setting the value to 0 above `starting_y`. `starting_amount` now defaults to 1 with `multiply` (still 0 with `add`)
* Fixed `time_played` modifier with `operation: multiply` always doubling the value. With `multiply`, `cap` now limits only the increase (e.g. `cap: 0.5` = up to +50%) and `mode: average` no longer averages the base 1 too
* Fixed `boss_bar` disappearing forever when the mob was unloaded (e.g. chunk unload, server restart, dimension change)
* Fixed every mob being healed to max health when spawning, even if no property changed its max health (e.g. ignoring `Health` set by `/summon` or spawners)
  * `attribute_modifier` properties on max health now keep the mob's health percentage instead (also when applied by events)
* Fixed `death` events with `cancel_death` overriding the health set by their `apply_properties` (e.g. `heal`), always leaving the mob at 0.01 health
* Fixed `kill` events triggering even if the victim's death was canceled
* `death` events now also trigger when the mob dies without a killer (e.g. fall damage, lava, `/kill`). Before, they only triggered if the mob was killed by a living entity
  * Added `require_entity` to `death`, `kill`, `attack` and `damaged` events (defaults to `false`)  
    If true, the event triggers only if the damage was caused by a living entity. Set it to `true` in `death` events to get the old behavior
* Fixed `potion_effect` with stackable `duration` mixing ticks and seconds, resulting in way longer effects (e.g. 10s left + 5s gave 205s instead of 15s). Stacking on an infinite effect now keeps it infinite
* Fixed `potion_effect` warning about `ambient` with hidden particles showing when particles were visible instead
* Fixed conditions with `target: other` inside an `or` condition always failing
* Fixed `score` condition creating a score entry in the scoreboard for every mob checked. Mobs without a score now don't satisfy the condition
* Fixed `time_played` condition with `player: any` checking a random player in range instead of the nearest
* Fixed `day_time` condition losing precision in long-lived worlds
* Fixed `attack`/`damaged` events modifying the damage even when `health_left` was not satisfied
* Fixed `damaged` events reading an outdated damage amount when multiple `damaged` events modify the damage
* Fixed `round` in ranges (e.g. `{"min": 1, "max": 5, "round": 0}`) being ignored. It now rounds the final random value, with every possible value having the same chance
* Fixed integer ranges (e.g. `potion_effect` `amplifier`, `set_count`, `nbt` with `integer`/`boolean` type) almost never rolling the `max` value (e.g. an amplifier of `{"min": 1, "max": 3}` gave 1 or 2, a boolean `nbt` with `{"min": 0, "max": 1}` was always false)
* Fixed `change_target` events not detecting the previous target of brain based mobs (e.g. piglins, hoglins), making `new_target` trigger on every target change and `switch_target` never trigger
* `nbt` is now required in `raw_nbt` properties. Before, omitting it crashed the game when the property was applied
* Fixed `presets` properties nested in other properties (e.g. in events' `apply_properties`) being silently ignored
* Fixed a possible failure of the whole `/reload` when an error message contained a `%` character
* Fixed several JSON serializers writing missing or wrong fields
* Fixed `equipment` items emptied by their functions (e.g. `set_count` to 0) clearing the slot instead of keeping the current item. Only `minecraft:air` clears the slot now
* `enchant` item function fixes
  * `random` and `single` now check compatibility with the enchantments already stored in enchanted books
  * `with_levels` now excludes curses/treasure (if disabled) before picking the enchantments, instead of picking them and then discarding them, resulting in fewer enchantments
  * `with_levels` now respects `allow_incompatible` with the enchantments already on the item
* Condition, modifier and function preset references (`{"preset": "..."}`) with other fields (e.g. `inverted`, `target`) now fail to load instead of silently ignoring them
* Fixed exploding creepers having effects longer than 30 minutes (and infinite ones) removed from themselves (e.g. not triggering on death effects like Oozing) and excluded from the lingering cloud even with `betterCreeperLingering` disabled. Now they're only excluded from the cloud, and only when the config is enabled
* `structure` condition now reports non-existing structures when loading, instead of sending a message in chat every time a mob is checked
* Documented that `weather: rain` is also true during thunderstorms
* `/mpr summon` now fails with an unknown preset, instead of summoning a normal mob, and uses the local difficulty of the summon position instead of the command source's one
* Mobs with the same `priority` are now always applied in the same order (sorted by file id)
* Minor performance improvement for `damage_immunity`
* `slots` in `add_attribute_modifier` item functions and `event_priority` are now lowercase like the other values (e.g. `"mainhand"`, `"highest"`). Uppercase values still work
* Fixed `presets` inside a preset never being applied when the preset was applied via preset nbt (e.g. `/mpr summon`)
* Fixed a `death` event with `cancel_death` overriding the health healed by a previous `death` event of the same mob

# 6.1.10.0
* Added `target` field to properties inside `apply_properties`
  * Defaults to the event's own `target` if omitted, so existing data packs behave the same.  
    When set, it overrides where that specific property is applied, independently of the event's `target` and of the other properties in the same `apply_properties` list.  
    E.g., a single event with a 50% chance can now apply one property to `"this"` and another to `"other"` at the same time.

# 6.1.9.0
* Added `target` field to conditions
  * Defaults to `"this"`, used in events to decide which entity to check the conditions against.  
    Before, you couldn't check for conditions on `"other"` while applying properties to `"this"`. Now you can.  
    E.g., you can now apply an effect to `"this"` if the attacker (`"other"`) has a certain item.  
    This propagates to inner conditions too, so in `apply_properties`, nested modifiers, etc. you can decide to check conditions against `"this"` or `"other"`

# 6.1.8.0
* Added `days_passed` condition

# 6.1.7.2
* Fixed `health_left` check in `damaged`/`attack` events being inverted (was triggering outside the specified range instead of inside)
* Fixed `flat_health_left` in `damaged`/`attack` events using percentage by default and flat health when enabled, instead of the other way around
* Fixed `nbt` property with `boolean` type using a random chance instead of setting a `true`/`false` value
* Fixed `potion_effect`'s stackable `amplifier`/`duration` discarding the previously stacked value when a `cap` was set, instead of summing then clamping to the cap

# 6.1.7.1
* Fixed weather condition "thunder" not working

# 6.1.7.0
* Stack traces are now hidden from errors in logs by default and can be enabled with a config option
  * Basically an error should show up much cleaner in the log

# 6.1.6.1
* Fixed On Hit events not checking for conditions before applying the damage multiplier

# 6.1.6.0
* Added `remove_potion_effects` and `remove_attribute_modifiers` properties

## Technical
* Properties, Conditions etc are now registry objects, like blocks and items

# 6.1.5.2
## Technical
* Fixed events being posted too early, not working

# 6.1.5.1
## Technical
* Fixed events being posted too early, not working

# 6.1.5.0
## Technical
* Changed how Registries work
  * Now requires an event to register properties, conditions, etc

# 6.1.4.1
* Fixed `play_sound` not working if played from a player

# 6.1.4.0
* Added `has_owner` condition

# 6.1.3.2
* Updated for latest InsaneLib

# 6.1.3.1
* Disabled warning message when reloading by default

# 6.1.3.0
* Added message to chat when reloading generates errors or warnings

# 6.1.2.0
* Inverted how `priority` on mobs works.

# 6.1.1.1
* Fixed biome condition not working

# 6.1.1.0-beta
* Changed some fields for Potion effects
  * `hide_particles` -> `show_particles`: defaults to true now
  * Added `show_icon`: defaults to true
* Fixed attribute modifiers erroring (failing to spawn the mob) if a modifier with the same id is already applied

# 6.1.0.0-alpha
BREAKING CHANGES Please read carefully
* More presets!
  * PLEASE NOTE that mobs presets now live in `data/<namespace>/mobs_properties_randomness/presets/mobs/` instead of `data/<namespace>/mobs_properties_randomness/presets/`.
  * This version introduces modifiers, conditions and functions presets.  
    These are used if you have multiple conditions, modifiers and functions that repeat, and instead you can write once and reference.
  * Example, you want to apply a global difficulty scaling modifier to many attribute values, but this is repetitive
    ```json 
    [
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.max_health",
        "id": "ns:bonus_health",
        "amount": {
            "value": 1,
            "modifiers": [
                {
                    "modifier": "difficulty", 
                    "operation": "multiply", 
                    "easy": 0.75, 
                    "hard": 1.5 
                }
            ]
        },
        "operation": "add_multiplied_base"
    },
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.movement_speed",
        "id": "ns:movement_speed",
        "amount": {
            "value": 0.10,
            "modifiers": [
                { 
                    "modifier": "difficulty", 
                    "operation": "multiply", 
                    "easy": 0.75, 
                    "hard": 1.5 
                }
            ]
        },
        "operation": "add_multiplied_base"
    }
    ]
    ```
    To avoid all this redundancy, you can use a modifier preset. in `data/ns/presets/modifiers/difficulty_scaling.json`. A modifier preset can contain as many modifiers as you want.
    ```json
    [
        { "modifier": "difficulty", "operation": "multiply", "easy": 0.75, "hard": 1.5 }
    ]
    ```
    So the properties can become
    ```json 
    [
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.max_health",
        "id": "ns:bonus_health",
        "amount": {
            "value": 1,
            "modifiers": [
                { "preset": "ns:difficulty_scaling" }
            ]
        },
        "operation": "add_multiplied_base"
    },
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.movement_speed",
        "id": "ns:movement_speed",
        "amount": {
            "value": 0.10,
            "modifiers": [
                { "preset": "ns:difficulty_scaling" }
            ]
        },
        "operation": "add_multiplied_base"
    }
    ]
    ```
    This is a quite easy example, but another real example is:  
    [This is something that will heavily benefit from using presets](https://github.com/Insane96/EnhancedAI/blob/febf0ebe3db34dce1a0d1e9892afb6a2bd8243e4/src/main/resources/integrated_packs/mpr_integration/data/enhancedai/mobs_properties_randomness/mobs/overworld_zombies_equipment.json).
* Fixed summoning via `/mpr` command applying presets 5 times

# 6.0.1.3
* Fixed multiple boss bars flickering

# 6.0.1.2-beta
* Update to latest InsaneLib

# 6.0.1.1-beta
* Update to latest InsaneLib

# 6.0.1.0-beta
* Added `set_component` item function
  * You can set whatever data component you want on an item. The following example from the example data pack will make the target equip an iron ingot that can be eaten and used as a pickaxe. Numeric values can be replaced with #range object to generate random values.
  ```json
  {
    "property": "equipment",
    "slot": "offhand",
    "items": [
      {
        "item": "minecraft:iron_ingot",
        "functions": [
          {
            "function": "set_component",
            "components": {
              "minecraft:food": {
                "nutrition": {"#range": {"min": 2, "max": 6}},
                "saturation": {"#range": 0.5}
              },
              "minecraft:tool": {
                "rules": [
                  {
                    "blocks": "#mineable/pickaxe",
                    "speed": 6,
                    "correct_for_drops": true
                  }
                ]
              },
              "minecraft:max_damage": 453,
              "minecraft:damage": 0
            }
          },
          {
            "function": "set_drop_chance",
            "drop_chance": 2
          }
        ]
      }
    ]
  }
  ```

# 6.0.0.0-alpha
Ported to 1.21.1

Mod support will be added back in the future. Let's see how this goes first.  
I've also updated the Example Data Pack which was broken in 1.20.1 too.
The only difference between 1.20.1 and 1.21.1 are attribute modifier operations.

## Missing
* Set (Raw) NBT for items
* Scaling (pehkui is no longer needed (and seems to no longer work?) as scaling is now an attribute)
* Serene Seasons support
* Game stages (hasn't updated past 1.20.3)
