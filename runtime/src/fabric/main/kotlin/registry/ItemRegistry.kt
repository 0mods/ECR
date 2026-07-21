package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.registries.ECRegistries
import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.item.*
import com.algorithmlx.ecr.common.item.tool.*
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object ItemRegistry {
    actual val soulStone: SoulStone = register(ECRModIDs.SOUL_STONE, ::SoulStone)
    actual val researchBook: ResearchBookItem = register(
        ECRModIDs.RESEARCH_BOOK, ::ResearchBookItem, Item.Properties().component(
            DataComponentRegistry.bookType,
            ECRegistries.BOOK_TYPES.getResourceKey(BookTypeRegistry.basic).get()
        )
    )
    actual val boundGem: BoundGemItem = register(ECRModIDs.BOUND_GEM, ::BoundGemItem)

    actual val weaknessElementalAxe: WeakAxe = register(ECRModIDs.WEAKNESS_ELEMENTAL_AXE, ::WeakAxe)
    actual val weaknessElementalHoe: WeakHoe = register(ECRModIDs.WEAKNESS_ELEMENTAL_HOE, ::WeakHoe)
    actual val weaknessElementalPickaxe: WeakPickaxe = register(ECRModIDs.WEAKNESS_ELEMENTAL_PICKAXE, ::WeakPickaxe)
    actual val weaknessElementalShovel: WeakShovel = register(ECRModIDs.WEAKNESS_ELEMENTAL_SHOVEL, ::WeakShovel)
    actual val weaknessElementalSword: WeakSword = register(ECRModIDs.WEAKNESS_ELEMENTAL_SWORD, ::WeakSword)

    actual val elementalGem: Item = basicItem(ECRModIDs.ELEMENTAL_GEM)
    actual val flameGem: Item = basicItem(ECRModIDs.FLAME_GEM)
    actual val waterGem: Item = basicItem(ECRModIDs.WATER_GEM)
    actual val earthGem: Item = basicItem(ECRModIDs.EARTH_GEM)
    actual val airGem: Item = basicItem(ECRModIDs.AIR_GEM)

    actual val elementalCore: Item = basicItem(ECRModIDs.ELEMENTAL_CORE)
    actual val combinedMagicAlloys: Item = basicItem(ECRModIDs.COMBINED_MAGIC_ALLOYS)
    actual val demonicCore: Item = basicItem(ECRModIDs.DEMONIC_CORE)
    actual val diamondPlate: Item = basicItem(ECRModIDs.DIAMOND_PLATE)
    actual val emeraldPlate: Item = basicItem(ECRModIDs.EMERALD_PLATE)
    actual val enderScaleAlloy: Item = basicItem(ECRModIDs.ENDER_SCALE_ALLOY)
    actual val forcefieldCore: Item = basicItem(ECRModIDs.FORCEFIELD_CORE)
    actual val forcefieldPlating: Item = basicItem(ECRModIDs.FORCIFIELD_PLATING)
    actual val fortifiedFrame: Item = basicItem(ECRModIDs.FORTIFIED_FRAME)
    actual val magicFortifiedPlating: Item = basicItem(ECRModIDs.MAGIC_FORTIFIED_PLATING)
    actual val magicPlate: Item = basicItem(ECRModIDs.MAGIC_PLATE)
    actual val magicPurifiedBlazeAlloy: Item = basicItem(ECRModIDs.MAGIC_PURIFIED_BLAZE_ALLOY)
    actual val magicPurifiedEnderScaleAlloy: Item = basicItem(ECRModIDs.MAGIC_PURIFIED_ENDER_SCALE_ALLOY)
    actual val magicPurifiedGlassAlloy: Item = basicItem(ECRModIDs.MAGIC_PURIFIED_GLASS_ALLOY)
    actual val obsidianPlate: Item = basicItem(ECRModIDs.OBSIDIAN_PLATE)
    actual val paleCore: Item = basicItem(ECRModIDs.PALE_CORE)
    actual val palePlate: Item = basicItem(ECRModIDs.PALE_PLATE)
    actual val particleCatcher: Item = basicItem(ECRModIDs.PARTICLE_CATCHER)
    actual val particleEmitter: Item = basicItem(ECRModIDs.PARTICLE_EMITTER)
    actual val sunImbuedGlass: Item = basicItem(ECRModIDs.SUN_IMBUED_GLASS)
    actual val voidPlating: Item = basicItem(ECRModIDs.VOID_PLATING)
    actual val mithrilineIngot: Item = basicItem(ECRModIDs.MITHRILINE_INGOT)
    actual val magicalIngot: Item = basicItem(ECRModIDs.MAGICAL_INGOT)
    actual val mithrilineDust: Item = basicItem(ECRModIDs.MITHRILINE_DUST)
    actual val heatingRod: Item = basicItem(ECRModIDs.HEATING_ROD)
    actual val mithrilineCrystalGem: Item = basicItem(ECRModIDs.MITHRILINE_CRYSTAL_GEM)
    actual val mruResonatingCrystal: Item = basicItem(ECRModIDs.MRU_RESONATING_CRYSTAL)
    actual val fadingCrystal: Item = basicItem(ECRModIDs.FADING_CRYSTAL)

    private fun basicItem(id: String, properties: Item.Properties = Item.Properties()) = register(id, ::Item, properties)

    private fun <T: Item> register(
        id: String,
        item: (Item.Properties) -> T,
        properties: Item.Properties = Item.Properties()
    ): T {
        val itemKey = { it: Identifier -> ResourceKey.create(Registries.ITEM, it) }
        val resId = id.ecRL
        return Registry.register(BuiltInRegistries.ITEM, id.ecRL, item(properties.setId(itemKey(resId))))
    }
}