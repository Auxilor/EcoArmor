---
title: "EcoArmor"
---

## What sets EcoArmor apart from other custom armor plugins?
Most importantly, EcoArmor isn't built for the sole purpose of PvP. Of course, you can use EcoArmor on a PvP-centric server, it will work perfectly - however it has a more generalized featureset. Perfect for Survival, MMORPG, Skyblock, Towny - anything you want, EcoArmor will work perfectly for. Empower your armor sets with better effects for all situations, make sets only work under certain conditions - bring a whole metagame to your armor.

Also - EcoArmor is completely customizable to anything you want. You can make your own Armor Sets and Upgrades in config, and with a little coding knowledge, create your own effects and conditions too. The sky's the limit!

## What does EcoArmor do?

### Sets

At the core of EcoArmor are the sets. A player must be wearing a full set of any given armor before the effects activate. For example, if the player is missing a piece, such as leggings, then the effects will not activate.

Armor pieces themselves have 2 distinct upgrades / properties:

### Tiers

Tiers are attribute modifiers given to each individual piece in the set. These can be different for all armor pieces and will apply to the player even if they're not wearing the full set. This modifies things like the armor value (shown in the hotbar), the armor protection, movement speed, etc.

Tiers are modified using **Upgrade Crystals**. Of course, you can rename this however you want, but everything will be explained in terms of the default config. Upgrade crystals are dragged and dropped in the inventory, by clicking an upgrade crystal onto a piece of armor.

The default tier tree looks like this:

```yaml
                                            /-> exotic
default --> iron --> diamond --> netherite --> manyullyn
         \-> cobalt --> osmium

ancient --> mythic
```

The main progression goes from default -> manyullyn, with optional branches. Cobalt and osmium are more tanky, in other words you move slower but deal more damage and take less damage, and exotic makes you more vulnerable however you move faster.

Ancient and Mythic aren't craftable by default and exist more as a special tier, perfect for crates or drops from bosses.

You can make your own progression in config, this exists purely as an example.

### Advancement

Advancement is an upgrade that must be applied to all items in the set before the bonus effects kick in. These can be more powerful versions of the base effects, or entirely new effects and potion effects altogether. For example, in the default config, **Reaper Armor** gives 1.25x attack damage, whereas **Advanced Reaper Armor** gives 1.5x attack damage and a 10% incoming damage reduction bonus.

Advancement can be applied with **Advancement Shards**. You can rename this however you want, like everything else in the plugin, but once again I'm using the default config as an example. Just like upgrade crystals, these can be dragged and dropped in the inventory, by clicking an advancement shard onto a piece of armor.
