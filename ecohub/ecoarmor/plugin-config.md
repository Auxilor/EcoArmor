---
title: "Plugin Config"
sidebar_position: 4
---

The main plugin settings live in `EcoArmor/config.yml`. It controls plugin-wide behavior that isn't tied to a single set or tier, such as recipe discovery and how item names and lore are displayed. Every option below is annotated inline.

After editing, run `/ecoarmor reload` to apply your changes.

## Default config.yml

```yaml
discover-recipes: true # Whether all plugin recipes are automatically discovered for players
update-item-names: true # Whether item names are refreshed to match config (disable to allow renaming pieces in anvils)
update-leather-colors: true # Whether leather colors are refreshed to match config
advanced-lore-only: false # Whether advanced armor shows only the advanced lore, hiding the base lore

per-piece-advanced-lore:
  mode: add # How a piece's own advancedLore combines with the set-wide advancedLore: "add" shows both, "replace" shows only the piece's lore
  order: set-first # Which lore comes first when mode is "add": "set-first" (set-wide lore, then the piece's) or "piece-first" (the piece's lore, then set-wide)

armor-display:
  tier-list-separator: "&7, " # Separator used to join tier display names in the %tier% placeholder when a piece has more than one additive tier stacked on it
  tier-stack-format: multiple # How repeated additive tiers are shown in the %tier% placeholder: "none" lists every application separately (ANCIENT, ANCIENT, ANCIENT), "multiple" collapses them (4x ANCIENT), "numeral" collapses them with a roman numeral (ANCIENT IV)
```

<hr/>

## Where to go next

- **Build a set:** [How to Make an Armor Set](how-to-make-a-custom-set).
- **Commands:** [Commands and Permissions](commands-and-permissions) for the in-game `/ecoarmor` commands.