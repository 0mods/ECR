package team._0mods.ecr.common.init

import team._0mods.ecr.ModId
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem

object ECRRegistry {
    val items = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)

    val flameGem = items.register("flame_gem", ECGem.flame)
    val waterGem = items.register("water_gem", ECGem.water)
    val earthGem = items.register("earth_gem", ECGem.earth)
    val airGem = items.register("air_gem", ECGem.air)
    val elementalGem = items.register("elemental_gem", ECGem.elemental)

    val basicBook = items.register("basic_book", ECBook.basicBook)
    val mruBook = items.register("mru_book", ECBook.mruBook)
    val engineerBook = items.register("engineer_book", ECBook.engineerBook)
    val hoanaBook = items.register("hoana_book", ECBook.hoanaBook)
    val shadeBook = items.register("shade_book", ECBook.shadeBook)

    @JvmStatic
    fun init(bus: IEventBus) {
        items.register(bus)
    }

    fun basicItem(id: String, properties: Properties.() -> Unit = {}): RegistryObject<Item> {
        val props = Properties().apply(properties).tab(ECTabs.tabItems)
        return items.register(id) { Item(props) }
    }
}