package team._0mods.ecr.common.init.registry

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.api.utils.onRegisterCommands
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookTypes

object ECCommands {
    private val commandId = listOf("essentialcraft", "ec", "essential-craft", "ecr", "essentialcraftremained", "essential-craft-remained")

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.onRegisterCommands {
            commandId.forEach {
                it {
                    "debug" {
                        val argument = this.source.playerOrException
                        val uuid = argument.uuid
                        val cap = if (argument.getCapability(ECCapabilities.PLAYER_MRU).isPresent)
                            argument.getCapability(ECCapabilities.PLAYER_MRU).orElseThrow { IllegalStateException("Capability is present but not present") }
                        else null

                        val builder = StringBuilder()
                        builder.append("Debug info about ${argument.displayName.string}:")
                            .append('\n').append(' ')
                            .append("Current UUID: $uuid")
                            .append('\n').append(' ')
                            .append(if (cap != null) "Capability: " else "Capability is null.")

                        if (cap != null) {
                            builder.append('\n').append(' ').append(' ')
                                .append("MatrixType: ${cap.matrixType.name.string}")
                                .append('\n').append(' ').append(' ')
                                .append("Matrix Destruction: ${cap.matrixDestruction}")
                                .append('\n').append(' ').append(' ')
                                .append("Are infused: ${cap.isInfused}")
                        }

                        argument.sendSystemMessage(Component.literal(builder.toString()))
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
                }
            }
        }
    }
}
