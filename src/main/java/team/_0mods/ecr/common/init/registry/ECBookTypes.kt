package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import team._0mods.ecr.ModId
import team._0mods.ecr.api.item.ECBookType

enum class ECBookTypes(override val translate: Component): ECBookType {
    BASIC(Component.translatable("bookType.$ModId.basic")),
    MRU(Component.translatable("bookType.$ModId.mru")),
    ENGINEER(Component.translatable("bookType.$ModId.engineer")),
    HOANA(Component.translatable("bookType.$ModId.hoana")),
    SHADE(Component.translatable("bookType.$ModId.shade"));
}