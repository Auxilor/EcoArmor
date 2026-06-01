---
title: "Commands and Permissions"
sidebar_position: 5
---

Every EcoArmor command is under `/ecoarmor`. Permissions follow the `ecoarmor.command.<name>` pattern and are granted to operators by default. This page lists each command, what it does, and the permission it needs.

## Commands

| Command | Description | Permission |
| --- | --- | --- |
| `/ecoarmor reload` | Reload the plugin. | `ecoarmor.command.reload` |
| `/ecoarmor give <player> set:<set> [piece] [advanced] [tier] [amount]` | Give a player an armor piece or full set. | `ecoarmor.command.give` |
| `/ecoarmor give <player> crystal:<tier> [amount]` | Give a player an upgrade crystal. | `ecoarmor.command.give` |
| `/ecoarmor give <player> shard:<set> [amount]` | Give a player an advancement shard. | `ecoarmor.command.give` |
| `/ecoarmor import <id>` | Import an armor set from [lrcdb](https://lrcdb.auxilor.io/). | `ecoarmor.command.import` |
| `/ecoarmor export <id>` | Export an armor set to [lrcdb](https://lrcdb.auxilor.io/). | `ecoarmor.command.export` |

<hr/>

## Where to go next

- **Build a set:** [How to Make an Armor Set](how-to-make-a-custom-set).
- **Build a tier:** [How to Make a Tier](how-to-make-a-custom-tier).
- **Plugin settings:** [Plugin Config](plugin-config).