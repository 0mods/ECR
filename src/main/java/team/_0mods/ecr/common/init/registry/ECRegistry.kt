package team._0mods.ecr.common.init.registry

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.animal.horse.Horse
import net.minecraft.world.item.BlockItem
import team._0mods.ecr.ModId
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import team._0mods.ecr.common.blocks.MithrilineFurnace
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.makeBERegistry
import team._0mods.ecr.common.register

object ECRegistry {
    val items = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)
    val blocksWE = makeBERegistry(ModId)
    val blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)

    // items
    val flameGem = items.register("flame_gem", ECGem.flame)
    val waterGem = items.register("water_gem", ECGem.water)
    val earthGem = items.register("earth_gem", ECGem.earth)
    val airGem = items.register("air_gem", ECGem.air)
    val elementalGem = items.register("elemental_gem", ECGem.elemental)

    val researchBook = items.register("research_book", ::ECBook)
    val soulStone = items.register("soul_stone", ::SoulStone)

    val elementalCore = basicItem("elemental_core")

    // blocks
    val mithrilineFurnace by blocksWE.register(
        "mithriline_furnace",
        { MithrilineFurnace(BlockBehaviour.Properties.of(Material.METAL)) },
        ::MithrilineFurnaceEntity
    )

    @JvmStatic
    fun init(bus: IEventBus) {
        items.register(bus)
        blocksWE.register(bus)
    }

    private fun basicItem(id: String, properties: Properties.() -> Unit = { tab(ECTabs.tabItems) }): RegistryObject<Item> {
        val props = Properties().apply(properties)
        return items.register(id) { Item(props) }
    }
}
