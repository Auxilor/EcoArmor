---
title: "How to Make an Armor Set"
sidebar_position: 2
---

An **armor set** is a group of pieces that grant **set effects** while the full set is worn, with optional **partial effects** for wearing only part of it and an **advanced** upgrade that unlocks bonus effects. Each set is one YAML file in the `/sets/` folder. This page builds one from scratch and explains every part of the config.

## Quick start

1. Create a file in `plugins/EcoArmor/sets/`, e.g. `reaper.yml` (copy `_example.yml` as a starting point). The file name becomes the set ID.
2. Define the set `effects` and `amount_for_set` (how many pieces must be worn for the bonus).
3. Configure the `helmet`, `chestplate`, `leggings`, `boots`, and `elytra` pieces (all five are required).
4. Run `/ecoarmor reload`.
5. Give yourself the set with `/ecoarmor give <you> set:reaper` and equip it; the set bonus applies once you wear enough pieces.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real set. You can also organise sets into subfolders inside `sets/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the set ID. You use it in commands and in the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system) (e.g. `ecoarmor:set_reaper_helmet`).

:::warning ID rules
IDs may contain lowercase letters, numbers, and underscores only. Anything else will break the set.
:::

## The structure of an armor set

A set config has six parts:

| Part | What it controls |
| --- | --- |
| **Set effects** | The bonus that applies when the full set is worn. |
| **Partial effects** | Weaker bonuses for wearing only part of the set. |
| **Advanced effects and lore** | Extra effects and lore unlocked once the set is advanced. |
| **Sounds** | Sounds played on equip, advanced equip, and unequip. |
| **Advancement shard** | The item players use to advance the set. |
| **Armor pieces** | Each piece's item, recipe, lore, tier, and per-piece effects. |

Here is a complete set with every part in place:

```yaml
# === Set effects: the bonus when the full set is worn ===
effects:
  - id: damage_multiplier
    args:
      multiplier: 1.25
    triggers:
      - melee_attack
      - bow_attack
      - trident_attack
amount_for_set: 4 # How many pieces must be worn for the set effects to activate

# === Partial effects: bonuses for wearing only part of the set ===
partialEffects:
  enabled: true # Whether partial effects apply at all
  stacked: false # If true, each tier's effects stack; if false, only the highest matched amount applies
  disabledByFull: false # If true, partial effects switch off once the full set is worn
  amounts:
    2: # Pieces worn needed to trigger this block of effects
      effects:
        - id: damage_multiplier
          args:
            multiplier: 1.15
          triggers:
            - melee_attack
            - bow_attack
            - trident_attack

# === Advanced effects and lore: unlocked once the set is advanced ===
advancedEffects:
  - id: damage_multiplier
    args:
      multiplier: 0.9
    triggers:
      - take_damage
advancedLore: # Lore appended to each piece once it is advanced
  - ''
  - "<gradient:f12711>&lADVANCED BONUS</gradient:f5af19>"
  - "&8» &6Take 10% less damage"
  - "&8&oRequires full set to be worn"

# === Sounds: played on equip, advanced equip, and unequip ===
sounds:
  equip:
    enabled: false # Whether a sound plays when the set is equipped
    sound: "" # Sound key, see https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
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

# === Advancement shard: the item that advances the set ===
shard:
  item: prismarine_shard unbreaking:1 hide_enchants # Shard item, see https://plugins.auxilor.io/the-item-lookup-system
  name: "<GRADIENT:f12711>Advancement Shard:</GRADIENT:f5af19> &cReaper" # In-game name
  lore: # Set to `lore: []` to remove lore
    - "&8Drop this onto &cReaper Armor"
    - "&8to make it <GRADIENT:f12711>Advanced</GRADIENT:f5af19>."
  craftable: false # Whether the shard is craftable
  crafting-permission: "permission" # Optional; permission required to craft this recipe
  recipe: # See https://plugins.auxilor.io/the-item-lookup-system/recipes
    - prismarine_shard
    - ecoarmor:set_reaper_helmet
    - prismarine_shard
    - ecoarmor:set_reaper_chestplate
    - nether_star
    - ecoarmor:set_reaper_leggings
    - prismarine_shard
    - ecoarmor:set_reaper_boots
    - prismarine_shard

# === Armor pieces: one block each for helmet, chestplate, leggings, boots, elytra ===
# All five pieces share the same fields; the helmet is shown here in full.
helmet:
  item: leather_helmet color:#303030 hide_dye # Base item, see https://plugins.auxilor.io/the-item-lookup-system
  name: "&cReaper Helmet" # In-game name
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Helmet" # Name shown once advanced
  lore: # Set to `lore: []` to remove lore
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true # Whether this piece is craftable
  crafting-permission: "permission" # Optional; permission required to craft this recipe
  recipe: # See https://plugins.auxilor.io/the-item-lookup-system/recipes
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
    - nether_star
    - netherite_helmet
    - nether_star
    - air
    - nether_star
    - air
  defaultTier: default # Tier this piece starts on
  effectiveDurability: 2048 # Optional; scales how quickly the item wears instead of changing real durability
  effects: [] # Effects that run only while this piece is worn
  advancedEffects: [] # Per-piece effects unlocked once advanced
  conditions: [] # Conditions required for this piece's effects to run
```

### Set effects

The set effects are the core of the armor set, the functionality that runs while the full set is worn.

```yaml
effects:
  - id: damage_multiplier
    args:
      multiplier: 1.25
    triggers:
      - melee_attack
      - bow_attack
      - trident_attack
