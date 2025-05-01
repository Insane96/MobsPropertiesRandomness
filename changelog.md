# Changelog

## 4.11.0
* On Hit events (`on_attack` and `on_damaged`)
  * `damage_type` has been renamed to `direct_indirect`
  * Added `damage_type` allowing you to specify the damage type (or tag with `"#"`) that caused the damage.
    E.g. `"damage_type": "#minecraft:is_projectile"`
* Added a way to summon a mob with a preset
  * `{ForgeData:{"mobspropertiesrandomness:preset":"namespace:preset_id"}}`
  * Adding this NBT tag will not apply any other preset

## 4.10.16
* Pehkui scales now throw an error if the scale type is invalid

## 4.10.15
* Invalid items are now no****t loaded and logged instead of breaking the whole json
* Items `count` is now a Range object
  * So the wiki is no longer false advertisement

## 4.10.14
* Fixed item check not working and loading air
* Also optimized items
* Invalid presets are no longer added to the preset list
* Fixed NBT condition not working on some of mob's NBT (e.g. `Silent`, `NoGravity`, ...)

## 4.10.13
* Yet another try at fixing pehkui deadlock
  * Pehkui scaling is now applied 1 tick later than other properties hopefully making chunks already loaded by that time
* Mobs spawned by structure are again affected by pehkui scaling
  * Seems like that wasn't the problem

## 4.10.12
* Requires InsaneLib 1.15.0
* Fixed mobs and presets not throwing errors anymore (for some reason)

## 4.10.11
* Passenger entities have nothing to do with the deadlock. 
  * It seems to be caused by mobs generating within structures on world generation.
  * With this, mobs spawned by structure are no longer affected by pehkui scaling

## 4.10.10
* Passenger entities are no longer scaled with Pehkui
  * This is to prevent a deadlock still under investigation
* Added a debug log when applying properties to mobs

## 4.10.9
* Added `team` to properties to add an entity to a scoreboard team
* Fixed some NBT tags not getting applied
* Fixed `condition_modifiers` not working

## 4.10.8
* Fixed boss bar ignoring conditions

## 4.10.7
* ~~Removed `health_left`~~ Fixed `health_left` yet yet again not working

## 4.10.6
* Fixed `health_left` yet again not working

## 4.10.5
* Fixed `health_left` always defaulting to 0~1

## 4.10.4
* Added `distance_cap` to World Spawn Distance Modifier
* `items` in Equipment is no longer mandatory
  * Can be used to apply enchantments, nbt and other stuff to already equipped items 
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