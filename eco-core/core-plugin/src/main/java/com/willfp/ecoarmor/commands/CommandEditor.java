package com.willfp.ecoarmor.commands;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.eco.core.config.interfaces.JSONConfig;
import com.willfp.eco.core.web.Paste;
import com.willfp.eco.util.StringUtils;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.conditions.Conditions;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandEditor extends Subcommand {
    /**
     * Instantiate a new /eaeditor command handler.
     *
     * @param plugin The plugin for the commands to listen for.
     */
    public CommandEditor(@NotNull final EcoPlugin plugin) {
        super(plugin, "editor", "ecoarmor.command.editor", false);
    }

    @Override
    public CommandHandler getHandler() {
        return (sender, args) -> {
            JSONConfig config = ((EcoArmorPlugin) this.getPlugin()).getEcoArmorJson().clone();
            Map<String, Object> meta = new HashMap<>();

            List<String> enchants = Arrays.stream(Enchantment.values()).map(Enchantment::getKey).map(NamespacedKey::getKey).collect(Collectors.toList());
            meta.put("enchants", enchants);

            List<String> potionEffects = Arrays.stream(PotionEffectType.values()).map(PotionEffectType::getName).collect(Collectors.toList());
            meta.put("potion-effects", potionEffects);

            List<String> effects = Effects.values().stream().map(Effect::getName).collect(Collectors.toList());
            meta.put("effects", effects);

            List<String> conditions = Conditions.values().stream().map(Condition::getName).collect(Collectors.toList());
            meta.put("conditions", conditions);

            config.set("meta", meta);
            new Paste(config.toPlaintext()).getHastebinToken(token -> {
                sender.sendMessage(StringUtils.format("&cThe editor is coming soon!") + token);
            });
        };
    }
}
