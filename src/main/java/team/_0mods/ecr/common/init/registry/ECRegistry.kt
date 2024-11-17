package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import ru.hollowhorizon.hc.client.utils.JavaHacks
import ru.hollowhorizon.hc.client.utils.rl
import ru.hollowhorizon.hc.common.registry.AutoModelType
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import ru.hollowhorizon.hc.common.registry.RegistryObject
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.menu.simpleMenuFactory
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.api.PropertiedBlock
import team._0mods.ecr.common.blocks.*
import team._0mods.ecr.common.blocks.entity.*
import team._0mods.ecr.common.menu.*
import team._0mods.ecr.common.effects.*
import team._0mods.ecr.common.items.*
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.particle.ECParticleType
import team._0mods.ecr.common.recipes.*

object ECRegistry: HollowRegistry(ModId) {
    private val defaultBlockProperties = BlockBehaviour.Properties.of().strength(3f, 3f).requiresCorrectToolForDrops()
    private val clusterProperties = BlockBehaviour.Properties.of().noOcclusion().strength(1.5F).requiresCorrectToolForDrops()

    // Item tabs
    val tabItems by register("tab_items") {
        CreativeModeTab.builder()
            .icon { ItemStack(elementalGem.get()) }
            .title(Component.translatable("itemGroup.$ModId.items"))
            .build()
    }

    val tabBlocks by register("tab_blocks") {
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.$ModId.blocks"))
            .icon { ItemStack(mithrilineFurnace.get()) }
            .withTabsBefore("tab_items".ecRL)
            .build()
    }

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
    val magicTable by register("magic_table", null) { MagicTable(defaultBlockProperties) }
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
        BlockEntityType.Builder.of(XLikeBlockEntity::Envoyer, envoyer.get()).build(promise())
    }
    val magicTableEntity by register("magic_table") {
        BlockEntityType.Builder.of(XLikeBlockEntity::MagicTable, magicTable.get()).build(promise())
    }

    // menu types
    val mithrilineFurnaceMenu by register("mithriline_furnace") { simpleMenuFactory(::MithrilineFurnaceMenu) }
    val matrixDestructorMenu by register("matrix_destructor") { simpleMenuFactory(::MatrixDestructorMenu) }
    val envoyerMenu by register("envoyer") { simpleMenuFactory(XLikeMenu::Envoyer) }
    val magicTableMenu by register("magic_table") { simpleMenuFactory(XLikeMenu::MagicTable) }

    // recipe types
    val mithrilineFurnaceRecipe by register("mithriline_furnace") { RecipeType.simple<MithrilineFurnaceRecipe>("mithriline_furnace".id) }
    val envoyerRecipe by register("envoyer") { RecipeType.simple<XLikeRecipe.Envoyer>("envoyer".id) }
    val magicTableRecipe by register("magic_table") { RecipeType.simple<XLikeRecipe.MagicTable>("magic_table".id) }

    // recipe serializers
    val mithrilineFurnaceRecipeSerial by register("mithriline_furnace") { MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe) }
    val envoyerRecipeSerial by register("envoyer") { XLikeRecipe.Serializer(XLikeRecipe::Envoyer) }
    val magicTableRecipeSerial by register("magic_table") { XLikeRecipe.Serializer(XLikeRecipe::MagicTable) }

    // particles
    val ecParticle by register("ec_part") { ECParticleType() }

    // effects
    val mruCorruption by register("mru_corruption") { MRUCorruption() }

    // sounds
    val calm4 by register("calm4") { SoundEvent.createVariableRangeEvent("calm4".ecRL) }

    private fun basicItem(id: String, autoModel: AutoModelType? = AutoModelType.DEFAULT, props: Item.Properties.() -> Unit = {}, noTab: Boolean = false): RegistryObject<Item> {
        val p = Item.Properties().apply(props)
        val reg by register(id, autoModel) { Item(p) }
        return JavaHacks.forceCast(reg)
    }

    private val String.id: ResourceLocation
        get() = "$ModId:$this".rl
}