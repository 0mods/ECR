package team._0mods.ecr.common.items

import net.minecraft.tags.TagKey
import net.minecraft.world.item.Tier
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.block.Block

enum class ECToolMaterials(
    private val maxUses: Int,
    private val maxSpeed: Float,
    private val attackDamage: Float,
    private val harvestLevel: Int,
    private val enchantValue: Int,
    private val ingredient: () -> Ingredient,
    private val tagKey: TagKey<Block>? = null
): Tier {
    WEAK(754, 2.5f, 7f, 3, 36, { Ingredient.EMPTY }),
    ELEMENTAL(3568, 5f, 15f, 6, 36, { Ingredient.EMPTY });

    override fun getUses(): Int = maxUses

    override fun getSpeed(): Float = maxSpeed

    override fun getAttackDamageBonus(): Float = attackDamage

    @Deprecated("Deprecated in Java")
    override fun getLevel(): Int = harvestLevel

    override fun getEnchantmentValue(): Int = enchantValue

    override fun getRepairIngredient(): Ingredient = ingredient()

    override fun getTag(): TagKey<Block>? = tagKey
}