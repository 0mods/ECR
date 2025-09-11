package team._0mods.ecr.common.init.registry

import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockBehaviour
import ru.hollowhorizon.hc.common.registry.AutoModelType
import ru.hollowhorizon.hc.common.registry.HollowRegistry
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.utils.simpleBlockEntityType
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.api.utils.simpleMenuFactory
import team._0mods.ecr.api.utils.simpleRecipeType
import team._0mods.ecr.common.api.PropertiedBlock
import team._0mods.ecr.common.blocks.*
import team._0mods.ecr.common.blocks.entity.ColdDistillerEntity
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.blocks.entity.XLikeBlockEntity
import team._0mods.ecr.common.effects.MRUCorruption
import team._0mods.ecr.common.items.ECBook
import team._0mods.ecr.common.items.ECGem
import team._0mods.ecr.common.items.LocallyBoundGem
import team._0mods.ecr.common.items.SoulStone
import team._0mods.ecr.common.items.tools.*
import team._0mods.ecr.common.menu.MatrixDestructorMenu
import team._0mods.ecr.common.menu.MithrilineFurnaceMenu
import team._0mods.ecr.common.menu.XLikeMenu
import team._0mods.ecr.common.particle.ECParticleType
import team._0mods.ecr.common.recipes.MithrilineFurnaceRecipe
import team._0mods.ecr.common.recipes.StructureRecipe
import team._0mods.ecr.common.recipes.XLikeRecipe

object ECRRegistry: HollowRegistry(ModId) {
    private val defaultBlockProperties = BlockBehaviour.Properties.of().strength(3f, 3f).requiresCorrectToolForDrops()
    private val clusterProperties = BlockBehaviour.Properties.of().noOcclusion().strength(1.5F).requiresCorrectToolForDrops()

    // Item tabs
    val tabItems by creativeTab("tab_items") {
        icon { ItemStack(elementalGem) }
        title(Component.translatable("itemGroup.$ModId.items"))
    }

    val tabBlocks by creativeTab("tab_blocks") {
        title(Component.translatable("itemGroup.$ModId.blocks"))
        icon { ItemStack(mithrilineFurnace) }
        //? if forge
        /*withTabsBefore("tab_items".ecRL)*/
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
    val mithrilineFurnace by register("mithriline_furnace", null) { MithrilineFurnace(defaultBlockProperties) }
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
    val solarPrism by register("solar_prism", null) { SolarPrism(defaultBlockProperties.noOcclusion()) }
    val coldDistiller by register("cold_distiller", null) { ColdDistiller(defaultBlockProperties.noOcclusion()) }

    // blockEntity
    val mithrilineFurnaceEntity by register("mithriline_furnace") { simpleBlockEntityType(::MithrilineFurnaceEntity, mithrilineFurnace) }
    val matrixDestructorEntity by register("matrix_destructor") { simpleBlockEntityType(::MatrixDestructorEntity, matrixDestructor) }
    val envoyerEntity by register("envoyer") { simpleBlockEntityType(XLikeBlockEntity::Envoyer, envoyer) }
    val magicTableEntity by register("magic_table") { simpleBlockEntityType(XLikeBlockEntity::MagicTable, magicTable) }
    val coldDistillerEntity by register("cold_distiller") { simpleBlockEntityType(::ColdDistillerEntity, coldDistiller) }

    // menu types
    val mithrilineFurnaceMenu by register("mithriline_furnace") { simpleMenuFactory(::MithrilineFurnaceMenu) }
    val matrixDestructorMenu by register("matrix_destructor") { simpleMenuFactory(::MatrixDestructorMenu) }
    val envoyerMenu by register("envoyer") { simpleMenuFactory(XLikeMenu::Envoyer) }
    val magicTableMenu by register("magic_table") { simpleMenuFactory(XLikeMenu::MagicTable) }

    // recipe types
    val mithrilineFurnaceRecipe by register("mithriline_furnace") { simpleRecipeType<MithrilineFurnaceRecipe>(it) }
    val envoyerRecipe by register("envoyer") { simpleRecipeType<XLikeRecipe.Envoyer>(it) }
    val magicTableRecipe by register("magic_table") { simpleRecipeType<XLikeRecipe.MagicTable>(it) }
    val structureRecipe by register("structure") { simpleRecipeType<StructureRecipe>(it) }

    // recipe serializers
    val mithrilineFurnaceRecipeSerial by register("mithriline_furnace") { MithrilineFurnaceRecipe.Serializer(::MithrilineFurnaceRecipe) }
    val envoyerRecipeSerial by register("envoyer") { XLikeRecipe.Serializer(XLikeRecipe::Envoyer) }
    val magicTableRecipeSerial by register("magic_table") { XLikeRecipe.Serializer(XLikeRecipe::MagicTable) }
    val structureRecipeSerial by register("structure") { StructureRecipe.Serializer(::StructureRecipe) }

    // particles
    val ecParticle by register("ec_part") { ECParticleType() }

    // effects
    val mruCorruption by register("mru_corruption") { MRUCorruption() }

    // sounds
    val calm4 by register("calm4") { SoundEvent.createVariableRangeEvent("calm4".ecRL) }

    private fun basicItem(id: String, autoModel: AutoModelType? = AutoModelType.DEFAULT, props: Item.Properties.() -> Unit = {}, noTab: Boolean = false): Item {
        val p = Item.Properties().apply(props)
        val reg by register(id, autoModel) { Item(p) }
        return reg
    }
}