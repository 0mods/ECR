package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.utils.ecRL

object ECTabs: HollowRegistry(ModId) {
    val tabItems by register("tab_items") {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.$ModId.items"))
            .build()
    }

    val tabBlocks by register("tab_blocks") {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.$ModId.blocks"))
            .withTabsAfter("tab_items".ecRL)
            .build()
    }
}