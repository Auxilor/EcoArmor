package com.willfp.ecoarmor;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.display.Display;
import com.willfp.eco.util.display.DisplayModule;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import com.willfp.ecoarmor.commands.CommandEagive;
import com.willfp.ecoarmor.commands.CommandEareload;
import com.willfp.ecoarmor.commands.TabcompleterEagive;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import com.willfp.ecoarmor.display.ArmorDisplay;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.util.EffectiveDurabilityListener;
import com.willfp.ecoarmor.upgrades.advanced.AdvancementShardListener;
import com.willfp.ecoarmor.upgrades.crystal.CrystalListener;
import com.willfp.ecoarmor.upgrades.crystal.UpgradeCrystal;
import lombok.Getter;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EcoArmorPlugin extends AbstractEcoPlugin {
    /**
     * Instance of EcoArmor.
     */
    @Getter
    private static EcoArmorPlugin instance;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoArmorPlugin() {
        super("EcoArmor", 0, 10002, "com.willfp.ecoarmor.proxy", "&c");
        instance = this;
    }

    /**
     * Code executed on plugin enable.
     */
    @Override
    public void enable() {
        Display.registerDisplayModule(new DisplayModule(ArmorDisplay::display, 1, this.getPluginName()));
        Display.registerRevertModule(ArmorDisplay::revertDisplay);
        Effects.values().forEach(effect -> this.getEventManager().registerListener(effect));
        this.onReload();
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
        this.getLog().info(ArmorSets.values().size() + " Sets Loaded");
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
                new CommandEagive(this)
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
                new EffectiveDurabilityListener(this)
        );
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return Arrays.asList(
                EcoArmorConfigs.class,
                ArmorSets.class,
                TabcompleterEagive.class,
                UpgradeCrystal.class
        );
    }
}
