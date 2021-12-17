package com.willfp.ecoarmor;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.impl.PluginCommand;
import com.willfp.eco.core.display.DisplayModule;
import com.willfp.eco.core.integrations.IntegrationLoader;
import com.willfp.ecoarmor.commands.CommandEcoarmor;
import com.willfp.ecoarmor.config.EcoArmorYml;
import com.willfp.ecoarmor.display.ArmorDisplay;
import com.willfp.ecoarmor.sets.ArmorSets;
import com.willfp.ecoarmor.sets.util.ArmorUtils;
import com.willfp.ecoarmor.sets.EffectiveDurabilityListener;
import com.willfp.ecoarmor.sets.PreventSkullPlaceListener;
import com.willfp.ecoarmor.upgrades.Tiers;
import com.willfp.ecoarmor.upgrades.AdvancementShardListener;
import com.willfp.ecoarmor.upgrades.CrystalListener;
import com.willfp.ecoarmor.util.DiscoverRecipeListener;
import com.willfp.ecoarmor.util.EffectListener;
import com.willfp.libreforge.Holder;
import com.willfp.libreforge.LibReforge;
import lombok.Getter;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class EcoArmorPlugin extends EcoPlugin {
    /**
     * Instance of EcoArmor.
     */
    @Getter
    private static EcoArmorPlugin instance;

    /**
     * ecoarmor.yml.
     */
    @Getter
    private final EcoArmorYml ecoArmorYml;

    /**
     * Internal constructor called by bukkit on plugin load.
     */
    public EcoArmorPlugin() {
        super(687, 10002, "&c");
        instance = this;

        this.ecoArmorYml = new EcoArmorYml(this);

        LibReforge.init(this);
        LibReforge.registerHolderProvider(player -> {
            Holder active = ArmorUtils.getActiveSet(player);
            if (active == null) {
                return Collections.emptyList();
            } else {
                return Collections.singletonList(active);
            }
        });
    }

    @Override
    protected void handleEnable() {
        LibReforge.enable(this);
    }

    @Override
    protected void handleDisable() {
        LibReforge.disable(this);
    }

    @Override
    protected void handleReload() {
        this.getLogger().info(Tiers.values().size() + " Tiers Loaded");
        this.getLogger().info(ArmorSets.values().size() + " Sets Loaded");
        LibReforge.reload(this);
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
                new PreventSkullPlaceListener(),
                new EffectListener()
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

    @Override
    public String getMinimumEcoVersion() {
        return "6.15.0";
    }
}
