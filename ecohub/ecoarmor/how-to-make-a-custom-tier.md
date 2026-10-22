---
title: "How to Make a Tier"
sidebar_position: 2
---

A **tier** is a set of **attribute modifiers** applied to a single armor piece, e.g. extra armor, toughness, or movement speed. Tiers apply per piece, even without the full set, and players move between them with **Upgrade Crystals**. Each tier is one YAML file in the `/tiers/` folder. This page builds one and explains every part.

## Quick start

1. Create a file in `plugins/EcoArmor/tiers/`, e.g. `netherite.yml` (copy `_example.yml` as a starting point). The file name becomes the tier ID.
2. Set the `display` name and any `requiresTiers` prerequisites.
3. Add `attribute_modifiers` under each piece's `components` with the stats you want.
4. Run `/ecoarmor reload`.
5. Give yourself a crystal with `/ecoarmor give <you> crystal:netherite`, drop it onto a piece, and check the piece's stats change.

:::tip
`_example.yml` is included as a reference and is **never loaded**, so copy or rename it to make a real tier. You can also organise tiers into subfolders inside `tiers/`, and they'll still load.
:::

## Naming and IDs

The file name without `.yml` is the tier ID. You use it in commands and in the `defaultTier` field of a set config. Crystal items resolve through the [Item Lookup System](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system) as `ecoarmor:upgrade_crystal_<id>`.

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
additive: false # If true, applying this tier ADDS its modifiers on top of the piece's current ones instead of replacing them, and allows the tier to be stacked more than once
stack-limit: -1 # Only used when additive is true. Max number of times this tier can be stacked onto one piece. -1 = unlimited

# === Upgrade crystal: the item that applies this tier ===
crystal:
  item: end_crystal # Crystal item, see https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system
  name: "&cNetherite Upgrade Crystal" # In-game name
  lore: # Set to `lore: []` to remove lore
    - "&8Drop this onto an armor piece"
    - "&8to set its tier to:"
    - "&c&lNETHERITE"
    - ''
    - "&8&oRequires the armor to already have Diamond tier"
  craftable: true # Whether the crystal is craftable
  recipe: # See https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system/recipes
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
    components:
      attribute_modifiers:
        - type: "minecraft:armor" # The attribute
          amount: 3 # How much it applies; decimals work
          operation: "add_value" # add_value, add_multiplied_base, or add_multiplied_total
        - type: "minecraft:armor_toughness"
          amount: 3
          operation: "add_value"
        - type: "minecraft:knockback_resistance"
          amount: 0.01
          operation: "add_value"
```

### Info

The info block names the tier and gates it behind a prior tier.

```yaml
display: "&c&lNETHERITE" # In-game tier display name
requiresTiers: # Tiers a piece must already have before this one can be applied
  - diamond
additive: false # If true, applying this tier ADDS its modifiers on top of the piece's current ones instead of replacing them, and allows the tier to be stacked more than once
stack-limit: -1 # Only used when additive is true. Max number of times this tier can be stacked onto one piece. -1 = unlimited
```

`requiresTiers` builds a progression: in the example, the Netherite tier can only be applied to a piece that already has the Diamond tier. The default config ships this tier tree as an example:

```yaml
                                            /-> exotic
default --> iron --> diamond --> netherite --> manyullyn
         \-> cobalt --> osmium

ancient --> mythic
```

#### Additive tiers

By default, applying a tier's crystal **replaces** every modifier currently on the piece with that tier's own modifiers — this is how the progression in the tree above normally works. Setting `additive: true` changes that: the tier's modifiers are **added on top of** whatever is already on the piece, up to `stack-limit` applications.

`requiresTiers` still gates every application, additive or not — it is checked first, against the piece's *current* tier. After a tier is applied once, the piece's current tier becomes that tier's own id, so **to let a crystal be dropped onto a piece that already has it, `requiresTiers` must either be empty or include the tier's own id**:

```yaml
# id: enhancement.yml
requiresTiers:
  - enhancement # allows re-applying this same tier onto a piece that already has it
