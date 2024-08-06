package team._0mods.ecr.common.init.registry

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import team._0mods.ecr.api.*
import team._0mods.ecr.api.onRegisterCommands
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.utils.*

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
