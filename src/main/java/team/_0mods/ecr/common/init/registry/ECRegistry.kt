package team._0mods.ecr.common.init.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import ru.hollowhorizon.hc.client.utils.rl
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import ru.hollowhorizon.hc.common.registry.RegistryObject
import team._0mods.ecr.ModId
import team._0mods.ecr.api.menu.simpleMenuFactory
import team._0mods.ecr.common.blocks.CrystalBlock
import team._0mods.ecr.common.blocks.Envoyer
import team._0mods.ecr.common.blocks.MatrixDestructor
import team._0mods.ecr.common.blocks.MithrilineFurnace
import team._0mods.ecr.common.blocks.entity.EnvoyerBlockEntity
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.container.EnvoyerContainer
import team._0mods.ecr.common.container.MatrixDestructorContainer
import team._0mods.ecr.common.container.MithrilineFurnaceContainer
import team._0mods.ecr.common.effects.MRUCorruption
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem
import team._0mods.ecr.common.items.LocallyBoundGem
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.particle.ECParticleType
import team._0mods.ecr.common.recipes.EnvoyerRecipe
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe

object ECRegistry: HollowRegistry() {
    // items
    val flameGem by register("flame_gem".id, registryEntry = ECGem.flame)
    val waterGem by register("water_gem".id, registryEntry = ECGem.flame)
    val earthGem by register("earth_gem".id, registryEntry = ECGem.flame)
    val airGem by register("air_gem".id, registryEntry = ECGem.flame)
    val elementalGem by register("elemental_gem".id, registryEntry = ECGem.flame)

    val researchBook by register("research_book".id, false) { ECBook() }

    val soulStone by register("soul_stone".id) { SoulStone() }
    val boundGem by register("bound_gem".id, false) { LocallyBoundGem() }

    val elementalCore = basicItem("elemental_core")

    val weakAxe by register("weakness_elemental_axe".id) { WeakAxe() }
    val weakHoe by register("weakness_elemental_hoe".id) { WeakHoe() }
    val weakPickaxe by register("weakness_elemental_pickaxe".id) { WeakPickaxe() }
    val weakShovel by register("weakness_elemental_shovel".id) { WeakShovel() }
    val weakSword by register("weakness_elemental_sword".id) { WeakSword() }

    // blocks
    val mithrilinePlating by register("mithriline_plating".id, registry = Registry.BLOCK) {
        Block(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).requiresCorrectToolForDrops())
    }
    val mithrilineFurnace by register("mithriline_furnace".id, false, Registry.BLOCK) {
        MithrilineFurnace(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).noOcclusion().requiresCorrectToolForDrops())
    }
    val matrixDestructor by register("matrix_destructor".id, false, Registry.BLOCK) {
        MatrixDestructor(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).noOcclusion().requiresCorrectToolForDrops())
    }
    val envoyer by register("envoyer".id, false, registry = Registry.BLOCK) { Envoyer(BlockBehaviour.Properties.of(Material.METAL)) }
    val mithrilineCrystal by register("mithriline_crystal".id, registry = Registry.BLOCK) {
        CrystalBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).noOcclusion().requiresCorrectToolForDrops())
    }

    // blockEntity
    val mithrilineFurnaceEntity by register("mithriline_furnace".id) {
        BlockEntityType.Builder.of(::MithrilineFurnaceEntity, mithrilineFurnace.get()).build(promise())
    }
    val matrixDestructorEntity by register("matrix_destructor".id) {
        BlockEntityType.Builder.of(::MatrixDestructorEntity, matrixDestructor.get()).build(promise())
    }
    val envoyerEntity by register("envoyer".id) {
        BlockEntityType.Builder.of(::EnvoyerBlockEntity, envoyer.get()).build(promise())
    }

    // menu types
    val mithrilineFurnaceContainer by register("mithriline_furnace".id) { simpleMenuFactory(::MithrilineFurnaceContainer) }
    val matrixDestructorContainer by register("matrix_destructor".id) { simpleMenuFactory(::MatrixDestructorContainer) }
    val envoyerContainer by register("envoyer".id) { simpleMenuFactory(::EnvoyerContainer) }

    // recipe types
    val mithrilineFurnaceRecipe by register("mithriline_furnace".id, registry = Registry.RECIPE_TYPE) { RecipeType.simple<MithrilineFurnaceRecipe>("mithriline_furnace".id) }
    val envoyerRecipe by register("envoyer".id, registry = Registry.RECIPE_TYPE) { RecipeType.simple<EnvoyerRecipe>("envoyer".id) }

    // recipe serializers
    val mithrilineFurnaceRecipeSerial by register("mithriline_furnace".id) { MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe) }
    val envoyerRecipeSerial by register("envoyer".id) { EnvoyerRecipe.Serializer(::EnvoyerRecipe) }

    // particles
    val ecParticle by register("ec_part".id) { ECParticleType() }

    // effects
    val mruCorruption by register("mru_corruption".id, registry = Registry.MOB_EFFECT) { MRUCorruption() }

    private fun basicItem(id: String, autoModel: Boolean = true, props: Item.Properties.() -> Unit = {}): RegistryObject<Item> {
        val p = Item.Properties().apply(props)
        val reg by register(id.id, autoModel) { Item(p) }
        return reg
    }

    private val String.id: ResourceLocation
        get() = "$ModId:$this".rl
}