additive: true
stack-limit: 3 # This tier can be stacked at most 3 times on one piece; -1 for unlimited
```

Use this for crystals meant to layer bonuses (e.g. a repeatable "enhancement" crystal) rather than to move a piece up a fixed tier tree. `stack-limit` counts only applications of *this* tier — stacking three different additive tiers onto one piece is unaffected by any one tier's own limit.

When a piece has more than one tier applied (because one or more were additive), the `%tier%` placeholder in that piece's lore lists every distinct applied tier instead of just one — see [the placeholder table](how-to-make-a-custom-set#internal-placeholders) on the set page, [`armor-display.tier-list-separator`](plugin-config) to control how they're joined, and [`armor-display.tier-stack-format`](plugin-config) to control how a tier stacked more than once is shown (e.g. `4x ANCIENT` or `ANCIENT IV` instead of `ANCIENT, ANCIENT, ANCIENT, ANCIENT`).

Applying a **non-additive** tier to a piece always resets it back to that single tier, clearing any previously stacked additive tiers.

### Upgrade crystal

The crystal is the in-game item players drop onto a piece to apply this tier.

```yaml
crystal:
  item: end_crystal # Crystal item, see https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system
  name: "&cNetherite Upgrade Crystal" # In-game name
  lore: # Set to `lore: []` to remove lore
    - "&8Drop this onto an armor piece"
    - "&8to set its tier to:"
    - "&c&lNETHERITE"
    - ''
    - "&8&oRequires the armor to already have Diamond tier"
  craftable: true # Whether the crystal is craftable
  recipe: # See https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system/recipes
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
We support both shaped and shapeless recipes. See [Recipes](https://hub.auxilor.io/wiki/eco/the-item-lookup-system-the-item-lookup-system/recipes) for how to configure them.
:::

### Properties

Each piece gets its own attribute block under `properties`. The helmet is shown here; repeat the same block for `chestplate`, `leggings`, `boots`, and `elytra`. You can read how vanilla attributes work on the [Minecraft Wiki](https://minecraft.wiki/w/Damage#Dealing_damage).

```yaml
properties:
  helmet:
    components:
      attribute_modifiers:
        - type: "minecraft:armor" # The attribute
          amount: 3 # How much it applies; decimals work
          operation: "add_value" # add_value, add_multiplied_base, or add_multiplied_total
        - type: "minecraft:armor_toughness"
          amount: 3
          operation: "add_value"
        - type: "minecraft:knockback_resistance"
          amount: 0.01
          operation: "add_value"
```

#### Components

Each entry under `attribute_modifiers` is a vanilla [attribute modifier](https://minecraft.wiki/w/Data_component_format#attribute_modifiers), so a tier can reach **every** attribute the game has, take decimals, and choose its own slot group and operation:

```yaml
properties:
  helmet:
    components:
      attribute_modifiers:
        - type: "minecraft:armor"
          amount: 3.5 # Decimals work
          operation: "add_value"
        - type: "minecraft:water_movement_efficiency"
          amount: 0.35 # Any attribute works
          operation: "add_value"
```

| Field | What it does |
| --- | --- |
| `type` | The attribute, e.g. `minecraft:armor`. Required. |
| `amount` | How much the modifier applies. Required. |
| `operation` | `add_value`, `add_multiplied_base`, or `add_multiplied_total`. Defaults to `add_value`. |
| `slot` | The slot group the modifier applies in. Defaults to the piece's own slot. |
| `id` | The modifier's id. Defaults to one derived from the tier, attribute, and piece. |

Only `attribute_modifiers` can be set on a tier. A tier's modifiers are rewritten every time the piece's tier changes, and attribute modifiers are the only components that can be cleanly removed again; anything else belongs on the [piece itself](how-to-make-a-custom-set#components). Modifier ids are always placed under the `ecoarmor` namespace for the same reason, so an `id` you give here has its namespace replaced.

When a tier is `additive`, the modifiers of every applied tier are combined, and two tiers configuring the same `id` collapse into one - the last applied wins.

:::info The stat options are deprecated
The older per-piece stat options (`armor`, `toughness`, `speedPercentage`, and the rest) still work, and existing tiers keep working unchanged. They're deprecated and will be removed in a future version, and tiers that use them are listed in a warning on load. Write new tiers with `components`, and move existing ones over when convenient:

```yaml
# Before
properties:
  helmet:
    armor: 3
    speedPercentage: 10

# After
properties:
  helmet:
    components:
      attribute_modifiers:
        - type: "minecraft:armor"
          amount: 3
          operation: "add_value"
        - type: "minecraft:movement_speed"
          amount: 0.1 # Percentages become fractions: 10% is 0.1
          operation: "add_multiplied_base"
```

The percentage options divide by 100 and apply as `add_multiplied_base`; `knockbackResistance` and `explosionKnockbackResistance` divide by 100 and apply as `add_value`; every other option applies its value as `add_value` unchanged.
:::

:::tip Troubleshooting
- **Crystal won't apply to a piece?** The piece doesn't have the tier listed in `requiresTiers` yet; apply that tier first.
- **Stats don't change after applying?** Re-equip the piece, and run `/ecoarmor reload` if you edited the tier while the server was running.
- **Stacked tier stopped applying?** Either the tier's `stack-limit` was reached (raise or remove `stack-limit`, or use a different additive tier), or its `requiresTiers` doesn't include the tier's own id, so `requiresTiers` is blocking the re-application before `stack-limit` is even checked — see [Additive tiers](#additive-tiers) above.
- **Crystal recipe missing?** Confirm `craftable: true` and that the `requiresTiers` crystal in the recipe exists.
:::

<hr/>

## Where to go next

- **Sets:** [How to Make an Armor Set](how-to-make-a-custom-set) to attach tiers to pieces via `defaultTier`.
- **Default tiers:** the shipped configs live [on GitHub](https://github.com/Auxilor/EcoArmor/tree/master/eco-core/core-plugin/src/main/resources/tiers).
- **Commands:** [Commands and Permissions](commands-and-permissions) for giving crystals in-game.