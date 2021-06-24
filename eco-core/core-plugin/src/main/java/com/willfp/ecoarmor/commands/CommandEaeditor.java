package com.willfp.ecoarmor.commands;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.config.JSONConfig;
import com.willfp.eco.core.web.Paste;
import com.willfp.ecoarmor.EcoArmorPlugin;
import com.willfp.ecoarmor.conditions.Condition;
import com.willfp.ecoarmor.conditions.Conditions;
import com.willfp.ecoarmor.effects.Effect;
import com.willfp.ecoarmor.effects.Effects;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommandEaeditor extends AbstractCommand {
    /**
     * Instantiate a new /eaeditor command handler.
     *
     * @param plugin The plugin for the commands to listen for.
     */
    public CommandEaeditor(@NotNull final EcoPlugin plugin) {
        super(plugin, "eaeditor", "ecoarmor.editor", false);
    }

    @Override
    public void onExecute(@NotNull final CommandSender sender,
                          @NotNull final List<String> args) {
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
        String token = new Paste(config.toPlaintext()).getHastebinToken();
        String message = this.getPlugin().getLangYml().getMessage("open-editor")
                .replace("%url%", "https://auxilor.io/editor/ecoarmor?token=" + token);
        sender.sendMessage(message);
    }
}
