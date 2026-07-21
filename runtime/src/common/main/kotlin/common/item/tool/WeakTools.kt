package com.algorithmlx.ecr.common.item.tool

import com.algorithmlx.ecr.api.mru.MRUMultiplierWeapon
import com.algorithmlx.ecr.common.item.material.ECToolMaterials
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.Item
import net.minecraft.world.item.ShovelItem

class WeakAxe(properties: Properties): AxeItem(ECToolMaterials.WEAK.material, 5F, -3.2F, properties)
class WeakHoe(properties: Properties): HoeItem(ECToolMaterials.WEAK.material, -6F, 2F, properties)
class WeakPickaxe(properties: Properties): Item(properties.pickaxe(ECToolMaterials.WEAK.material, -3F, -2.8F))
class WeakShovel(properties: Properties): ShovelItem(ECToolMaterials.WEAK.material, -2.5F, -3f, properties)
class WeakSword(properties: Properties): Item(properties.sword(ECToolMaterials.WEAK.material, -1F, -2.4F)), MRUMultiplierWeapon {
    override val multiplier: Float = 1.2f
}
