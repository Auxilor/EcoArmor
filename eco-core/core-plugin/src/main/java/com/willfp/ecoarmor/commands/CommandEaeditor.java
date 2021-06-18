package com.willfp.ecoarmor.commands;

import com.willfp.eco.core.EcoPlugin;
import com.willfp.eco.core.command.AbstractCommand;
import com.willfp.eco.core.web.Paste;
import com.willfp.ecoarmor.EcoArmorPlugin;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        String token = new Paste(((EcoArmorPlugin) this.getPlugin()).getEcoArmorJson().toPlaintext()).getHastebinToken();
        String message = this.getPlugin().getLangYml().getMessage("open-editor")
                .replace("%url%", "https://auxilor.io/editor/ecoarmor?token=" + token);
        sender.sendMessage(message);
    }
}
