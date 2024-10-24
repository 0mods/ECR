package team._0mods.ecr.common.init.registry

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
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.menu.simpleMenuFactory
import team._0mods.ecr.common.api.PropertiedBlock
import team._0mods.ecr.common.blocks.*
import team._0mods.ecr.common.blocks.entity.*
import team._0mods.ecr.common.container.*
import team._0mods.ecr.common.effects.*
import team._0mods.ecr.common.items.*
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.particle.ECParticleType
import team._0mods.ecr.common.recipes.*

object ECRegistry: HollowRegistry(ModId) {
    private val defaultBlockProperties = BlockBehaviour.Properties.of(Material.METAL).strength(3f, 3f).requiresCorrectToolForDrops()
    private val clusterProperties = BlockBehaviour.Properties.of(Material.AMETHYST).noOcclusion().strength(1.5F).requiresCorrectToolForDrops()

    // items
    val flameGem by register("flame_gem", registryEntry = ECGem.flame)
    val waterGem by register("water_gem", registryEntry = ECGem.water)
    val earthGem by register("earth_gem", registryEntry = ECGem.earth)
    val airGem by register("air_gem", registryEntry = ECGem.air)
    val elementalGem by register("elemental_gem", registryEntry = ECGem.elemental)

    val researchBook by register("research_book", null) { ECBook() }

    val soulStone by register("soul_stone") { SoulStone() }
    val boundGem by register("bound_gem") { LocallyBoundGem() }

    val elementalCore = basicItem("elemental_core")
    val combinedMagicAlloys = basicItem("combined_magic_alloys")
    val demonicCore = basicItem("demonic_core")
    val diamondPlate = basicItem("diamond_plate")
    val emeraldPlate = basicItem("emerald_plate")
    val enderScalePlating = basicItem("ender_scale_alloy")
    val forceFieldCore = basicItem("forcefield_core")
    val forceFieldPlating = basicItem("forcefield_plating")
    val fortifiedFrame = basicItem("fortified_frame")
    val magicFortifiedPlating = basicItem("magic_fortified_plating")
    val magicPlate = basicItem("magic_plate")
    val magicPurifiedBlazeAlloy = basicItem("magic_purified_blaze_alloy")
    val magicPurifiedEnderScaleAlloy = basicItem("magic_purified_ender_scale_alloy")
    val magicPurifiedGlassAlloy = basicItem("magic_purified_glass_alloy")
    val obsidianPlate = basicItem("obsidian_plate")
    val paleCore = basicItem("pale_core")
    val palePlate = basicItem("pale_plate")
    val particleCatcher = basicItem("particle_catcher")
    val particleEmitter = basicItem("particle_emitter")
    val sunImbuedGlass = basicItem("sun_imbued_glass")
    val voidPlating = basicItem("void_plating")

    val mithrilineIngot = basicItem("mithriline_ingot")
    val magicalIngot = basicItem("magical_ingot")

    val mithrilineDust = basicItem("mithriline_dust")

    val heatingRod = basicItem("heating_rod")

    val mithrilineCrystalGem = basicItem("mithriline_crystal_gem")
    val mruResonatingCrystal = basicItem("mru_resonating_crystal")
    val fadingCrystal = basicItem("fading_crystal")

    val weakAxe by register("weakness_elemental_axe", AutoModelType.HANDHELD) { WeakAxe() }
    val weakHoe by register("weakness_elemental_hoe", AutoModelType.HANDHELD) { WeakHoe() }
    val weakPickaxe by register("weakness_elemental_pickaxe", AutoModelType.HANDHELD) { WeakPickaxe() }
    val weakShovel by register("weakness_elemental_shovel", AutoModelType.HANDHELD) { WeakShovel() }
    val weakSword by register("weakness_elemental_sword", AutoModelType.HANDHELD) { WeakSword() }

    // blocks
    val mithrilinePlating by register("mithriline_plating", AutoModelType.CUBE_ALL) { PropertiedBlock(defaultBlockProperties) }
    val mithrilineFurnace by register("mithriline_furnace", null) { MithrilineFurnace(defaultBlockProperties.noOcclusion()) }
    val matrixDestructor by register("matrix_destructor", null) { MatrixDestructor(defaultBlockProperties.noOcclusion()) }
    val envoyer by register("envoyer", null) { Envoyer(defaultBlockProperties.noOcclusion()) }
    val mithrilineCrystal by register("mithriline_crystal", null) { CrystalBlock(defaultBlockProperties.noOcclusion()) }
    val voidStone by register("void_stone", AutoModelType.CUBE_ALL) { PropertiedBlock(defaultBlockProperties) }
    val paleBlock by register("pale_block", AutoModelType.CUBE_ALL) { PropertiedBlock(defaultBlockProperties) }
    val palePlating by register("pale_plating", AutoModelType.CUBE_ALL) { PropertiedBlock(defaultBlockProperties) }
    val magicPlating by register("magic_plating", AutoModelType.CUBE_ALL) { PropertiedBlock(defaultBlockProperties) }
    val demonicPlating by register("demonic_plating", AutoModelType.CUBE_ALL) { PropertiedBlock(defaultBlockProperties) }
    val flameCluster by register("flame_cluster", null) { ClusterBlock(clusterProperties) }
    val waterCluster by register("water_cluster", null) { ClusterBlock(clusterProperties) }
    val earthCluster by register("earth_cluster", null) { ClusterBlock(clusterProperties) }
    val airCluster by register("air_cluster", null) { ClusterBlock(clusterProperties) }

    // blockEntity
    val mithrilineFurnaceEntity by register("mithriline_furnace") {
        BlockEntityType.Builder.of(::MithrilineFurnaceEntity, mithrilineFurnace.get()).build(promise())
    }
    val matrixDestructorEntity by register("matrix_destructor") {
        BlockEntityType.Builder.of(::MatrixDestructorEntity, matrixDestructor.get()).build(promise())
    }
    val envoyerEntity by register("envoyer") {
        BlockEntityType.Builder.of(::EnvoyerBlockEntity, envoyer.get()).build(promise())
    }

    // menu types
    val mithrilineFurnaceContainer by register("mithriline_furnace") { simpleMenuFactory(::MithrilineFurnaceContainer) }
    val matrixDestructorContainer by register("matrix_destructor") { simpleMenuFactory(::MatrixDestructorContainer) }
    val envoyerContainer by register("envoyer") { simpleMenuFactory(::EnvoyerContainer) }

    // recipe types
    val mithrilineFurnaceRecipe by register("mithriline_furnace") { RecipeType.simple<MithrilineFurnaceRecipe>("mithriline_furnace".id) }
    val envoyerRecipe by register("envoyer") { RecipeType.simple<EnvoyerRecipe>("envoyer".id) }

    // recipe serializers
    val mithrilineFurnaceRecipeSerial by register("mithriline_furnace") { MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe) }
    val envoyerRecipeSerial by register("envoyer") { EnvoyerRecipe.Serializer(::EnvoyerRecipe) }

    // particles
    val ecParticle by register("ec_part") { ECParticleType() }

    // effects
    val mruCorruption by register("mru_corruption", registryEntry = ::MRUCorruption)

    private fun basicItem(id: String, autoModel: AutoModelType? = AutoModelType.DEFAULT, props: Item.Properties.() -> Unit = { this.tab(ECTabs.tabItems) }): RegistryObject<Item> {
        val p = Item.Properties().apply(props)
        val reg by register(id, autoModel) { Item(p) }
        return reg
    }

    private val String.id: ResourceLocation
        get() = "$ModId:$this".rl
}