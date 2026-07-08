package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.item.*
import com.algorithmlx.ecr.common.item.tool.*
import net.minecraft.world.item.Item

interface ItemRegistry {
    val soulStone: SoulStone
    val researchBook: ResearchBookItem

    val weaknessElementalAxe: WeakAxe
    val weaknessElementalHoe: WeakHoe
    val weaknessElementalPickaxe: WeakPickaxe
    val weaknessElementalShovel: WeakShovel
    val weaknessElementalSword: WeakSword

    val elementalGem: Item
    val flameGem: Item
    val waterGem: Item
    val earthGem: Item
    val airGem: Item

    val elementalCore: Item
    val combinedMagicAlloys: Item
    val demonicCore: Item
    val diamondPlate: Item
    val emeraldPlate: Item
    val enderScaleAlloy: Item
    val forcefieldCore: Item
    val forcefieldPlationg: Item
    val fortifiedFrame: Item
    val magicFortifiedPlating: Item
    val magicPlate: Item
    val magicPurifiedBlazeAlloy: Item
    val magicPurifiedEnderScaleAlloy: Item
    val magicPurifiedGlassAlloy: Item
    val obsidianPlate: Item
    val paleCore: Item
    val palePlate: Item
    val particleCatcher: Item
    val particleEmitter: Item
    val sunImbuedGlass: Item
    val voidPlating: Item
    val mithrilineIngot: Item
    val magicalIngot: Item
    val mithrilineDust: Item
    val heatingRod: Item
    val mithrilineCrystalGem: Item
    val mruResonatingCrystal: Item
    val fadingCrystal: Item

    companion object {
        lateinit var instance: ItemRegistry
    }
}
