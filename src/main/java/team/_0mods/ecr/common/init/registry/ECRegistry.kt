package team._0mods.ecr.common.init.registry

import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.network.IContainerFactory
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import team._0mods.ecr.ModId
import team._0mods.ecr.common.blocks.MithrilineFurnace
import team._0mods.ecr.common.blocks.base.ConnectedTextureBlock
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.items.BoundGem
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.makeBERegistry
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe
import team._0mods.ecr.common.register

object ECRegistry {
    private val items: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)
    private val blocksWE = makeBERegistry(ModId)
    private val blocks: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)
    private val containers: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModId)
    private val recipes: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModId)

    // items
    val flameGem: RegistryObject<ECGem> = items.register("flame_gem", ECGem.flame)
    val waterGem: RegistryObject<ECGem> = items.register("water_gem", ECGem.water)
    val earthGem: RegistryObject<ECGem> = items.register("earth_gem", ECGem.earth)
    val airGem: RegistryObject<ECGem> = items.register("air_gem", ECGem.air)
    val elementalGem: RegistryObject<ECGem> = items.register("elemental_gem", ECGem.elemental)

    val researchBook: RegistryObject<ECBook> = items.register("research_book", ::ECBook)

    val soulStone: RegistryObject<SoulStone> = items.register("soul_stone", ::SoulStone)
    val boundGem: RegistryObject<BoundGem> = items.register("bound_gem", ::BoundGem)

    val elementalCore = basicItem("elemental_core")

    val weakAxe: RegistryObject<WeakAxe> = items.register("weakness_elemental_axe", ::WeakAxe)
    val weakHoe: RegistryObject<WeakHoe> = items.register("weakness_elemental_hoe", ::WeakHoe)
    val weakPickaxe: RegistryObject<WeakPickaxe> = items.register("weakness_elemental_pickaxe", ::WeakPickaxe)
    val weakShovel: RegistryObject<WeakShovel> = items.register("weakness_elemental_shovel", ::WeakShovel)
    val weakSword: RegistryObject<WeakSword> = items.register("weakness_elemental_sword", ::WeakSword)

    // blocks
    val mithrilinePlating = block("mithriline_plating") {
        ConnectedTextureBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f))
    }

    val mithrilineFurnace by blocksWE.register(
        "mithriline_furnace",
        { MithrilineFurnace(BlockBehaviour.Properties.of(Material.METAL)) },
        ::MithrilineFurnaceEntity
    )

    // containers
    val mithrilineFurnaceContainer: RegistryObject<MenuType<MithrilineFurnaceContainer>> = containers.register("mithriline_furnace") {
        MenuType(IContainerFactory { id, inv, buf -> MithrilineFurnaceContainer(id, inv, buf) })
    }

    // recipes
    val mithrilineFurnaceRecipe: RegistryObject<MithrilineFurnaceRecipe.Serializer> = recipes.register("mithriline_furnace") {
        MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe)
    }

    @JvmStatic
    fun init(bus: IEventBus) {
        items.register(bus)
        blocks.register(bus)
        blocksWE.register(bus)
        containers.register(bus)
        recipes.register(bus)
    }

    private fun basicItem(id: String, properties: Properties.() -> Unit = { tab(ECTabs.tabItems) }): RegistryObject<Item> {
        val props = Properties().apply(properties)
        return items.register(id) { Item(props) }
    }

    private fun <T: Block> block(id: String, b: () -> T): RegistryObject<T> {
        val block = this.blocks.register(id, b)
        items.register(id) { BlockItem(block.get(), Item.Properties().tab(ECTabs.tabBlocks)) }
        return block
    }
}
