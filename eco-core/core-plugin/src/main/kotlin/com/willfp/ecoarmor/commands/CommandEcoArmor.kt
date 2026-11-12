package com.willfp.ecoarmor.commands

import com.willfp.ecoarmor.EcoArmorPlugin
import wiki.ecohub.core.command.command

internal fun EcoArmorPlugin.ecoArmorCommand() = command(name = "ecoarmor", permission = "ecoarmor.command.ecoarmor") {
    reloadCommand()
    giveCommand()
}
