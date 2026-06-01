---
title: "How to Make a Tier"
sidebar_position: 3
---

A **tier** is a set of **attribute modifiers** applied to a single armor piece, e.g. extra armor, toughness, or movement speed. Tiers apply per piece, even without the full set, and players move between them with **Upgrade Crystals**. Each tier is one YAML file in the `/tiers/` folder. This page builds one and explains every part.

## Quick start

1. Create a file in `plugins/EcoArmor/tiers/`, e.g. `netherite.yml` (copy `_example.yml` as a starting point). The file name becomes the tier ID.
2. Set the `display` name and any `requiresTiers` prerequisites.
3. Fill in the `properties` for each piece with the attribute values you want.
4. Run `/ecoarmor reload`.
5. Give yourself a crystal with `/ecoarmor give <you> crystal:netherite`, drop it onto a piece, and check the piece's stats change.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real tier. You can also organise tiers into subfolders inside `tiers/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the tier ID. You use it in commands and in the `defaultTier` field of a set config. Crystal items resolve through the [Item Lookup System](https://plugins.auxilor.io/the-item-lookup-system) as `ecoarmor:upgrade_crystal_<id>`.

:::warning ID rules
IDs may contain lowercase letters, numbers, and underscores only. Anything else will break the tier.
:::

## The structure of a tier

A tier config has three parts:

| Part | What it controls |
| --- | --- |
| **Info** | The display name and which tier must come before this one. |
| **Upgrade crystal** | The in-game item that applies the tier. |
| **Properties** | The per-piece attribute modifiers the tier grants. |

Here is a complete tier with every part in place:

```yaml
# === Info: display name and prerequisites ===
display: "&c&lNETHERITE" # In-game tier display name
requiresTiers: # Tiers a piece must already have before this one can be applied
  - diamond

# === Upgrade crystal: the item that applies this tier ===
crystal:
  item: end_crystal # Crystal item, see https://plugins.auxilor.io/the-item-lookup-system
  name: "&cNetherite Upgrade Crystal" # In-game name
  lore: # Set to `lore: []` to remove lore
    - "&8Drop this onto an armor piece"
    - "&8to set its tier to:"
    - "&c&lNETHERITE"
    - ''
    - "&8&oRequires the armor to already have Diamond tier"
  craftable: true # Whether the crystal is craftable
  recipe: # See https://plugins.auxilor.io/the-item-lookup-system/recipes
    - air
    - netherite_ingot
    - air
    - netherite_ingot
    - ecoarmor:upgrade_crystal_diamond
    - netherite_ingot
    - air
    - netherite_ingot
    - air
  giveAmount: 1 # Optional; how many crystals the recipe yields

# === Properties: per-piece attribute modifiers (helmet shown; repeat per piece) ===
properties:
  helmet:
    armor: 3 # Armor points (≈8 = netherite; >20 per piece has heavy diminishing returns)
    toughness: 3 # Armor toughness (≈3 = netherite; >12 to 15 per piece has little benefit)
    knockbackResistance: 1 # Knockback resist 0 to 100 (100 = immune; 40 to 60 ≈ netherite)
    speedPercentage: 0 # Move speed % (-50 to +50 recommended)
    attackSpeedPercentage: 0 # Attack speed % (-50 to +100; very high values can feel broken)
    attackDamagePercentage: 0 # Damage % (-50 to +100; high values trivialize combat)
    attackKnockbackPercentage: 0 # Knockback dealt % (-100 to +100; negative = less knockback)
    maxHealth: 0 # Extra max health (0 to 40 total across set; +20 = +10 hearts)
    attackDamageFlat: 0 # Flat damage bonus (0 to 6; +6 ≈ Sharp V extra)
    attackSpeedFlat: 0 # Flat attack speed (0 to 4; base is 4.0, +4 = double speed)
    jumpStrength: 0 # Jump strength % (0 to 100; +100 ≈ double jump height)
    gravityPercentage: 0 # Gravity % (-80 to +100; negative = floatier, positive = heavier)
    burningTimePercentage: 0 # Fire duration % (-100 to +100; -100 = fire immune)
    explosionKnockbackResistance: 0 # Explosion knockback resist 0 to 100
    oxygenBonus: 0 # Extra oxygen ticks (0 to 600; +300 ≈ +15s underwater)
    movementEfficiency: 0 # Slowdown resistance 0 to 100 (100 = ignore soul sand/webs)
    safeFallDistance: 0 # Extra safe fall blocks (0 to 20; +3 = vanilla, +10 ≈ 13 blocks)
    entityInteractionRangePercentage: 0 # Entity reach % (0 to 100; +50 = noticeably longer)
    blockInteractionRangePercentage: 0 # Block reach % (0 to 100; creative ≈ +50)
```

