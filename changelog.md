# Changelog

## Beta 3.0.2
* Now requires InsaneLib 1.2.1
* `difficulty_modifier` is now specified in Range objects (min max) and in chance objects
* Added a spawner_behaviour tag to the mob  
  (Default) NONE: Normal behaviour  
  SPAWNER_ONLY: Only mobs from spawners will be affected by the property modifiers  
  NATURAL_ONLY: Only mobs not from spawners will be affected by the property modifiers
* Chance is now a modifiable value object. Now uses `value` instead of `amount`
* Added `pos_modifier` in Range objects (min max) and in modifiable values objects (chance and weight). Item no longer has a `world_difficulty` modifier (as it's in the Weight object)
* Added Phantom Size property
* Moved `dimensions` and `biomes` whitelist to a `world_whitelist` object
* Fixed Slot not checking for world whitelist correctly
* Fixed attribute names breaking them
* Renamed `difficulty` to `world_difficulty`
* Fixed `world_difficulty` modifier defaulting to +1 instead of +0 when additive
* Game/Server no longer crashes when InsaneLib is missing

## Alpha 3.0.1
* Revamped Difficulty modifier
The difficulty object is now `difficulty_modifier` containing a modifier for difficulty, for local difficulty and if should affect the max value only.  
Weight Modifier now uses the `difficulty` object (`operation`, `easy`, `normal`, `hard`)  
Chance and Attributes make use of the new `difficulty_modifier`
* `override_vanilla` has been renamed to `override`
* Chance is now required with a value between 0 and 1 instead of 0 and 100
* Added `allow_curses` and `allow_treasure` for "random" enchantment
* Debug is now disabled by default and loggin is now slightly better
* Fixed Groups loading too late
* JSON and Groups folders are now created on minecraft load

## Alpha 3.0.0
* The mod's JSON are now reloaded with the /reload command instead of begin a separate command
* Creepers with mod's applied potion effects no longer generate clouds
* Potion effects dimensions now require a string instead of a numeric id (e.g. "minecraft:overworld")
* The apply of properties is now done at the lowest priority so mods can add the prevent processing tag correctly. Also the tag prevent processing has been changed to `mobspropertiesrandomness:processed`.
* Change attribute's is_flat to operation
* Renamed weight_difficulty to weight_modifier
* Groups no longer need a name. Group name is now given by file name