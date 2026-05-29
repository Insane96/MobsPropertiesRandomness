## Upcoming
* Fixed summoning via `/mpr` command applying presets 5 times

## 6.0.1.3
* Fixed multiple boss bars flickering

## 6.0.1.2-beta
* Update to latest InsaneLib

## 6.0.1.1-beta
* Update to latest InsaneLib

## 6.0.1.0-beta
* Added `set_component` item function
  * You can set whatever data component you want on an item. The following example from the example data pack will make the target equip an iron ingot that can be eaten and used as a pickaxe. Numeric values can be replaced with #range object to generate random values.
  ```json
  {
    "property": "equipment",
    "slot": "offhand",
    "items": [
      {
        "item": "minecraft:iron_ingot",
        "functions": [
          {
            "function": "set_component",
            "components": {
              "minecraft:food": {
                "nutrition": {"#range": {"min": 2, "max": 6}},
                "saturation": {"#range": 0.5}
              },
              "minecraft:tool": {
                "rules": [
                  {
                    "blocks": "#mineable/pickaxe",
                    "speed": 6,
                    "correct_for_drops": true
                  }
                ]
              },
              "minecraft:max_damage": 453,
              "minecraft:damage": 0
            }
          },
          {
            "function": "set_drop_chance",
            "drop_chance": 2
          }
        ]
      }
    ]
  }
  ```

## 6.0.0.0-alpha
Ported to 1.21.1

Mod support will be added back in the future. Let's see how this goes first.  
I've also updated the Example Data Pack which was broken in 1.20.1 too.
The only difference between 1.20.1 and 1.21.1 are attribute modifier operations.

### Missing
* Set (Raw) NBT for items
* Scaling (pehkui is no longer needed (and seems to no longer work?) as scaling is now an attribute)
* Serene Seasons support
* Game stages (hasn't updated past 1.20.3)
