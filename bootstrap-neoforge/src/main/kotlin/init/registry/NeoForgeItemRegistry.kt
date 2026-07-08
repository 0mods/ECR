package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.init.registry.ItemRegistry
import com.algorithmlx.ecr.common.item.SoulStone
import com.algorithmlx.ecr.common.item.ResearchBookItem
import com.algorithmlx.ecr.common.item.tool.WeakAxe
import com.algorithmlx.ecr.common.item.tool.WeakHoe
import com.algorithmlx.ecr.common.item.tool.WeakPickaxe
import com.algorithmlx.ecr.common.item.tool.WeakShovel
import com.algorithmlx.ecr.common.item.tool.WeakSword
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeItemRegistry(bus: IEventBus): ItemRegistry {
    private val items = DeferredRegister.createItems(ModId)

    init {
        items.register(bus)
    }

    private val soulStoneItem = registerItem("soul_stone", ::SoulStone)
    private val researchBookItem = registerItem("research_book", ::ResearchBookItem)

    private val weakAxeItem = registerItem("weakness_elemental_axe", ::WeakAxe)
    private val weakHoeItem = registerItem("weakness_elemental_hoe", ::WeakHoe)
    private val weakPickaxeItem = registerItem("weakness_elemental_pickaxe", ::WeakPickaxe)
    private val weakShovelItem = registerItem("weakness_elemental_shovel", ::WeakShovel)
    private val weakSwordItem = registerItem("weakness_elemental_sword", ::WeakSword)

    val elementalGemItem = basicItem("elemental_gem")
    val flameGemItem = basicItem("flame_gem")
    val waterGemItem = basicItem("water_gem")
    val earthGemItem = basicItem("earth_gem")
    val airGemItem = basicItem("air_gem")

    val elementalCoreItem = basicItem("elemental_core")
    val combinedMagicAlloysItem = basicItem("combined_magic_alloys")
    val demonicCoreItem = basicItem("demonic_core")
    val diamondPlateItem = basicItem("diamond_plate")
    val emeraldPlateItem = basicItem("emerald_plate")
    val enderScaleAlloyItem = basicItem("ender_scale_alloy")
    val forcefieldCoreItem = basicItem("forcefield_core")
    val forcefieldPlationgItem = basicItem("forcefield_plating")
    val fortifiedFrameItem = basicItem("fortified_frame")
    val magicFortifiedPlatingItem = basicItem("magic_fortified_plating")
    val magicPlateItem = basicItem("magic_plate")
    val magicPurifiedBlazeAlloyItem = basicItem("magic_purified_blaze_alloy")
    val magicPurifiedEnderScaleAlloyItem = basicItem("magic_purified_ender_scale_alloy")
    val magicPurifiedGlassAlloyItem = basicItem("magic_purified_glass_alloy")
    val obsidianPlateItem = basicItem("obsidian_plate")
    val paleCoreItem = basicItem("pale_core")
    val palePlateItem = basicItem("pale_plate")
    val particleCatcherItem = basicItem("particle_catcher")
    val particleEmitterItem = basicItem("particle_emitter")
    val sunImbuedGlassItem = basicItem("sun_imbued_glass")
    val voidPlatingItem = basicItem("void_plating")
    val mithrilineIngotItem = basicItem("mithriline_ingot")
    val magicalIngotItem = basicItem("magical_ingot")
    val mithrilineDustItem = basicItem("mithriline_dust")
    val heatingRodItem = basicItem("heating_rod")
    val mithrilineCrystalGemItem = basicItem("mithriline_crystal_gem")
    val mruResonatingCrystalItem = basicItem("mru_resonating_crystal")
    val fadingCrystalItem = basicItem("fading_crystal")

    // implements
    override val soulStone: SoulStone by lazy { soulStoneItem.get() }
    override val researchBook: ResearchBookItem by lazy { researchBookItem.get() }

    override val weaknessElementalAxe: WeakAxe by lazy { weakAxeItem.get() }
    override val weaknessElementalHoe: WeakHoe by lazy { weakHoeItem.get() }
    override val weaknessElementalPickaxe: WeakPickaxe by lazy { weakPickaxeItem.get() }
    override val weaknessElementalShovel: WeakShovel by lazy { weakShovelItem.get() }
    override val weaknessElementalSword: WeakSword by lazy { weakSwordItem.get() }

    override val elementalGem: Item by lazy { elementalGemItem.get() }
    override val flameGem: Item by lazy { flameGemItem.get() }
    override val waterGem: Item by lazy { waterGemItem.get() }
    override val earthGem: Item by lazy { earthGemItem.get() }
    override val airGem: Item by lazy { airGemItem.get() }

    override val elementalCore: Item by lazy { elementalCoreItem.get() }
    override val combinedMagicAlloys: Item by lazy { combinedMagicAlloysItem.get() }
    override val demonicCore: Item by lazy { demonicCoreItem.get() }
    override val diamondPlate: Item by lazy { diamondPlateItem.get() }
    override val emeraldPlate: Item by lazy { emeraldPlateItem.get() }
    override val enderScaleAlloy: Item by lazy { enderScaleAlloyItem.get() }
    override val forcefieldCore: Item by lazy { forcefieldCoreItem.get() }
    override val forcefieldPlationg: Item by lazy { forcefieldPlationgItem.get() }
    override val fortifiedFrame: Item by lazy { fortifiedFrameItem.get() }
    override val magicFortifiedPlating: Item by lazy { magicFortifiedPlatingItem.get() }
    override val magicPlate: Item by lazy { magicPlateItem.get() }
    override val magicPurifiedBlazeAlloy: Item by lazy { magicPurifiedBlazeAlloyItem.get() }
    override val magicPurifiedEnderScaleAlloy: Item by lazy { magicPurifiedEnderScaleAlloyItem.get() }
    override val magicPurifiedGlassAlloy: Item by lazy { magicPurifiedGlassAlloyItem.get() }
    override val obsidianPlate: Item by lazy { obsidianPlateItem.get() }
    override val paleCore: Item by lazy { paleCoreItem.get() }
    override val palePlate: Item by lazy { palePlateItem.get() }
    override val particleCatcher: Item by lazy { particleCatcherItem.get() }
    override val particleEmitter: Item by lazy { particleEmitterItem.get() }
    override val sunImbuedGlass: Item by lazy { sunImbuedGlassItem.get() }
    override val voidPlating: Item by lazy { voidPlatingItem.get() }
    override val mithrilineIngot: Item by lazy { mithrilineIngotItem.get() }
    override val magicalIngot: Item by lazy { magicalIngotItem.get() }
    override val mithrilineDust: Item by lazy { mithrilineDustItem.get() }
    override val heatingRod: Item by lazy { heatingRodItem.get() }
    override val mithrilineCrystalGem: Item by lazy { mithrilineCrystalGemItem.get() }
    override val mruResonatingCrystal: Item by lazy { mruResonatingCrystalItem.get() }
    override val fadingCrystal: Item by lazy { fadingCrystalItem.get() }

    private fun basicItem(id: String, properties: Item.Properties = Item.Properties()) = registerItem(id, ::Item, properties)

    private fun <I: Item> registerItem(
        id: String,
        item: (Item.Properties) -> I,
        properties: Item.Properties = Item.Properties()
    ): DeferredItem<I> {
        val itemKey = { it: Identifier -> ResourceKey.create(Registries.ITEM, it) }
        return items.register(id) { rk -> item(properties.setId(itemKey(rk))) }
    }
}
