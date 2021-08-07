# Changelog

## Alpha 3.0.0
* The mod's JSON are now reloaded with the /reload command instead of begin a separate command
* Creepers with mod's applied potion effects no longer generate clouds
* Potion effects dimensions now require a string instead of a numeric id (e.g. "minecraft:overworld")
* The apply of properties is now done at the lowest priority so mods can add the prevent processing tag correctly. Also the tag prevent processing has been changed to `mobspropertiesrandomness:processed`.