package team._0mods.ecr.common.init.registry

import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.utils.ecRL

object ECTags {
    // items
    val magicCores = TagKey.create(Registries.ITEM, "magic_core".ecRL)
    // blocks
    val copperSlabs = TagKey.create(Registries.BLOCK, "copper_slabs".rl)
    val copperBlocks = TagKey.create(Registries.BLOCK, "copper_blocks".rl)
}