# Changelog

## Upcoming
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
* Fixed crash when `affects_max_only` was missing from `time_existed_multiplier`
* Fixed Range and Modifiable Value Objects giving wrong error when `min` or `value` were missing

## Alpha 3.4.1
* Added TiCon Attack config option. When enabled, mobs will be able to use some of the Tinker tools modifiers when attacking (e.g. Sharpness, Fiery, Conducting when on fire, etc.)
* `ticon_modifiers`' `level` can now be omitted and will default to 1

## Alpha 3.4.0
* Added Tinkers Construct Materials and Modifiers setters
    * `ticon_modifiers` is a list of TiConModifier object, containing the modifier `id`, `level` as Range object and `chance` as Modifiable Value object
    * `ticon_materials` is a TiConMaterials object, containing either a list of `material` ids, or a Random TiConMaterial object `random`, containing the `max_tier` for the materials to apply 

## 3.3.5
* Added `with_levels` to `enchantments`
* Removed `local_difficulty` modifier as could hang the game. Also renamed `world_difficulty` to `world`
* Removed namespace wide mobs (e.g. `quark:*` is no longer valid as a mob)
* Added more infos when invalid NBT is used
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