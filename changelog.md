# Changelog

## Beta 5.1.0.0
* Added `block_on` condition
  * Condition is fulfilled if the target is on one of the `blocks` (list of blocks or block tags) specified
* Added `block_in` condition
  * Condition is fulfilled if the target's bounding box is inside one of the `blocks` (list of blocks or block tags) specified
* Added `season` condition
  * Requires Serene Seasons
  * Condition is fulfilled if the season matches one of the `seasons` (list of seasons) specified
* Added `damage` property
    * Damages the target. Can specify `damage_type`
* Added `despawn` property
  * Despawns the target
* Added `function` property
  * Executes an mcfunction (`function`)
  * Removed function from events, use this new property
* Reworked Range object
  * `min` and `max` are now Modifiable Values objects, so you can apply modifiers to either of them
  * The range object still has `modifiers` that will apply to both `min` and `max`
  * Removed `modifiers_behaviour`
    * Example migration for max_only
      ```json
      {
        "value": {
          "min": 1,
          "max": 2,
          "modifiers": [
              {
                  "modifier": "distance_from_spawn",
                  "operation": "add",
                  "amount_per_blocks": 0.01,
                  "blocks": 100
              }
          ],
          "modifiers_behaviour": "max_only"
        }
      }
      ```  
      Becomes
      ```json
      {
        "value": {
          "min": 1,
          "max": {
              "value": 2,
              "modifiers": [
                  {
                      "modifier": "distance_from_spawn",
                      "operation": "add",
                      "amount_per_blocks": 0.01,
                      "blocks": 100
                  }
              ]
          }
        }
      }
      ```
* Enhanced potion effect property
  * `duration` and `amplifier` can now use a new Stackable Object. `amplifier` is no longer between 0 and 255 but between 1 and 256
    * `value`: Range Object
    * `stack`: false by default, if true, the duration or amplifier will stack each time the property is applied
    * `cap`: Range Object defining the min and max value can reach with stacking  
  * This example applies between 1 and 2 levels of swiftness for 10 seconds and each time it's applied, the amplifier will increase by 1/2 levels
    ```json
    {
        "property": "potion_effect",
        "effect": "minecraft:swiftness",
        "duration": 10,
        "amplifier": {
            "value": {
                "min": 1,
                "max": 2
            },
            "stack": true
        }
    }
    ```

## 5.0.24
* Added `effect` condition
  * Checks if the entity has the effect and the amplifier (optional)
    This example condition will be fulfilled if the entity has slowness with amplifier at least III
    ```json
    {
        "condition": "effect",
        "effect": "slowness",
        "amplifier": {
            "min": 2, 
            "max": 255
        }
    }
    ```

## 5.0.23
* Equipment condition now supports item tags
  * You could only use a list of items, now you can use a single item
    ```json
    {
        "condition": "equipment",
        "slot": "mainhand",
        "items": "minecraft:crossbow"
    }
    ```
    item tag
    ```json
    {
        "condition": "equipment",
        "slot": "mainhand",
        "items": "#minecraft:axes"
    }
    ```
    or item list
    ```json
    {
        "condition": "equipment",
        "slot": "mainhand",
        "items": [
            "minecraft:crossbow",
            "minecraft:bow"
        ]
    }
    ```

## 5.0.22
* Added distance object to `has_target` condition
* Fixed boss bar being removed when closing the world

## 5.0.21
* Fixed invalid Potion Effects and attributes preventing targets from spawning (errored)
* Invalid registry objects (notably enchantments) that aren't valid are now logged and not added to deserialized lists

## 5.0.20
* Invalid items are now logged
  * E.g. `[WARN] Invalid item: minecaft:stone_pickaxe. Ignored.`
* Fixed not being able to clear an item from slots with `minecraft:air`

## 5.0.19
* Fixed multiple tick events not working

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