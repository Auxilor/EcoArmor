---
title: "Plugin Config"
sidebar_position: 6
---

The main plugin settings live in `EcoArmor/config.yml`. It controls plugin-wide behavior that isn't tied to a single set or tier, such as recipe discovery and how item names and lore are displayed. Every option below is annotated inline.

After editing, run `/ecoarmor reload` to apply your changes.

## Default config.yml

```yaml
discover-recipes: true # Whether all plugin recipes are automatically discovered for players
update-item-names: true # Whether item names are refreshed to match config (disable to allow renaming pieces in anvils)
update-leather-colors: true # Whether leather colors are refreshed to match config
advanced-lore-only: false # Whether advanced armor shows only the advanced lore, hiding the base lore
```

<hr/>

## Where to go next

- **Build a set:** [How to Make an Armor Set](how-to-make-a-custom-set).
- **Commands:** [Commands and Permissions](commands-and-permissions) for the in-game `/ecoarmor` commands.