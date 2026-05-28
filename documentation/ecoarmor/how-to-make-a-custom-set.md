---
title: How to make an Armor Set
sidebar_position: 2
---

## Creating an Armor Set
Each set is its own config file, placed in the `/sets/` folder, and you can add or remove them as you please. There's an example config called `_example.yml` to help you out!

The ID of the Armor Set is the file name. This is what you use in commands and in the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system).
ID's must be lowercase letters, numbers, and underscores only.

## Example Armor Set Config

```yaml
effects:
  - id: damage_multiplier
    args:
      multiplier: 1.25
    triggers:
      - melee_attack
      - bow_attack
      - trident_attack

amount_for_set: 4

partialEffects:
  enabled: true
  stacked: false
  disabledByFull: false
  amounts:
    2:
      effects:
        - id: damage_multiplier
          args:
            multiplier: 1.15
          triggers:
            - melee_attack
            - bow_attack
            - trident_attack
    3:
      effects:
        - id: damage_multiplier
          args:
            multiplier: 1.20
          triggers:
            - melee_attack
            - bow_attack
            - trident_attack

advancedEffects:
  - id: damage_multiplier
    args:
      multiplier: 1.25
    triggers:
      - melee_attack
      - bow_attack
      - trident_attack
  - id: damage_multiplier
    args:
      multiplier: 0.9
    triggers:
      - take_damage

advancedLore: 
  - ''
  - "<gradient:f12711>&lADVANCED BONUS</gradient:f5af19>"
  - "&8» &6Take 10% less damage"
  - "&8&oRequires full set to be worn"
 
sounds:
  equip:
    enabled: false
    sound: ""
    volume: 1
    pitch: 1
  advancedEquip:
    enabled: false
    sound: ""
    volume: 1
    pitch: 1
  unequip:
    enabled: false
    sound: ""
    volume: 1
    pitch: 1
 
shard:
  item: prismarine_shard unbreaking:1 hide_enchants 
  name: "<GRADIENT:f12711>Advancement Shard:</GRADIENT:f5af19> &cReaper" 
  lore: 
    - "&8Drop this onto &cReaper Armor"
    - "&8to make it <GRADIENT:f12711>Advanced</GRADIENT:f5af19>."
  craftable: false 
  crafting-permission: "permission" 
  recipe: 
    - prismarine_shard
    - ecoarmor:set_reaper_helmet
    - prismarine_shard
     
    - ecoarmor:set_reaper_chestplate
    - nether_star
    - ecoarmor:set_reaper_leggings
     
    - prismarine_shard
    - ecoarmor:set_reaper_boots
    - prismarine_shard
    
helmet:
  item: leather_helmet color:#303030 hide_dye
  name: "&cReaper Helmet" 
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Helmet" 
  lore: 
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true 
  crafting-permission: "permission" 
  recipe: 
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
     
    - nether_star
    - netherite_helmet
    - nether_star
     
    - air
    - nether_star
    - air
  defaultTier: default 
  effectiveDurability: 2048  
  effects: []
  advancedEffects: []
  conditions: [] 

chestplate:
  item: leather_chestplate color:#303030 hide_dye
  name: "&cReaper Chestplate"
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Chestplate"
  lore:
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true
  crafting-permission: "permission"
  recipe:
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
     
    - nether_star
    - netherite_chestplate
    - nether_star
     
    - air
    - nether_star
    - air
  defaultTier: default
  effectiveDurability: 2048
  effects: []
  advancedEffects: []
  conditions: []

elytra:
  item: elytra
  name: "&cReaper Elytra"
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Elytra"
  lore:
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true
  crafting-permission: "permission"
  recipe:
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
     
    - nether_star
    - elytra
    - nether_star
     
    - air
    - nether_star
    - air
  defaultTier: default
  effectiveDurability: 2048
  effects: []
  advancedEffects: []
  conditions: []

leggings:
  item: leather_leggings color:#303030 hide_dye
  name: "&cReaper Leggings"
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Leggings"
  lore:
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true
  crafting-permission: "permission"
  recipe:
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
     
    - nether_star
    - netherite_leggings
    - nether_star
     
    - air
    - nether_star
    - air
  defaultTier: default
  effectiveDurability: 2048
  effects: []
  advancedEffects: []
  conditions: []

boots:
  item: leather_boots color:#303030 hide_dye
  name: "&cReaper Boots"
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Boots"
  lore:
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true
  crafting-permission: "permission"
  recipe:
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
     
    - nether_star
    - netherite_boots
    - nether_star
      
    - air
    - nether_star
    - air
  defaultTier: default
  effectiveDurability: 2048
  effects: []
  advancedEffects: []
  conditions: []
```

## Understanding all the sections:
### The Set Effects Section

This is the config section for the set effects, which are the effects that apply when the full set is worn.

:::danger Effects Section

The effects section is the core functionality of the armor set. You can configure effects, conditions, filters, mutators and triggers in this section to run whilst the set is active.

Check out [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) to understand how to configure this section correctly.

For more advanced users or setups, you can configure chains in this section to string together different effects under one trigger. Check out [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) for more info.

:::

```yaml
# The effects of the set (i.e. the functionality)
# See here: https://plugins.auxilor.io/effects/configuring-an-effect
effects:
  - id: damage_multiplier
    args:
      multiplier: 1.25
    triggers:
      - melee_attack
      - bow_attack
      - trident_attack

amount_for_set: 4 # The amount of pieces required to be wearing for the set effects to activate.
```

