package com.algorithmlx.ecr.common.init

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.commands.commands
import com.algorithmlx.ecr.api.research.ResearchJson
import com.algorithmlx.ecr.api.research.ResearchProgress
import com.algorithmlx.ecr.common.components.playerMatrix
import com.algorithmlx.ecr.common.components.updatePlayerMatrix
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier

object ECRCommands {
    // todo: need translate
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.commands { ModId {
            aliases("essential-craft", "essentialcraft")
            requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))

            "research" {
                "reset" {
                    executes {
                        val player = this.context.source.player
                        if (player == null) {
                            source.sendFailure(Component.literal("Research reset without player available only inside game. Not console."))
                            return@executes 0
                        }
                        ResearchProgress.reset(player)
                        source.sendSuccess(
                            { Component.literal("Reset research progress for ${player.name.string}") },
                            true
                        )
                        1
                    }

                    argument(
                        "targets",
                        EntityArgument.players(),
                        { context, name -> EntityArgument.getPlayers(context, name) },) { targets ->
                        executes {
                            val players = targets()
                            players.forEach(ResearchProgress::reset)
                            source.sendSuccess(
                                { Component.literal("Reset research progress for ${players.size} player(s)") },
                                true
                            )
                            players.size
                        }
                    }
                }

                "unlock_all" {
                    executes {
                        val player = this.context.source.player
                        if (player == null) {
                            source.sendFailure(Component.literal("Research unlock without player available only inside game. Not console."))
                            return@executes 0
                        }
                        ResearchProgress.grantAll(player)
                        source.sendSuccess(
                            { Component.literal("Unlocked all research for ${player.name.string}") },
                            true
                        )
                        1
                    }

                    argument(
                        "targets",
                        EntityArgument.players(),
                        { context, name -> EntityArgument.getPlayers(context, name) },) { targets ->
                        executes {
                            val players = targets()
                            players.forEach(ResearchProgress::grantAll)
                            source.sendSuccess(
                                { Component.literal("Unlocked all research for ${players.size} player(s)") },
                                true
                            )
                            players.size
                        }
                    }
                }

                "unlock" {
                    argument("research", StringArgumentType.string()) { research ->
                        executes {
                            val target = parseTarget(research()) ?: return@executes 0
                            val player = this.context.source.player
                            if (player == null) {
                                source.sendFailure(Component.literal("Research progress update without player available only inside game. Not console."))
                                return@executes 0
                            }
                            ResearchProgress.grant(player, target.first, target.second)
                            source.sendSuccess(
                                { Component.literal("Unlocked all research for ${player.name.string}") },
                                true
                            )
                            1
                        }

                        argument(
                            "targets",
                            EntityArgument.players(),
                            { context, name -> EntityArgument.getPlayers(context, name) }
                        ) { targets ->
                            executes {
                                val target = parseTarget(research()) ?: return@executes 0
                                val changed = targets()
                                    .count { ResearchProgress.grant(it, target.first, target.second) }
                                source.sendSuccess(
                                    { Component.literal("Updated research progress for $changed player(s)") },
                                    true
                                )
                                changed
                            }
                        }
                    }
                }
            }

            ECRModIDs.UBMRU {
                "query" {
                    executes {
                        val player = this.context.source.player
                        if (player == null) {
                            source.sendFailure(Component.literal("Get UBMRU count without player available only inside game. Not console."))
                            return@executes 0
                        }

                        source.sendSuccess(
                            {
                                Component.literal(
                                    "${player.name.string} has ${player.playerMatrix.mru} ${player.playerMatrix.mruType.name.string}."
                                )
                            },
                            true
                        )
                        1
                    }
                }

                "add" {
                    argument("count", IntegerArgumentType.integer()) { count ->
                        executes {
                            val player = this.context.source.player
                            if (player == null) {
                                source.sendFailure(Component.literal("UBMRU update without player available only inside game. Not console."))
                                return@executes 0
                            }

                            player.updatePlayerMatrix { this.insert(count()) }

                            source.sendSuccess(
                                {
                                    Component.literal(
                                        "${player.playerMatrix.mru} was added ${player.playerMatrix.mruType.name.string} to ${player.name.string}."
                                    )
                                },
                                true
                            )

                            1
                        }

                        argument(
                            "targets",
                            EntityArgument.players(),
                            { context, name -> EntityArgument.getPlayers(context, name) }
                        ) { targets ->
                            executes {
                                val players = targets()
                                players.forEach { player ->
                                    player.updatePlayerMatrix { this.insert(count()) }
                                }

                                source.sendSuccess(
                                    {
                                        Component.literal(
                                            "${count()} UBMRU was added for ${players.size} players."
                                        )
                                    },
                                    true
                                )

                                players.size
                            }
                        }
                    }
                }

                "set" {
                    argument("count", IntegerArgumentType.integer()) { count ->
                        executes {
                            val player = this.context.source.player
                            if (player == null) {
                                source.sendFailure(Component.literal("UBMRU update without player available only inside game. Not console."))
                                return@executes 0
                            }

                            player.updatePlayerMatrix { this.set(count()) }

                            source.sendSuccess(
                                {
                                    Component.literal("${count()} UBMRU sets for ${player.name.string}")
                                },
                                true
                            )

                            1
                        }

                        argument(
                            "targets",
                            EntityArgument.players(),
                            { context, name -> EntityArgument.getPlayers(context, name) }
                        ) { targets ->
                            executes {
                                val players = targets()
                                players.forEach { player ->
                                    player.updatePlayerMatrix { this.set(count()) }
                                }

                                source.sendSuccess(
                                    {
                                        Component.literal(
                                            "${count()} UBMRU was sets for ${players.size} players."
                                        )
                                    },
                                    true
                                )

                                players.size
                            }
                        }
                    }
                }
            }
        } }
    }

    private fun parseTarget(value: String): Pair<Identifier, String?>? = runCatching {
        val requirement = ResearchJson.parseRequirement(value, null)
        requirement.researchId(Identifier.parse(value.substringBeforeLast('.', value))) to requirement.task
    }.getOrNull()
}
