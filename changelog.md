## 6.1.0.0-alpha
BREAKING CHANGES Please read carefully
* More presets!
  * PLEASE NOTE that mobs presets now live in `data/mobs_properties_randomness/presets/mobs/` instead of `data/mobs_properties_randomness/presets/`.
  * This version introduces modifiers, conditions and functions presets.  
    These are used if you have multiple conditions, modifiers and functions that repeat, and instead you can write once and reference.
  * Example, you want to apply a global difficulty scaling modifier to many attribute values, but this is repetitive
    ```json 
    [
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.max_health",
        "id": "ns:bonus_health",
        "amount": {
            "value": 1,
            "modifiers": [
                {
                    "modifier": "difficulty", 
                    "operation": "multiply", 
                    "easy": 0.75, 
                    "hard": 1.5 
                }
            ]
        },
        "operation": "add_multiplied_base"
    },
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.movement_speed",
        "id": "ns:movement_speed",
        "amount": {
            "value": 0.10,
            "modifiers": [
                { 
                    "modifier": "difficulty", 
                    "operation": "multiply", 
                    "easy": 0.75, 
                    "hard": 1.5 
                }
            ]
        },
        "operation": "add_multiplied_base"
    }
    ]
    ```
    To avoid all this redundancy, you can use a modifier preset. in `data/ns/presets/modifiers/difficulty_scaling.json`. A modifier preset can contain as many modifiers as you want.
    ```json
    [
        { "modifier": "difficulty", "operation": "multiply", "easy": 0.75, "hard": 1.5 }
    ]
    ```
    So the properties can become
    ```json 
    [
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.max_health",
        "id": "ns:bonus_health",
        "amount": {
            "value": 1,
            "modifiers": [
                { "preset": "ns:difficulty_scaling" }
            ]
        },
        "operation": "add_multiplied_base"
    },
    {
        "property": "attribute_modifier",
        "attribute": "minecraft:generic.movement_speed",
        "id": "ns:movement_speed",
        "amount": {
            "value": 0.10,
            "modifiers": [
                { "preset": "ns:difficulty_scaling" }
            ]
        },
        "operation": "add_multiplied_base"
    }
    ]
    ```
    This is a quite easy example, but another real example is:  
    [This is something that will heavily benefit from using presets](https://github.com/Insane96/EnhancedAI/blob/febf0ebe3db34dce1a0d1e9892afb6a2bd8243e4/src/main/resources/integrated_packs/mpr_integration/data/enhancedai/mobs_properties_randomness/mobs/overworld_zombies_equipment.json).
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
