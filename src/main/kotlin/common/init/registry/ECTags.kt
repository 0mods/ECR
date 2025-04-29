package team._0mods.ecr.common.init.registry

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import team._0mods.ecr.api.utils.ecRL

object ECTags {
    val magicCores = TagKey.create(Registries.ITEM, "magic_core".ecRL)
}