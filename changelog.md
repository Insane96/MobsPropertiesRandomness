# Changelog

## Upcoming
* Time Existed, Condition and Depth modifiers now use a ModifiableValue instead of a simple number
* Changed `override` to `keep_spawned` (so it now defaults to opposite behaviour)
* Fixed crash when missing `biomes` or `dimensions` in world whitelist object

## 4.6.0
* Updated to MC 1.20.1

## 4.5.2
* Range Object now inherits from Modifiable Value and has a new field
  * `modifiers_behaviour`: can be "both" (default), "min_only", "max_only".
* Depth Modifiers' `starting_y` no longer has a default value and it's mandatory
* Fixed `time_existed_modifier` not taking into account `operation`
  * Also renamed `bonus_percentage` to `bonus_per_seconds`
* Fixed the mod not working server side only

## 4.5.1
* Fixed scaling being separated with multiple scale types

## 4.5.0
* "Conditions" object now has `inverted` boolean
  * When true, the conditions are inverted (so by checking if the player has completed an advancement, with `"inverted": true` it checks if the player hasn't completed it)
* Scale Pehkui object now requires `operation`
  * Can be "set" (previous behavior), "add" or "multiply"
* Abstract Event Object now has `scale_pehkui` list
  * Applies scale changes whenever the event triggers
* Range object now accepts `min` and `max` as Modifiable Values instead of being a Modifiable Object itself
* Split `pos_modifier` into `world_spawn_distance_modifier` and `depth_modifier`
* Reimplemented Game Stages Support
* Renamed `conditions_modifier` to `condition_modifiers`
* Renamed `advancements_done` to `advancements_unlocked`
* Enchantment and NBT Objects now have `conditions`
* Removed mobs specific properties (`creeper`, `phantom`, `ghast`). Use NBT
* Potion effect duration is now actually infinite and not a huge value
* Decreased the time of a potion effect required for a creeper to not spawn a lingering cloud (60 -> 30 minutes)

## 4.4.2
* Fixed Custom names not working with presets
* Moved info logs to debug

## 4.4.1
* Added `moon_phases` to `world` condition
  * A list of [MoonPhases](https://github.com/Insane96/MobsPropertiesRandomness/blob/ddbb215424dfcfe6db969f4e8c908768a1abace5/src/main/java/insane96mcp/mobspropertiesrandomness/data/json/util/MPRWorldWhitelist.java#L103) which must match for the condition to be met
* Changed `world_whitelist` to `conditions` in Attributes, Potion Effects and Items.
* Fixed OnHit object's `potion_effects` begin mandatory

## 4.4.0
* Updated to 1.19.4

## Beta 4.3.0
* Port to 1.19.3

## Beta 4.2.0
* Added `boss_bar` to `presets`
  * When present adds a boss bar to the preset mob. Has a `color`, `type` (decoration), `darken_screen` and `range`
* Added `apply_all` to `presets`
  * When true, instead of picking a random preset based off weight, all the presets are checked and try to apply.
  * With this, a `chance` field has been added to Weighted Presets

## Beta 4.1.1
* Reimplemented World Whitelist
  * `biomes` now accepts a list of biomes / biome tags. "minecraft:desert" or "#minecraft:is_forest"
* Added `inverse_dimension_list` and `inverse_biome_list` to world whitelist
* Weighted Presets now have `condition` instead of `world_whitelist`

## Alpha 4.1.0
* Updated to MC 1.19.2, requires InsaneLib 1.7.5+
* Changed `mob_id` and `entity_tag` to `target`
  * Target accepts either a mob like `mob_id` would or an entity tag with a # before the name. E.g. to target all skeletons you can do "#minecraft:skeletons"

### Notes
This is marked as Alpha because World Whitelist is currently not implemented yet.  
Also marked as an alpha because stuff might be broken as I didn't fully test it.  
Tinkers Construct is not yet implemented as ... well, there's no 1.19.2 version of TiCon yet