amount_for_set: 4 # How many pieces must be worn for the set effects to activate
```

:::danger Effects are their own system
Effects, conditions, filters, mutators, triggers, and chains are a shared eco system, not specific to EcoArmor, with hundreds of options. They are **not** documented here, so see the dedicated guides:

- [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) is the full effect, trigger, and condition reference.
- [Configuring an Effect Chain](https://plugins.auxilor.io/effects/configuring-a-chain) strings multiple effects under one trigger for advanced sets.
:::

### Partial effects

Partial effects reward players for wearing part of a set, before they complete it.

```yaml
partialEffects:
  enabled: true # Whether partial effects apply at all
  stacked: false # If true, each tier's effects stack; if false, only the highest matched amount applies
  disabledByFull: false # If true, partial effects switch off once the full set is worn
  amounts:
    2: # Pieces worn needed to trigger this block of effects
      effects:
        - id: damage_multiplier
          args:
            multiplier: 1.15
          triggers:
            - melee_attack
            - bow_attack
            - trident_attack
```

### Advanced effects and lore

Advanced effects apply on top of the set effects once every piece has been advanced with a shard. `advancedLore` is appended to each piece's lore at the same time.

```yaml
advancedEffects:
  - id: damage_multiplier
    args:
      multiplier: 0.9
    triggers:
      - take_damage
advancedLore: # Lore appended to each piece once it is advanced
  - ''
  - "<gradient:f12711>&lADVANCED BONUS</gradient:f5af19>"
  - "&8» &6Take 10% less damage"
  - "&8&oRequires full set to be worn"
```

### Sounds

Optional sounds played as players equip and unequip the set.

```yaml
sounds:
  equip:
    enabled: false # Whether a sound plays when the set is equipped
    sound: "" # Sound key, see https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Sound.html
    volume: 1
    pitch: 1
  advancedEquip: # Plays when an advanced set is equipped
    enabled: false
    sound: ""
    volume: 1
    pitch: 1
  unequip:
    enabled: false
    sound: ""
    volume: 1
    pitch: 1
```

### Advancement shard

The shard is the in-game item players drop onto a piece to advance it.

```yaml
shard:
  item: prismarine_shard unbreaking:1 hide_enchants # Shard item, see https://plugins.auxilor.io/the-item-lookup-system
  name: "<GRADIENT:f12711>Advancement Shard:</GRADIENT:f5af19> &cReaper" # In-game name
  lore: # Set to `lore: []` to remove lore
    - "&8Drop this onto &cReaper Armor"
    - "&8to make it <GRADIENT:f12711>Advanced</GRADIENT:f5af19>."
  craftable: false # Whether the shard is craftable
  crafting-permission: "permission" # Optional; permission required to craft this recipe
  recipe: # See https://plugins.auxilor.io/the-item-lookup-system/recipes
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
We support both shaped and shapeless recipes. See [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for how to configure them.
:::

### Armor pieces

Each piece (`helmet`, `chestplate`, `leggings`, `boots`, `elytra`) has its own block with the same fields, so you can give pieces different items, recipes, lore, and even piece-specific effects. The helmet is shown here; the rest follow the same shape.

```yaml
helmet:
  item: leather_helmet color:#303030 hide_dye # Base item, see https://plugins.auxilor.io/the-item-lookup-system
  name: "&cReaper Helmet" # In-game name
  advancedName: "<GRADIENT:f12711>Advanced</GRADIENT:f5af19>&c Reaper Helmet" # Name shown once advanced
  lore: # Set to `lore: []` to remove lore
    - "&c&lREAPER SET BONUS"
    - "&8» &cDeal 25% more damage"
    - "&8&oRequires full set to be worn"
    - ''
    - "&fTier: %tier%"
    - "&8&oUpgrade with an Upgrade Crystal"
  craftable: true # Whether this piece is craftable
  crafting-permission: "permission" # Optional; permission required to craft this recipe
  recipe: # See https://plugins.auxilor.io/the-item-lookup-system/recipes
    - ecoitems:armor_core ? air
    - nether_star
    - ecoitems:armor_core ? air
    - nether_star
    - netherite_helmet
    - nether_star
    - air
    - nether_star
    - air
  defaultTier: default # Tier this piece starts on
  effectiveDurability: 2048 # Optional; scales how quickly the item wears instead of changing real durability
  effects: [] # Effects that run only while this piece is worn
  advancedEffects: [] # Per-piece effects unlocked once advanced
  conditions: [] # Conditions required for this piece's effects to run
```

:::danger The elytra is required
All five pieces, including the elytra, must be present. Removing the elytra block is the most common cause of a set spawning a block of stone. If you don't want players to use the elytra, leave the block in and give individual pieces with commands instead.
:::

## Internal placeholders

These placeholders are provided by EcoArmor and can be used in piece lore:

| Placeholder | Value |
| --- | --- |
| `%tier%` | The tier of the armor piece. |

:::tip Troubleshooting
- **Players get a block of stone instead of armor?** A piece is missing or misconfigured, usually a removed elytra block. Keep all five piece blocks.
- **Set bonus never applies?** Check `amount_for_set` against how many pieces the player is wearing, and that all worn pieces belong to the same set.
- **Advanced effects don't trigger?** Every piece in the set must be advanced with a shard before `advancedEffects` apply.
- **Recipe doesn't show up?** Confirm `craftable: true` and that the player has any `crafting-permission` you set.
:::

<hr/>

## Where to go next

- **Tiers:** [How to Make a Tier](how-to-make-a-custom-tier) for the per-piece attribute modifiers and upgrade crystals.
- **Effects:** [Configuring an Effect](https://plugins.auxilor.io/effects/configuring-an-effect) for everything in the `effects` sections.
- **Default sets:** the shipped configs live [on GitHub](https://github.com/Auxilor/EcoArmor/tree/master/eco-core/core-plugin/src/main/resources/sets).
- **Community configs:** browse and share more on [lrcdb](https://lrcdb.auxilor.io/).