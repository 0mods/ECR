package team._0mods.ecr.common.init.registry

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.item.Items
import ru.hollowhorizon.hc.common.utils.get
import ru.hollowhorizon.hc.common.utils.literal
import ru.hollowhorizon.hc.common.commands.arg
import ru.hollowhorizon.hc.common.commands.onRegisterCommands
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.api.utils.SoulStoneUtils.capacity
import team._0mods.ecr.api.utils.SoulStoneUtils.owner
import team._0mods.ecr.api.utils.SoulStoneUtils.ownerName
import team._0mods.ecr.common.capability.PlayerMRU
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookTypes

object ECCommands {
    private val commandId = listOf("essentialcraft", "ec", "essential-craft")

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.onRegisterCommands {
            commandId.forEach {
                it {
                    "debug" {
                        val argument = this.source.playerOrException
                        val uuid = argument.uuid
                        val cap: PlayerMRU? = try {
                            argument[PlayerMRU::class]
                        } catch (e: Exception) {
                            null
                        }

                        val builder = StringBuilder()
                        builder.append("Debug info about ${argument.displayName.string}:")
                            .append('\n').append(' ')
                            .append("Current UUID: $uuid")
                            .append('\n').append(' ')

                        if (cap != null) {
                            builder.append("Capability: ").append('\n').append(' ').append(' ')
                                .append("MatrixType: ${cap.getMatrixType().displayName.string}")
                                .append('\n').append(' ').append(' ')
                                .append("Matrix Destruction: ${cap.matrixDestruction}")
                                .append('\n').append(' ').append(' ')
                                .append("Are infused: ${cap.isInfused}")
                        } else {
                            builder.append("Capability is null.")
                        }

                        argument.sendSystemMessage(Component.literal(builder.toString()))
                    }

                    "reset_matrix"(arg("player", EntityArgument.player())) {
                        val pl = try {
                            EntityArgument.getPlayer(this, "player")
                        } catch (_: Exception) {
                            try {
                                this.source.playerOrException
                            } catch (_: Exception) {
                                source.server.sendSystemMessage(Component.literal("No players found!"))
                                throw CommandSourceStack.ERROR_NOT_PLAYER.create()
                            }
                        }

                        pl[PlayerMRU::class].apply {
                            this.setMatrixType(ECPlayerMatrices.NEUTRAL)
                            this.isInfused = false
                            this.matrixDestruction = 0.0
                        }
                    }

                    "debug_item" {
                        val player = this.source.playerOrException
                        val handItem = player.getItemInHand(InteractionHand.MAIN_HAND)
                        val str = StringBuilder()

                        if (handItem.item is ECBook) {
                            val bt = handItem.bookTypes
                            str.append("Book Types").append(":").append(' ').append('\n')

                            bt?.forEach {
                                str.append(" -").append(ECRegistries.BOOK_TYPES.getKey(it)).append('\n')
                            }

                            player.sendSystemMessage(Component.literal(str.toString()))
                        }
                    }

                    "switch_soul_stone"(arg("player", EntityArgument.player())) soulStone@{
                        val pl = try {
                            EntityArgument.getPlayer(this, "player")
                        } catch (_: Exception) {
                            try {
                                this.source.playerOrException
                            } catch (_: Exception) {
                                source.server.sendSystemMessage("No players found!".literal.withStyle(ChatFormatting.RED))
                                throw CommandSourceStack.ERROR_NOT_PLAYER.create()
                            }
                        }

                        val stack = pl.getItemInHand(InteractionHand.MAIN_HAND)
                        if (stack.item !is SoulStoneLike) {
                            pl.sendSystemMessage(
                                Component.literal("Failed to rewrite item data! Because item is not soul stone! ${
                                    if (stack.item != Items.AIR)
                                        "(Item in hand: ${stack.item.getName(stack).string})"
                                    else ""
                                }").withStyle(ChatFormatting.RED)
                            )
                            return@soulStone
                        }

                        val oldOwnerName = stack.ownerName

                        val oldCapacity = stack.capacity
                        stack.owner = null
                        stack.owner = pl.uuid
                        stack.capacity = oldCapacity

                        pl.sendSystemMessage(Component.literal("Soul Stone data successful changed! Old Owner: $oldOwnerName, New Owner: ${pl.name.string}"))
                    }
                }
            }
        }
    }
}
