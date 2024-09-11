package team._0mods.ecr.common.init.registry

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import ru.hollowhorizon.hc.client.utils.rl
import ru.hollowhorizon.hc.common.registry.AutoModelType
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import ru.hollowhorizon.hc.common.registry.RegistryObject
import team._0mods.ecr.ModId
import team._0mods.ecr.api.block.PropertiedBlock
import team._0mods.ecr.api.menu.simpleMenuFactory
import team._0mods.ecr.common.blocks.*
import team._0mods.ecr.common.blocks.entity.*
import team._0mods.ecr.common.container.*
import team._0mods.ecr.common.effects.*
import team._0mods.ecr.common.items.*
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.particle.ECParticleType
import team._0mods.ecr.common.recipes.*

object ECRegistry: HollowRegistry() {
    // items
    val flameGem by register("flame_gem".id, registryEntry = ECGem.flame)
    val waterGem by register("water_gem".id, registryEntry = ECGem.flame)
    val earthGem by register("earth_gem".id, registryEntry = ECGem.flame)
    val airGem by register("air_gem".id, registryEntry = ECGem.flame)
    val elementalGem by register("elemental_gem".id, registryEntry = ECGem.flame)

    val researchBook by register("research_book".id, null) { ECBook() }

    val soulStone by register("soul_stone".id) { SoulStone() }
    val boundGem by register("bound_gem".id) { LocallyBoundGem() }

    val elementalCore = basicItem("elemental_core")

    val weakAxe by register("weakness_elemental_axe".id, AutoModelType.HANDHELD) { WeakAxe() }
    val weakHoe by register("weakness_elemental_hoe".id, AutoModelType.HANDHELD) { WeakHoe() }
    val weakPickaxe by register("weakness_elemental_pickaxe".id, AutoModelType.HANDHELD) { WeakPickaxe() }
    val weakShovel by register("weakness_elemental_shovel".id, AutoModelType.HANDHELD) { WeakShovel() }
    val weakSword by register("weakness_elemental_sword".id, AutoModelType.HANDHELD) { WeakSword() }

    // blocks
    val mithrilinePlating by register("mithriline_plating".id, registry = Registry.BLOCK) {
        PropertiedBlock(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).requiresCorrectToolForDrops())
    }
    val mithrilineFurnace by register("mithriline_furnace".id, null) {
        MithrilineFurnace(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).noOcclusion().requiresCorrectToolForDrops())
    }
    val matrixDestructor by register("matrix_destructor".id, null) {
        MatrixDestructor(BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).noOcclusion().requiresCorrectToolForDrops())
    }
    val envoyer by register("envoyer".id, null) { Envoyer(BlockBehaviour.Properties.of(Material.METAL)) }
    val mithrilineCrystal by register("mithriline_crystal".id) {
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
    val mithrilineFurnaceRecipe by register("mithriline_furnace".id) { RecipeType.simple<MithrilineFurnaceRecipe>("mithriline_furnace".id) }
    val envoyerRecipe by register("envoyer".id) { RecipeType.simple<EnvoyerRecipe>("envoyer".id) }

    // recipe serializers
    val mithrilineFurnaceRecipeSerial by register("mithriline_furnace".id) { MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe) }
    val envoyerRecipeSerial by register("envoyer".id) { EnvoyerRecipe.Serializer(::EnvoyerRecipe) }

    // particles
    val ecParticle by register("ec_part".id) { ECParticleType() }

    // effects
    val mruCorruption by register("mru_corruption".id) { MRUCorruption() }

    private fun basicItem(id: String, autoModel: AutoModelType? = AutoModelType.DEFAULT, props: Item.Properties.() -> Unit = {}): RegistryObject<Item> {
        val p = Item.Properties().apply(props)
        val reg by register(id.id, autoModel) { Item(p) }
        return reg
    }

    private val String.id: ResourceLocation
        get() = "$ModId:$this".rl
}