### Info

The info block names the tier and gates it behind a prior tier.

```yaml
display: "&c&lNETHERITE" # In-game tier display name
requiresTiers: # Tiers a piece must already have before this one can be applied
  - diamond
```

`requiresTiers` builds a progression: in the example, the Netherite tier can only be applied to a piece that already has the Diamond tier. The default config ships this tier tree as an example:

```yaml
                                            /-> exotic
default --> iron --> diamond --> netherite --> manyullyn
         \-> cobalt --> osmium

ancient --> mythic
```

### Upgrade crystal

The crystal is the in-game item players drop onto a piece to apply this tier.

```yaml
crystal:
  item: end_crystal # Crystal item, see https://plugins.auxilor.io/the-item-lookup-system
  name: "&cNetherite Upgrade Crystal" # In-game name
  lore: # Set to `lore: []` to remove lore
    - "&8Drop this onto an armor piece"
    - "&8to set its tier to:"
    - "&c&lNETHERITE"
    - ''
    - "&8&oRequires the armor to already have Diamond tier"
  craftable: true # Whether the crystal is craftable
  recipe: # See https://plugins.auxilor.io/the-item-lookup-system/recipes
    - air
    - netherite_ingot
    - air
    - netherite_ingot
    - ecoarmor:upgrade_crystal_diamond
    - netherite_ingot
    - air
    - netherite_ingot
    - air
  giveAmount: 1 # Optional; how many crystals the recipe yields
```

:::tip
We support both shaped and shapeless recipes. See [Recipes](https://plugins.auxilor.io/the-item-lookup-system/recipes) for how to configure them.
:::

### Properties

Each piece gets its own attribute block under `properties`. The helmet is shown here; repeat the same block for `chestplate`, `leggings`, `boots`, and `elytra`. You can read how vanilla attributes work on the [Minecraft Wiki](https://minecraft.wiki/w/Damage#Dealing_damage).

```yaml
properties:
  helmet:
    armor: 3 # Armor points (≈8 = netherite; >20 per piece has heavy diminishing returns)
    toughness: 3 # Armor toughness (≈3 = netherite; >12 to 15 per piece has little benefit)
    knockbackResistance: 1 # Knockback resist 0 to 100 (100 = immune; 40 to 60 ≈ netherite)
    speedPercentage: 0 # Move speed % (-50 to +50 recommended)
    attackSpeedPercentage: 0 # Attack speed % (-50 to +100; very high values can feel broken)
    attackDamagePercentage: 0 # Damage % (-50 to +100; high values trivialize combat)
    attackKnockbackPercentage: 0 # Knockback dealt % (-100 to +100; negative = less knockback)
    maxHealth: 0 # Extra max health (0 to 40 total across set; +20 = +10 hearts)
    attackDamageFlat: 0 # Flat damage bonus (0 to 6; +6 ≈ Sharp V extra)
    attackSpeedFlat: 0 # Flat attack speed (0 to 4; base is 4.0, +4 = double speed)
    jumpStrength: 0 # Jump strength % (0 to 100; +100 ≈ double jump height)
    gravityPercentage: 0 # Gravity % (-80 to +100; negative = floatier, positive = heavier)
    burningTimePercentage: 0 # Fire duration % (-100 to +100; -100 = fire immune)
    explosionKnockbackResistance: 0 # Explosion knockback resist 0 to 100
    oxygenBonus: 0 # Extra oxygen ticks (0 to 600; +300 ≈ +15s underwater)
    movementEfficiency: 0 # Slowdown resistance 0 to 100 (100 = ignore soul sand/webs)
    safeFallDistance: 0 # Extra safe fall blocks (0 to 20; +3 = vanilla, +10 ≈ 13 blocks)
    entityInteractionRangePercentage: 0 # Entity reach % (0 to 100; +50 = noticeably longer)
    blockInteractionRangePercentage: 0 # Block reach % (0 to 100; creative ≈ +50)
```

:::tip Troubleshooting
- **Crystal won't apply to a piece?** The piece doesn't have the tier listed in `requiresTiers` yet; apply that tier first.
- **Stats don't change after applying?** Re-equip the piece, and run `/ecoarmor reload` if you edited the tier while the server was running.
- **Crystal recipe missing?** Confirm `craftable: true` and that the `requiresTiers` crystal in the recipe exists.
:::

<hr/>

## Where to go next

- **Sets:** [How to Make an Armor Set](how-to-make-a-custom-set) to attach tiers to pieces via `defaultTier`.
- **Default tiers:** the shipped configs live [on GitHub](https://github.com/Auxilor/EcoArmor/tree/master/eco-core/core-plugin/src/main/resources/tiers).
- **Commands:** [Commands and Permissions](commands-and-permissions) for giving crystals in-game.