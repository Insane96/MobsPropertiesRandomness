# Changelog

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