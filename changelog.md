# Changelog

## 3.6.0
* Added `game_stages_unlocked` condition
  * Works like advancements condition
* Added Condition Modifier
  * In a ModifiableValue or Range you can specify a `conditions_modifier` list, containing ConditionModifiers
    * A condition modifier contains `condition` (same as the mob's one), `operation` (the operation to apply to the value) and `amount` to apply to the value if the `condition`s are met
* Added `damage_modifier` and `damage_modifier_operation` to on hit effects
  * Can be used to increase/decrease damage taken/dealt
* `health_left` in on hit effects now only applies if the mob has been attacked

## 3.5.3
* Fixed potion effects not being applied due to duration overflowing

## 3.5.2
* Fixed exception when missing `value` from modifiable value

## Beta 3.5.1
* Fix error if `advancements_done` were missing from `conditions`

## Beta 3.5.0
* Added `nbt` to mob properties.  
  Lets you set dynamically a mob nbt by specifying the nbt tag, the type and the value (which is a Range Object).  
  E.g. you can add a randomized amount of absorption to a mob with
    ```json
    {
      "mob_id": "minecraft:zombie",
      "nbt": [
        {
          "nbt_tag": "AbsorptionAmount",
          "type": "double",
          "value": {
            "min": 10,
            "max": 20
          }
        }
      ]
    }
    ```
* Added `raw_nbt` to mob properties.  
  Lets you set the nbt of the mob directly
* Fixed `time_existed_modifier` spawning glitched mobs when the mob was spawned on world generation: [NaN](https://youtu.be/Z3utiqgtFGo) 

## Beta 3.4.4
* Fixed crash with `potion effects` `duration` when Range object is used

## Beta 3.4.3
* Fixed crash with Range object

## Beta 3.4.2
* Merged `world` difficulty modifier into `difficulty_modifier`
  * Now this is how the difficulty modifier is used:  
    ```json
      {
        "value": 2,
        "difficulty_modifier": {
          "operation": "add",
          "hard": 1
        }   
      }
    ```
* Potions duration is now a range Min Max value instead of a fixed value
* Added `affect_max_only` to `pos_modifier`
* Fixed crash when `affects_max_only` was missing from `time_existed_modifier`
* Fixed Range and Modifiable Value Objects giving wrong error when `min` or `value` were missing

## Alpha 3.4.1
* Added TiCon Attack config option. When enabled, mobs will be able to use some Tinker tools modifiers when attacking (e.g. Sharpness, Fiery, Conducting when on fire, etc.)
* `ticon_modifiers`' `level` can now be omitted and will default to 1

## Alpha 3.4.0
* Added Tinkers Construct Materials and Modifiers setters
    * `ticon_modifiers` is a list of TiConModifier object, containing the modifier `id`, `level` as Range object and `chance` as Modifiable Value object
    * `ticon_materials` is a TiConMaterials object, containing either a list of `material` ids, or a Random TiConMaterial object `random`, containing the `max_tier` for the materials to apply 

## 3.3.5
* Added `with_levels` to `enchantments`
* Removed `local_difficulty` modifier as could hang the game. Also renamed `world_difficulty` to `world`
* Removed namespace wide mobs (e.g. `quark:*` is no longer valid as a mob)
* Added more info when invalid NBT is used
* Fixed nbt tag not being applied on items
* Fixed error/crash when using depth bonus and distance from spawn modifier was not present

## 3.3.4
* Added `world_whitelist` to the mob object, so you don't have to write the whitelist into every object
* Fixed presets not loading before mobs

## 3.3.3
* Fixed a bug where World whitelist was not checked correctly for potion effects and attributes
* Fixed a crash/error when deepness in World whitelist is not specified

## 3.3.2
* Now Requires InsaneLib 1.5.1

## 3.3.1
* Port to 1.18.2