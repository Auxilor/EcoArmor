package com.willfp.ecoarmor;

import com.willfp.eco.core.AbstractPacketAdapter;
import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecoarmor.commands.CommandEaeditor;
import com.willfp.ecoarmor.commands.CommandEagive;
import com.willfp.ecoarmor.commands.CommandEareload;
import com.willfp.ecoarmor.commands.TabcompleterEagive;
import com.willfp.ecoarmor.conditions.Conditions;
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

import java.util.ArrayList;
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
        super(88246, 10002, "com.willfp.ecoarmor.proxy", "&c");
        instance = this;

        this.ecoArmorJson = new EcoArmorJson(this);
    }

    /**
     * Code executed on plugin enable.
     */
    @Override
    public void enable() {
        this.getExtensionLoader().loadExtensions();

        if (this.getExtensionLoader().getLoadedExtensions().isEmpty()) {
            this.getLogger().info("&cNo extensions found");
        } else {
            this.getLogger().info("Extensions Loaded:");
            this.getExtensionLoader().getLoadedExtensions().forEach(extension -> this.getLogger().info("- " + extension.getName() + " v" + extension.getVersion()));
        }

        Effects.values().stream().filter(Effect::isEnabled).forEach(effect -> this.getEventManager().registerListener(effect));
        Conditions.values().forEach(condition -> this.getEventManager().registerListener(condition));
        this.getScheduler().runTimer((Runnable) Conditions.HAS_PERMISSION, 100, 100);
    }

    /**
     * Code executed on plugin disable.
     */
    @Override
    public void disable() {
        // Nothing needs to be called on disable
    }

    /**
     * Nothing is called on plugin load.
     */
    @Override
    public void load() {
        // Nothing needs to be called on load
    }

    /**
     * Code executed on reload.
     */
    @Override
    public void onReload() {
        Effects.values().forEach(effect -> this.getEventManager().unregisterListener(effect));
        Effects.values().stream().filter(Effect::isEnabled).forEach(effect -> this.getEventManager().registerListener(effect));
        this.getLogger().info(Tiers.values().size() + " Tiers Loaded");
        this.getLogger().info(ArmorSets.values().size() + " Sets Loaded");
    }

    /**
     * Code executed after server is up.
     */
    @Override
    public void postLoad() {
        // Nothing needs to be called after load.
    }

    /**
     * EcoArmor-specific integrations.
     *
     * @return A list of all integrations.
     */
    @Override
    public List<IntegrationLoader> getIntegrationLoaders() {
        return new ArrayList<>();
    }

    /**
     * EcoArmor-specific commands.
     *
     * @return A list of all commands.
     */
    @Override
    public List<AbstractCommand> getCommands() {
        return Arrays.asList(
                new CommandEareload(this),
                new CommandEagive(this),
                new CommandEaeditor(this)
        );
    }

    /**
     * Packet Adapters.
     *
     * @return A list of packet adapters.
     */
    @Override
    public List<AbstractPacketAdapter> getPacketAdapters() {
        return new ArrayList<>();
    }

    /**
     * EcoArmor-specific listeners.
     *
     * @return A list of all listeners.
     */
    @Override
    public List<Listener> getListeners() {
        return Arrays.asList(
                new CrystalListener(this),
                new AdvancementShardListener(this),
                new EffectiveDurabilityListener(this),
                new DiscoverRecipeListener(this),
                new EffectWatcher(this),
                new PreventSkullPlaceListener()
        );
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return Arrays.asList(
                Tiers.class,
                ArmorSets.class,
                TabcompleterEagive.class
        );
    }

    @Override
    protected @Nullable DisplayModule createDisplayModule() {
        return new ArmorDisplay(this);
    }
}
