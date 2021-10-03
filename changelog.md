# Changelog

## Upcoming
* Requires InsaneLib 1.3.0
  * Fixes follow range globally and removes the Leader Zombies bonus health
* Added a `fire_chance` property to creepers
* Added `round` property for ModifiableValue  
  After applying the modifiers the final value will be rounded to the specified decimal places. Things that require an integer (e.g. enchantment levels or potion amplifiers) are already rounded.
* Fixed potion effects not giving correct amplifiers (e.g. with min and max set to 1 and 2 you would always get amplifier 1)
* Fixed Difficulty modifier not being validated
* Fixed crash when not specifiyng drop chance
* Fixed missing difficulties' validation, causing mobs to not spawn

## Alpha 3.1.0
* Renamed "json" folder to "mobs". "json" folder is renamed automatically to "mobs" if present.
* Reworked random enchantments  
  "random" is a new Object in the Enchantment Object. You no longer have to write "random" in the "id" key.  
  The RandomEnchantment object contains `allow_curses`, `allow_treasures` and `list`, which contains a list of enchantments to chose from. If the list is omitted, the old behaviour will apply (random enchantment out of all the possible ones for the item)
* Added `chance` to attributes (much like potion effects). Also, health and follow range are now fixed for items too
* Added a `deepness` property to WorldWhitelist object. This Range Object dictates the min and max Y position at which the property will be applied
* Added a `structure_behaviour` property to the mob. Much like `spawner_behaviour` will filter mobs spawned from structures. Valid values are NONE, STRUCTURE_ONLY, NATURAL_ONLY.
* Range and Modifiable value objects can now be set with just a number, instead of having to create an object with the "value" (or "min") key inside.  
* Items Drop Chance is now a Modifiable Value
* Fixed mod crashing server on startup

## 3.0.5
* Fixed modifier names for mobs using modid:attribute_id instead of the modifier's name
* Fixed enchantments replacing already existing enchantments. Adding more than 1 enchantment was not possible
* Fixed modifiers using min values for max values

## 3.0.4
* Enchantments
  * Added a new `allow_incompatible` property. When true incompatible enchantments can be applied to the tool even when incompatible. E.g. Adding multishot to a crossbow will prevent you from adding quick charge. Setting quick charge's `allow_incompatible` to true, will make it apply
  * Fixed "random" enchantments applying incompatible enchantments
  * Fixed enchantments not applying at all

## 3.0.3
* Fixed Creeper's fuse not synced client-side.
* Drop Chance is now between 0 and 1 instead of 0-100
* Enchantment Level is no longer required. If omitted will use the min and max level of the enchantment to choose the final level.
* Renamed WorldDifficulty operations to ADD and MULTIPLY
* Renamed attribute_id to id
* Pos modifier's bonus and step are now required for eachother
* Chance in enchantments is no longer mandatory
* Chance in slot is no longer mandatory
* Fixed enchantments not applying to enchanted books
* Fixed missing allowCurses and Treasure in toString 

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
* Property modification is now done at the lowest priority so mods can add the "prevent processing" tag correctly. Also, the tag prevent processing has been changed to `mobspropertiesrandomness:processed`.
* Change attribute's is_flat to operation
* Renamed weight_difficulty to weight_modifier
* Groups no longer need a name. Group name is now given by file name