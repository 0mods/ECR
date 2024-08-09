package team._0mods.ecr.common.init.registry

import net.minecraft.core.particles.ParticleType
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeSerializer
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.network.IContainerFactory
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.makeBERegistry
import team._0mods.ecr.api.utils.register
import team._0mods.ecr.api.utils.rl
import team._0mods.ecr.common.blocks.CrystalBlock
import team._0mods.ecr.common.blocks.MithrilineFurnace
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.items.BoundGem
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.particle.ECParticleType
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

object ECRegistry {
    private val items: DeferredRegister<Item> = DeferredRegister.create(ForgeRegistries.ITEMS, ModId)
    private val blocksWE = makeBERegistry(ModId)
    private val blocks: DeferredRegister<Block> = DeferredRegister.create(ForgeRegistries.BLOCKS, ModId)
    private val containers: DeferredRegister<MenuType<*>> = DeferredRegister.create(ForgeRegistries.MENU_TYPES, ModId)
    private val recipes: DeferredRegister<RecipeType<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, ModId)
    private val recipeSerializers: DeferredRegister<RecipeSerializer<*>> = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ModId)
    private val particles: DeferredRegister<ParticleType<*>> = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, ModId)

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
        Block(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).requiresCorrectToolForDrops())
    }

    val mithrilineFurnace by blocksWE.register(
        "mithriline_furnace",
        { MithrilineFurnace(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).requiresCorrectToolForDrops()) },
        ::MithrilineFurnaceEntity
    )

    val mithrilineCrystal = block("mithriline_crystal") {
        CrystalBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).requiresCorrectToolForDrops())
    }

    // containers
    val mithrilineFurnaceContainer: RegistryObject<MenuType<MithrilineFurnaceContainer>> = containers.register("mithriline_furnace") {
        MenuType(IContainerFactory { id, inv, buf -> MithrilineFurnaceContainer(id, inv, buf) })
    }

    // recipe types
    val mithrilineFurnaceRecipe: RegistryObject<RecipeType<MithrilineFurnaceRecipe>> = recipes.register("mithriline_furnace") {
        RecipeType.simple("$ModId:mithriline_furnace".rl)
    }

    // recipes serializers
    val mithrilineFurnaceRecipeSerial: RegistryObject<MithrilineFurnaceRecipe.Serializer> = recipeSerializers.register("mithriline_furnace") {
        MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe)
    }

    // particle
    val ecParticle: RegistryObject<ECParticleType> = particles.register("ec_part", ::ECParticleType)

    @JvmStatic
    fun init(bus: IEventBus) {
        items.register(bus)
        blocks.register(bus)
        blocksWE.register(bus)
        containers.register(bus)
        recipes.register(bus)
        recipeSerializers.register(bus)
        particles.register(bus)
    }

    private fun basicItem(id: String, properties: Item.Properties.() -> Unit = { tab(ECTabs.tabItems) }): RegistryObject<Item> {
        val props = Item.Properties().apply(properties)
        return items.register(id) { Item(props) }
    }

    private fun <T: Block> block(id: String, b: () -> T): RegistryObject<T> {
        val block = this.blocks.register(id, b)
        items.register(id) { BlockItem(block.get(), Item.Properties().tab(ECTabs.tabBlocks)) }
        return block
    }
}
