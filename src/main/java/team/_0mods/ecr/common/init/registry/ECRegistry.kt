package team._0mods.ecr.common.init.registry

import team._0mods.ecr.ModId
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem

object ECRegistry {
    val items = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)

    val flameGem = items.register("flame_gem", ECGem.flame)
    val waterGem = items.register("water_gem", ECGem.water)
    val earthGem = items.register("earth_gem", ECGem.earth)
    val airGem = items.register("air_gem", ECGem.air)
    val elementalGem = items.register("elemental_gem", ECGem.elemental)

    val researchBook = items.register("research_book", ::ECBook)

    @JvmStatic
    fun init(bus: IEventBus) {
        items.register(bus)
    }

    fun basicItem(id: String, properties: Properties.() -> Unit = {}): RegistryObject<Item> {
        val props = Properties().apply(properties).tab(ECTabs.tabItems)
        return items.register(id) { Item(props) }
    }
}