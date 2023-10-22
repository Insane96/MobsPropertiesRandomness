# Changelog

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