package team._0mods.ecr.common.items.tools

import net.minecraft.world.item.*
import team._0mods.ecr.api.mru.MRUMultiplierWeapon
import team._0mods.ecr.common.items.ECToolMaterials

class WeakAxe: AxeItem(
    ECToolMaterials.WEAK,
    5f,
    -3.2f,
    Properties()
)

class WeakHoe: HoeItem(
    ECToolMaterials.WEAK,
    -6,
    -2f,
    Properties()
)

class WeakPickaxe: PickaxeItem(
    ECToolMaterials.WEAK,
    -3,
    -2.8f,
    Properties()
)

class WeakShovel: ShovelItem(
    ECToolMaterials.WEAK,
    -2.5f,
    -3f,
    Properties()
)

class WeakSword : SwordItem(
    ECToolMaterials.WEAK,
    -1,
    -2.4f,
    Properties()
), MRUMultiplierWeapon {
    override val multiplier: Float
        get() = 1.2f
}
