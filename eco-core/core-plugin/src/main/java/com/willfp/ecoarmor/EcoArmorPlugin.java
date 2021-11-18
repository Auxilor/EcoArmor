package com.willfp.ecoarmor;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecoarmor.commands.CommandEcoarmor;
import com.willfp.ecoarmor.config.EcoArmorJson;
import com.willfp.ecoarmor.display.ArmorDisplay;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.util.EffectiveDurabilityListener;
import com.willfp.ecoarmor.sets.util.PreventSkullPlaceListener;
import com.willfp.ecoarmor.upgrades.Tiers;
import com.willfp.ecoarmor.upgrades.listeners.AdvancementShardListener;
import com.willfp.ecoarmor.upgrades.listeners.CrystalListener;
import com.willfp.ecoarmor.util.DiscoverRecipeListener;
import com.willfp.libreforge.api.LibReforge;
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

        LibReforge.init(this);
    }

    @Override
    protected void handleEnable() {
        LibReforge.enable(this);
    }

    @Override
    protected void handleReload() {
        this.getLogger().info(Tiers.values().size() + " Tiers Loaded");
        this.getLogger().info(ArmorSets.values().size() + " Sets Loaded");
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
                new PreventSkullPlaceListener()
        );
    }

    @Override
    protected List<IntegrationLoader> loadIntegrationLoaders() {
        return LibReforge.getIntegrationLoaders();
    }

    @Override
    protected @Nullable DisplayModule createDisplayModule() {
        return new ArmorDisplay(this);
    }
}
