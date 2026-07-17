package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.registries.ECRegistryKeys
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.DataComponentRegistry
import com.algorithmlx.ecr.common.init.registry.ItemRegistry
import com.algorithmlx.ecr.common.item.BoundGemItem
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

    private val soulStoneItem = registerItem(ECRModIDs.SOUL_STONE, ::SoulStone)
    private val researchBookItem = registerItem(
        ECRModIDs.RESEARCH_BOOK, ::ResearchBookItem
    ) {
        Item.Properties().delayedComponent(
            DataComponentRegistry.instance.bookType
        ) {
            it.lookupOrThrow(ECRegistryKeys.BOOK_TYPE_KEY)
                .getOrThrow(ResourceKey.create(ECRegistryKeys.BOOK_TYPE_KEY, ECRModIDs.BASIC.ecRL))
                .value()
        }
    }
    private val boundGemItem = registerItem(ECRModIDs.BOUND_GEM, ::BoundGemItem)

    private val weakAxeItem = registerItem(ECRModIDs.WEAKNESS_ELEMENTAL_AXE, ::WeakAxe)
    private val weakHoeItem = registerItem(ECRModIDs.WEAKNESS_ELEMENTAL_HOE, ::WeakHoe)
    private val weakPickaxeItem = registerItem(ECRModIDs.WEAKNESS_ELEMENTAL_PICKAXE, ::WeakPickaxe)
    private val weakShovelItem = registerItem(ECRModIDs.WEAKNESS_ELEMENTAL_SHOVEL, ::WeakShovel)
    private val weakSwordItem = registerItem(ECRModIDs.WEAKNESS_ELEMENTAL_SWORD, ::WeakSword)

    val elementalGemItem = basicItem(ECRModIDs.ELEMENTAL_GEM)
    val flameGemItem = basicItem(ECRModIDs.FLAME_GEM)
    val waterGemItem = basicItem(ECRModIDs.WATER_GEM)
    val earthGemItem = basicItem(ECRModIDs.EARTH_GEM)
    val airGemItem = basicItem(ECRModIDs.AIR_GEM)

    val elementalCoreItem = basicItem(ECRModIDs.ELEMENTAL_CORE)
    val combinedMagicAlloysItem = basicItem(ECRModIDs.COMBINED_MAGIC_ALLOYS)
    val demonicCoreItem = basicItem(ECRModIDs.DEMONIC_CORE)
    val diamondPlateItem = basicItem(ECRModIDs.DIAMOND_PLATE)
    val emeraldPlateItem = basicItem(ECRModIDs.EMERALD_PLATE)
    val enderScaleAlloyItem = basicItem(ECRModIDs.ENDER_SCALE_ALLOY)
    val forcefieldCoreItem = basicItem(ECRModIDs.FORCEFIELD_CORE)
    val forcefieldPlationgItem = basicItem(ECRModIDs.FORCIFIELD_PLATING)
    val fortifiedFrameItem = basicItem(ECRModIDs.FORTIFIED_FRAME)
    val magicFortifiedPlatingItem = basicItem(ECRModIDs.MAGIC_FORTIFIED_PLATING)
    val magicPlateItem = basicItem(ECRModIDs.MAGIC_PLATE)
    val magicPurifiedBlazeAlloyItem = basicItem(ECRModIDs.MAGIC_PURIFIED_BLAZE_ALLOY)
    val magicPurifiedEnderScaleAlloyItem = basicItem(ECRModIDs.MAGIC_PURIFIED_ENDER_SCALE_ALLOY)
    val magicPurifiedGlassAlloyItem = basicItem(ECRModIDs.MAGIC_PURIFIED_GLASS_ALLOY)
    val obsidianPlateItem = basicItem(ECRModIDs.OBSIDIAN_PLATE)
    val paleCoreItem = basicItem(ECRModIDs.PALE_CORE)
    val palePlateItem = basicItem(ECRModIDs.PALE_PLATE)
    val particleCatcherItem = basicItem(ECRModIDs.PARTICLE_CATCHER)
    val particleEmitterItem = basicItem(ECRModIDs.PARTICLE_EMITTER)
    val sunImbuedGlassItem = basicItem(ECRModIDs.SUN_IMBUED_GLASS)
    val voidPlatingItem = basicItem(ECRModIDs.VOID_PLATING)
    val mithrilineIngotItem = basicItem(ECRModIDs.MITHRILINE_INGOT)
    val magicalIngotItem = basicItem(ECRModIDs.MAGICAL_INGOT)
    val mithrilineDustItem = basicItem(ECRModIDs.MITHRILINE_DUST)
    val heatingRodItem = basicItem(ECRModIDs.HEATING_ROD)
    val mithrilineCrystalGemItem = basicItem(ECRModIDs.MITHRILINE_CRYSTAL_GEM)
    val mruResonatingCrystalItem = basicItem(ECRModIDs.MRU_RESONATING_CRYSTAL)
    val fadingCrystalItem = basicItem(ECRModIDs.FADING_CRYSTAL)

    // implements
    override val soulStone: SoulStone by lazy { soulStoneItem.get() }
    override val researchBook: ResearchBookItem by lazy { researchBookItem.get() }
    override val boundGem: BoundGemItem by lazy { boundGemItem.get() }

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
    override val forcefieldPlating: Item by lazy { forcefieldPlationgItem.get() }
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

    private fun basicItem(
        id: String,
        properties: () -> Item.Properties = Item::Properties
    ) = registerItem(id, ::Item, properties)

    private fun <I: Item> registerItem(
        id: String,
        item: (Item.Properties) -> I,
        properties: () -> Item.Properties = Item::Properties
    ): DeferredItem<I> {
        val itemKey = { it: Identifier -> ResourceKey.create(Registries.ITEM, it) }
        return items.register(id) { rk -> item(properties().setId(itemKey(rk))) }
    }
}
