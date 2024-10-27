@file:Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.ExperimentalSerializationApi
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.common.api.NoTab
import team._0mods.ecr.common.init.config.ECCommonConfig
import team._0mods.ecr.common.init.registry.ECRegistry
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECBook.Companion.bookTypes
import kotlin.collections.plus

@OptIn(ExperimentalSerializationApi::class)
@SubscribeEvent
fun onModSetup(e: FMLCommonSetupEvent) {
    ECCommonConfig.instance = ECCommonConfig().loadConfig("essential-craft/common")
}

@SubscribeEvent
fun onBuildCreativeTabs(e: BuildCreativeModeTabContentsEvent) {
    val items = ForgeRegistries.ITEMS.filter { ForgeRegistries.ITEMS.getKey(it)!!.namespace.contains(ModId) }

    items.forEach { it ->
        if (it is NoTab && it !is ECBook) return@forEach

        if (it !is BlockItem) {
            if (e.tab == ECRegistry.tabItems.get()) {
                if (it is ECBook) {
                    val values = ECRegistries.BOOK_TYPES.registries.values

                    for (i in 0 ..< values.size) {
                        val stack = ItemStack(it).apply {
                            for (j in 0 .. i) {
                                var bt = this.bookTypes!!
                                values.toList()[j].let { bt += it }
                                this.bookTypes = bt
                            }
                        }

                        e.accept(stack)
                    }

                    return@forEach
                }
                e.accept(it)
            }
        } else {
            if (e.tab == ECRegistry.tabBlocks.get()) e.accept(it)
        }
    }
}
