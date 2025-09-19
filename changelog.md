# Changelog

## 5.0.18
* Fixed presets no longer working in mobs

## 5.0.17
* Fixed presets trying to apply even if not valid

## 5.0.16
* Presets-in-presets are now supported
  * Presets are loaded and then validates so you can now use presets in presets without getting errors due to loading order

## 5.0.15
* Fixed enchant function's random not working if omitting the enchantments list

## 5.0.14
* Added `hardcore` condition
  * Condition is fulfilled if the world is in hardcore mode
* Enhanced errors when deserializing registry objects

## 5.0.13
* Fixed `damaged` event not getting triggered with no attackers (e.g. fall damage)

## 5.0.12
* Added `equipment` condition
  * Checks for `items` in the defined `slot`
* Renamed `attacked` event to `damaged`

## Beta 5.0.11
* Fixed conditions for Equipment property being checked twice

## Beta 5.0.10
* Fixed events not working in presets

## Beta 5.0.9
* Added "middle" as `bias` option
  * Number will be biased towards the middle of the range

## Beta 5.0.8
* Added `bias` to Range Object
  * Can be "min" and "max". 
  * "min" will bias the number generated towards the min value
  * "max" will bias the number generated towards the max value
  * If omitted, the number generated will be uniform between min and max

## Alpha 5.0.7
* Fixed another possible crash with the kill event

## Alpha 5.0.6
* Fixed another possible crash with the death event

## Alpha 5.0.5
* Fixed crash #163

## Alpha 5.0.4
* Added `mod_loaded` condition
  * Requires a `mod_id` to be specified and will match if the mod is present
* Migrated some Modifiable Values objects to Range objects. This doesn't require any changes to the properties files
  * `value` in Condition Modifier
  * `damage_modifier` in Attack and Attacked events
  * `amount_per_step` in Deepness Modifier
  * `easy`, `normal`, `hard` in Difficulty Modifier
  * `amount_per_blocks` in Distance From Spawn Modifier
  * `amount_per_minutes` in Time Played Modifier

## Alpha 5.0.3
* Fixed attack event target being switched

## Alpha 5.0.2
* Crash fix

## Alpha 5.0.1
* Item functions are now applied to the item already in the slot if no `items` or `loot_tables` are specified
* Fixed `attack` event applying properties to wrong target

## Alpha 5.0.0
The mod has been rewritten and reworked from scratch.  
Older data packs will no longer work, you'll have to migrate them as the json format has changed.
Check here for the wiki: https://github.com/Insane96/MobsPropertiesRandomness/wiki/%5BVersion-5%5D-Mobs

Also now fully server sided

Check out the tutorial series I made for this major update: https://www.youtube.com/watch?v=uxWGWm14OEY&list=PLrFaT7MqPrI7BzWPr_6b20mCVG5c2GEt3