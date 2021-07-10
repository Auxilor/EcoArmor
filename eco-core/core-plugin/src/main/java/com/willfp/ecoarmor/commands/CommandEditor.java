package com.willfp.ecoarmor.commands;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.CommandHandler;
import com.willfp.eco.core.command.impl.Subcommand;
import com.willfp.eco.util.StringUtils;
import org.jetbrains.annotations.NotNull;

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
            /*
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
             */

            sender.sendMessage(StringUtils.translate("&cThe editor is coming soon!"));
        };
    }
}
