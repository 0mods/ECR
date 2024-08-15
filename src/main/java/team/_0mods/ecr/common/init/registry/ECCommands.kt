package team._0mods.ecr.common.init.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.api.utils.arg
import team._0mods.ecr.api.utils.onRegisterCommands
import team._0mods.ecr.common.items.ECBook

object ECCommands {
    private val commandId = listOf("essentialcraft", "ec", "essential-craft", "ecr", "essentialcraftremained", "essential-craft-remained")

    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.onRegisterCommands {
            commandId.forEach {
                it {
                    "book"(arg("type", StringArgumentType.greedyString(), books)) {
                        val type = StringArgumentType.getString(this, "type").uppercase()
                        val item = ECRegistry.researchBook.get()
                        val player = this.source.playerOrException
                        val level = player.level

                        val stack = ItemStack(item).apply {
                            this.orCreateTag.putString("ECBookType", type)
                        }

                        val ent = ItemEntity(level, player.x, player.y, player.z, stack).apply {
                            setNoPickUpDelay()
                        }

                        level.addFreshEntity(ent)
                    }

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
                }
            }
        }
    }

    private val books: Collection<String> get() {
        val ids = mutableListOf<String>()
        ECBook.Type.entries.forEach {
            ids += it.name.lowercase()
        }

        return ids
    }
}
