# Changelog

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