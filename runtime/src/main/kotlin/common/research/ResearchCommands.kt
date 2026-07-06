package com.algorithmlx.ecr.common.research

import com.algorithmlx.ecr.api.research.ResearchJson
import com.algorithmlx.ecr.api.research.ResearchProgress
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

object ResearchCommands {
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(
            Commands.literal("ecr")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(
                    Commands.literal("research")
                        .then(
                            Commands.literal("reset")
                                .then(Commands.argument("targets", EntityArgument.players()).executes { context ->
                                    val players = EntityArgument.getPlayers(context, "targets")
                                    players.forEach(ResearchProgress::reset)
                                    context.source.sendSuccess(
                                        { Component.literal("Reset research progress for ${players.size} player(s)") },
                                        true
                                    )
                                    players.size
                                })
                        )
                        .then(
                            Commands.literal("unlock_all")
                                .then(Commands.argument("targets", EntityArgument.players()).executes { context ->
                                    val players = EntityArgument.getPlayers(context, "targets")
                                    players.forEach(ResearchProgress::grantAll)
                                    context.source.sendSuccess(
                                        { Component.literal("Unlocked all research for ${players.size} player(s)") },
                                        true
                                    )
                                    players.size
                                })
                        )
                        .then(
                            Commands.literal("unlock")
                                .then(
                                    Commands.argument("targets", EntityArgument.players())
                                        .then(Commands.argument("research", StringArgumentType.string()).executes { context ->
                                            val target = parseTarget(StringArgumentType.getString(context, "research")) ?: return@executes 0
                                            val changed = EntityArgument.getPlayers(context, "targets")
                                                .count { ResearchProgress.grant(it, target.first, target.second) }
                                            context.source.sendSuccess(
                                                { Component.literal("Updated research progress for $changed player(s)") },
                                                true
                                            )
                                            changed
                                        })
                                )
                        )
                )
        )
    }

    private fun parseTarget(value: String): Pair<Identifier, String?>? = runCatching {
        val requirement = ResearchJson.parseRequirement(value, null)
        requirement.researchId(Identifier.parse(value.substringBeforeLast('.', value))) to requirement.taskId
    }.getOrNull()
}
