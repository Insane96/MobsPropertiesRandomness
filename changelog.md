# Changelog

## Upcoming
* Added `distance_cap` to World Spawn Distance Modifier
* On hit object's `health_left` is now a range between 0 and 1
* Fixed on_tick event working during the mob's death animation

## 4.10.3
* Fixed crash when non-living entities damaged someone

## 4.10.2
* Renamed `on_attacked` event to `on_damaged`
  * Fixed event triggering only when the mob is attacked by another entity

## 4.10.1
* Setting weight to < 1 will now show an error 

## 4.10.0
* Added `inverse_structures`
  * If true, the condition fails if the mob spawns in the listed structure
* Added `count` to items
* Fixed `play_sound` not working with resource pack sounds

## 4.9.1
* Fixed crash when omitting `inverse_dimension_list`

## 4.9.0
* Added `effects_immunity`
  * A list of effects id that the mob cannot be affected by
* Added `drop_chance`, `enchantments`, `attributes` and `nbt` to slots
  * This makes you apply them to all the items listed, instead of having to add them to every single item

## 4.8.0
* Added `set_fire` and `set_freeze` to On Hit and On Death event
  * Sets the target to the specified seconds on fire and freeze
* Renamed event targets from "entity" to "this"

## 4.7.2
* Now requires InsaneLib 1.11.1

## 4.7.1
* Fixed priorities being applied in the wrong order (lower first)

## 4.7.0
* Added `structures` in World Whitelist Object
  * A string list of structures where the properties are applied
* Time Existed, Condition and Depth modifiers now use a ModifiableValue instead of a simple number
* Changed `override` to `keep_spawned` (so it now defaults to opposite behaviour)
* Fixed crash when missing `biomes` or `dimensions` in world whitelist object

## 4.6.0
* Updated to MC 1.20.1