### The Partial Set Effects Section
```yaml
partialEffects:
  enabled: true # If partial set effects are enabled. These are the effects that apply when only part of the set is worn.
  stacked: false # If true, the effects will stack with each piece of armor. If false, the effects will not stack and only the highest amount's effects will apply.
  disabledByFull: false # If true, the partial effects will be disabled when the full set is worn. If false, the partial effects will still apply even when the full set is worn.
  amounts: 
    2: # The amount of pieces required to be wearing for these effects to activate.
      effects:
        - id: damage_multiplier
          args:
            multiplier: 1.15
          triggers:
            - melee_attack
            - bow_attack
            - trident_attack
```

### The Advanced Effects and Lore Section

```yaml
# The advanced effects of the set (i.e. the functionality) - additional effects that only apply when the set is advanced.
# See here: https://plugins.auxilor.io/effects/configuring-an-effect
advancedEffects:
  - id: damage_multiplier
    args:
      multiplier: 1.25
    triggers:
      - melee_attack
      - bow_attack
      - trident_attack
  - id: damage_multiplier
    args:
      multiplier: 0.9
    triggers:
      - take_damage

advancedLore: # Lore to be added to the armor piece when it has been advanced.
  - ''
  - "<gradient:f12711>&lADVANCED BONUS</gradient:f5af19>"
  - "&8» &6Take 10% less damage"
  - "&8&oRequires full set to be worn"
```

### The Additional Set Options Section
```yaml
sounds:
  equip:
    enabled: false # If a sound should play when armor is equipped.
    sound: "" # The sound to play, sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
    volume: 1
    pitch: 1
  advancedEquip:
    enabled: false # If a sound should play when advanced armor is equipped.
    sound: "" # The sound to play, sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
    volume: 1
    pitch: 1
  unequip:
    enabled: false # If a sound should play when armor is unequipped.
    sound: "" # The sound to play, sounds: https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
    volume: 1
    pitch: 1
```

### The Advancement Shard Section

This is the config section for the Advancement Shard, which is used to upgrade the armor set to its advanced version.

```yaml
shard:
  # The shard item, read more here: https://plugins.auxilor.io/the-item-lookup-system
  item: prismarine_shard unbreaking:1 hide_enchants
  name: "<GRADIENT:f12711>Advancement Shard:</GRADIENT:f5af19> &cReaper" # The in-game name of the shard.
  lore: # The lore shown in-game on the shard. Set to `lore: []` to remove lore.
    - "&8Drop this onto &cReaper Armor"
    - "&8to make it <GRADIENT:f12711>Advanced</GRADIENT:f5af19>."
  craftable: false # If the shard is craftable
  crafting-permission: "permission" # (Optional) The permission required to craft this recipe.
  recipe: # The recipe, read here for more: https://plugins.auxilor.io/the-item-lookup-system/recipes
    - prismarine_shard
    - ecoarmor:set_reaper_helmet
    - prismarine_shard
     
    - ecoarmor:set_reaper_chestplate
    - nether_star
    - ecoarmor:set_reaper_leggings
     
    - prismarine_shard
    - ecoarmor:set_reaper_boots
    - prismarine_shard
```

:::tip

We support shaped and shapeless recipes. Check out [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for more info on how to configure these.

:::

### The Individual Armor Piece Section

You can also configure each armor piece individually, such as lore, recipes, piece-specific effects, and more. <br/>
The below example is just for the helmet, but it is the same for the chestplate, leggings, boots, and elytra.

```yaml
helmet:
  # The armor item, read more here: https://plugins.auxilor.io/the-item-lookup-system
  item: leather_helmet color:#303030 hide_dye
  name: "&cReaper Helmet" # The name shown in-game.
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Helmet" # The advanced name shown in-game.
  lore: # The lore shown in-game. Set to `lore: []` to remove lore.
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true # If the armor piece is craftable
  crafting-permission: "permission" # (Optional) The permission required to craft this recipe.
  recipe: # The recipe, read here for more: https://plugins.auxilor.io/the-item-lookup-system/recipes
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
     
    - nether_star
    - netherite_helmet
    - nether_star
     
    - air
    - nether_star
    - air
  defaultTier: default # The default tier of the armor

  # The actual item durability isn't set (because it can't be changed), but instead
  # this scales how quickly the item wears to act as if it had this durability.
  # For example, let's say the actual durability is 350, but you set this to 700,
  # it will wear at half the normal rate.

  effectiveDurability: 2048  # Optional, set the durability

  # The effects of the item (i.e. the functionality)
  # See here: https://plugins.auxilor.io/effects/configuring-an-effect
  effects: []
  advancedEffects: []

  # The conditions required for the effects to activate
  conditions: [] # The conditions for the effects to be run
```

:::tip

We support shaped and shapeless recipes. Check out [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for more info on how to configure these.

:::

:::danger Frequently Asked Questions:
**Why do I get a piece of stone?** <br/>
- This is because an armor piece is missing, or configured incorrectly. In most cases, users remove the elytra section.

**Can I remove the elytra?** <br/>
- No, the elytra section is required. If you don't want to give players the elytra, you can use commands to give individual pieces.
:::

## Internal Placeholders

| Placeholder | Value                      |
| ----------- | -------------------------- |
| `%tier%`    | The tier of the armor set. |

<hr/>

## Default configs
The default configs can be found [here](https://github.com/Auxilor/EcoArmor/tree/master/eco-core/core-plugin/src/main/resources/sets). <br/>
You can find additional user-created configs on [lrcdb](https://lrcdb.auxilor.io/).