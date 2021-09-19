package com.willfp.ecoarmor;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.ecoarmor.commands.CommandEcoarmor;
import com.willfp.ecoarmor.conditions.Conditions;
import com.willfp.ecoarmor.conditions.util.MovementConditionListener;
import com.willfp.ecoarmor.config.EcoArmorJson;
import com.willfp.ecoarmor.display.ArmorDisplay;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.effects.util.EffectWatcher;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.util.EffectiveDurabilityListener;
import com.willfp.ecoarmor.sets.util.PreventSkullPlaceListener;
import com.willfp.ecoarmor.upgrades.Tiers;
import com.willfp.ecoarmor.upgrades.listeners.AdvancementShardListener;
import com.willfp.ecoarmor.upgrades.listeners.CrystalListener;
import com.willfp.ecoarmor.util.DiscoverRecipeListener;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EcoArmorPlugin extends EcoPlugin {
    /**
     * Instance of EcoArmor.
     */
    @Getter
    private static EcoArmorPlugin instance;

    /**
     * tiers.json.
     */
    @Getter
    private final EcoArmorJson ecoArmorJson;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoArmorPlugin() {
        super(687, 10002, "&c");
        instance = this;

        this.ecoArmorJson = new EcoArmorJson(this);
    }

    @Override
    protected void handleEnable() {
        Effects.values().stream().filter(Effect::isEnabled).forEach(effect -> this.getEventManager().registerListener(effect));
        Conditions.values().forEach(condition -> this.getEventManager().registerListener(condition));
    }

    @Override
    protected void handleReload() {
        Effects.values().forEach(effect -> this.getEventManager().unregisterListener(effect));
        Effects.values().stream().filter(Effect::isEnabled).forEach(effect -> this.getEventManager().registerListener(effect));
        this.getLogger().info(Tiers.values().size() + " Tiers Loaded");
        this.getLogger().info(ArmorSets.values().size() + " Sets Loaded");
        this.getScheduler().runTimer((Runnable) Conditions.HAS_PERMISSION, 100, 100);
    }

    @Override
    protected List<PluginCommand> loadPluginCommands() {
        return Arrays.asList(
                new CommandEcoarmor(this)
        );
    }

    @Override
    protected List<Listener> loadListeners() {
        return Arrays.asList(
                new CrystalListener(this),
                new AdvancementShardListener(this),
                new EffectiveDurabilityListener(this),
                new DiscoverRecipeListener(this),
                new EffectWatcher(this),
                new MovementConditionListener(this),
                new PreventSkullPlaceListener()
        );
    }

    @Override
    protected @Nullable DisplayModule createDisplayModule() {
        return new ArmorDisplay(this);
    }
}
