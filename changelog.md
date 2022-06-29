# Changelog

## Upcoming
* Removed `local_difficulty` modifier as could hang the game. Also renamed `world_difficulty` to `world`
* Removed namespace wide mobs (e.g. `quark:*` is no longer valid as a mob)
* Added more infos when invalid NBT is used
* Fixed nbt tag not being applied on items

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