package com.willfp.ecoarmor.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.ecoarmor.plugin
import com.willfp.ecoarmor.sets.ArmorSet
import com.willfp.ecoarmor.sets.ArmorSets
import com.willfp.libreforge.ArgType
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.effects.Effect
import com.willfp.libreforge.effects.Identifiers
import com.willfp.libreforge.forceRefreshHolders
import com.willfp.libreforge.get
import org.bukkit.entity.LivingEntity
import java.util.UUID

object EffectCountTowardSet : Effect<NoCompileData>("count_toward_set") {
    override val description = "Counts as pieces of an armor set while active."

    override val categories = setOf("inventory")

    override val shouldReload = false

    override val additionalInfo = listOf(
        "Counts towards full and partial set effects, and towards the advanced set only if advanced is true.",
        "Ignored when given by the effects or partial effects of an armor set."
    )

    override val arguments = arguments {
        require(
            "set",
            "You must specify the set!",
            description = "The ID of the armor set to count towards.",
            type = ArgType.STRING,
            example = "reaver"
        )
        optional(
            "amount",
            description = "The number of pieces to count as.",
            type = ArgType.INT,
            default = "1",
            example = "2"
        )
        optional(
            "advanced",
            description = "If true, also counts as advanced pieces towards the advanced set.",
            type = ArgType.BOOLEAN,
            default = "false"
        )
    }

    private data class SetPiece(val setId: String, val amount: Int, val advanced: Boolean)

    private val pieces = mutableMapOf<UUID, MutableMap<UUID, SetPiece>>()

    private val pendingRefreshes = mutableSetOf<UUID>()

    /**
     * Get the extra set pieces an entity counts as having.
     *
     * @param entity The entity.
     * @param advancedOnly If only pieces that count towards the advanced set should be included.
     * @return The sets mapped to their extra piece count.
     */
    fun getExtraPieces(entity: LivingEntity, advancedOnly: Boolean = false): Map<ArmorSet, Int> {
        val entityPieces = pieces[entity.uniqueId] ?: return emptyMap()
        val extraPieces = mutableMapOf<ArmorSet, Int>()

        for (piece in entityPieces.values) {
            if (advancedOnly && !piece.advanced) {
                continue
            }

            val set = ArmorSets.getByID(piece.setId) ?: continue
            extraPieces.merge(set, piece.amount, Int::plus)
        }

        return extraPieces
    }

    override fun onEnable(
        dispatcher: Dispatcher<*>,
        config: Config,
        identifiers: Identifiers,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ) {
        if (dispatcher.get<LivingEntity>() == null) {
            return
        }

        if (ArmorSets.values().any { it.providesSetEffects(holder.holder) }) {
            return
        }

        val amount = if (config.has("amount")) config.getInt("amount") else 1

        if (amount <= 0) {
            return
        }

        pieces.getOrPut(dispatcher.uuid) { mutableMapOf() }[identifiers.uuid] =
            SetPiece(config.getString("set"), amount, config.getBool("advanced"))

        scheduleRefresh(dispatcher)
    }

    override fun onDisable(dispatcher: Dispatcher<*>, identifiers: Identifiers, holder: ProvidedHolder) {
        val entityPieces = pieces[dispatcher.uuid] ?: return

        if (entityPieces.remove(identifiers.uuid) == null) {
            return
        }

        if (entityPieces.isEmpty()) {
            pieces.remove(dispatcher.uuid)
        }

        scheduleRefresh(dispatcher)
    }

    private fun scheduleRefresh(dispatcher: Dispatcher<*>) {
        if (!pendingRefreshes.add(dispatcher.uuid)) {
            return
        }

        plugin.scheduler.run {
            pendingRefreshes.remove(dispatcher.uuid)

            if (dispatcher.get<LivingEntity>()?.isValid == true) {
                dispatcher.forceRefreshHolders()
            }
        }
    }
}
