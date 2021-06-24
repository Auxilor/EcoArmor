package com.willfp.ecoarmor.commands;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.config.JSONConfig;
import com.willfp.eco.core.web.Paste;
import com.willfp.ecoarmor.EcoArmorPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
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
        List<String> enchantKeys = Arrays.stream(Enchantment.values()).map(Enchantment::getKey).map(NamespacedKey::getKey).collect(Collectors.toList());
        config.set("meta.enchants", enchantKeys);
        String token = new Paste(config.toPlaintext()).getHastebinToken();
        String message = this.getPlugin().getLangYml().getMessage("open-editor")
                .replace("%url%", "https://auxilor.io/editor/ecoarmor?token=" + token);
        sender.sendMessage(message);
    }
}
