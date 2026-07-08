package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.common.init.registry.ItemRegistry
import com.algorithmlx.ecr.common.item.SoulStone
import com.algorithmlx.ecr.common.item.ResearchBookItem
import com.algorithmlx.ecr.common.item.tool.WeakAxe
import com.algorithmlx.ecr.common.item.tool.WeakHoe
import com.algorithmlx.ecr.common.item.tool.WeakPickaxe
import com.algorithmlx.ecr.common.item.tool.WeakShovel
import com.algorithmlx.ecr.common.item.tool.WeakSword
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

object FabricItemRegistry: ItemRegistry {
    override val soulStone: SoulStone = register("soul_stone", ::SoulStone)
    override val researchBook: ResearchBookItem = register("research_book", ::ResearchBookItem)

    override val weaknessElementalAxe: WeakAxe = register("weakness_elemental_axe", ::WeakAxe)
    override val weaknessElementalHoe: WeakHoe = register("weakness_elemental_hoe", ::WeakHoe)
    override val weaknessElementalPickaxe: WeakPickaxe = register("weakness_elemental_pickaxe", ::WeakPickaxe)
    override val weaknessElementalShovel: WeakShovel = register("weakness_elemental_shovel", ::WeakShovel)
    override val weaknessElementalSword: WeakSword = register("weakness_elemental_sword", ::WeakSword)

    override val elementalGem: Item = basicItem("elemental_gem")
    override val flameGem: Item = basicItem("flame_gem")
    override val waterGem: Item = basicItem("water_gem")
    override val earthGem: Item = basicItem("earth_gem")
    override val airGem: Item = basicItem("air_gem")

    override val elementalCore: Item = basicItem("elemental_core")
    override val combinedMagicAlloys: Item = basicItem("combined_magic_alloys")
    override val demonicCore: Item = basicItem("demonic_core")
    override val diamondPlate: Item = basicItem("diamond_plate")
    override val emeraldPlate: Item = basicItem("emerald_plate")
    override val enderScaleAlloy: Item = basicItem("ender_scale_alloy")
    override val forcefieldCore: Item = basicItem("forcefield_core")
    override val forcefieldPlationg: Item = basicItem("forcefield_plating")
    override val fortifiedFrame: Item = basicItem("fortified_frame")
    override val magicFortifiedPlating: Item = basicItem("magic_fortified_plating")
    override val magicPlate: Item = basicItem("magic_plate")
    override val magicPurifiedBlazeAlloy: Item = basicItem("magic_purified_blaze_alloy")
    override val magicPurifiedEnderScaleAlloy: Item = basicItem("magic_purified_ender_scale_alloy")
    override val magicPurifiedGlassAlloy: Item = basicItem("magic_purified_glass_alloy")
    override val obsidianPlate: Item = basicItem("obsidian_plate")
    override val paleCore: Item = basicItem("pale_core")
    override val palePlate: Item = basicItem("pale_plate")
    override val particleCatcher: Item = basicItem("particle_catcher")
    override val particleEmitter: Item = basicItem("particle_emitter")
    override val sunImbuedGlass: Item = basicItem("sun_imbued_glass")
    override val voidPlating: Item = basicItem("void_plating")
    override val mithrilineIngot: Item = basicItem("mithriline_ingot")
    override val magicalIngot: Item = basicItem("magical_ingot")
    override val mithrilineDust: Item = basicItem("mithriline_dust")
    override val heatingRod: Item = basicItem("heating_rod")
    override val mithrilineCrystalGem: Item = basicItem("mithriline_crystal_gem")
    override val mruResonatingCrystal: Item = basicItem("mru_resonating_crystal")
    override val fadingCrystal: Item = basicItem("fading_crystal")

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
