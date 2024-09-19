package team._0mods.ecr.api.mru

/**
 * It is a marker for the game.
 * When inheriting the current class, the [net.minecraft.world.item.Item] will automatically spawn with the "MRU boost" property.
 *
 * # WARNING
 * **Can only be used with [net.minecraft.world.item.SwordItem]**
 */
interface MRUMultiplierWeapon {
    /**
     * Sets the value of the MRU loot multiplier.
     * @return [Float] of MRU multiplier
     */
    val multiplier: Float
}
