package com.willfp.ecoarmor;

import com.willfp.eco.util.command.AbstractCommand;
import com.willfp.eco.util.integrations.IntegrationLoader;
import com.willfp.eco.util.plugin.AbstractEcoPlugin;
import com.willfp.eco.util.protocollib.AbstractPacketAdapter;
import com.willfp.ecoarmor.commands.CommandEagive;
import com.willfp.ecoarmor.commands.CommandEareload;
import com.willfp.ecoarmor.commands.TabcompleterEagive;
import com.willfp.ecoarmor.config.EcoArmorConfigs;
import com.willfp.ecoarmor.display.packets.PacketChat;
import com.willfp.ecoarmor.display.packets.PacketSetCreativeSlot;
import com.willfp.ecoarmor.display.packets.PacketSetSlot;
import com.willfp.ecoarmor.display.packets.PacketWindowItems;
import com.willfp.ecoarmor.effects.Effects;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.tiers.CrystalListener;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class EcoArmorPlugin extends AbstractEcoPlugin {
    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoArmorPlugin() {
        super("EcoArmor", 0, 10002, "com.willfp.ecoarmor.proxy", "&5");
    }

    /**
     * Code executed on plugin enable.
     */
    @Override
    public void enable() {
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
        return Arrays.asList(
                new PacketChat(this),
                new PacketSetSlot(this),
                new PacketSetCreativeSlot(this),
                new PacketWindowItems(this)
        );
    }

    /**
     * EcoArmor-specific listeners.
     *
     * @return A list of all listeners.
     */
    @Override
    public List<Listener> getListeners() {
        return Arrays.asList(
                new CrystalListener(this)
        );
    }

    @Override
    public List<Class<?>> getUpdatableClasses() {
        return Arrays.asList(
                EcoArmorConfigs.class,
                ArmorSets.class,
                TabcompleterEagive.class
        );
    }
}
