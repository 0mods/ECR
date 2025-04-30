package team._0mods.ecr.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.core.Holder
import net.minecraft.tags.TagKey
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import ru.hollowhorizon.hc.common.utils.get
import team._0mods.ecr.api.utils.checkAndOpenMenu
import team._0mods.ecr.api.block.client.LowSizeBreakParticle
import team._0mods.ecr.api.utils.prepareDrops
import team._0mods.ecr.api.utils.simpleTicker
import team._0mods.ecr.common.api.PropertiedEntityBlock
import team._0mods.ecr.common.blocks.entity.XLikeBlockEntity
import team._0mods.ecr.common.init.registry.ECRRegistry
import java.util.function.Predicate

class MagicTable(properties: Properties): PropertiedEntityBlock(properties), LowSizeBreakParticle {
    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = XLikeBlockEntity.MagicTable(pos, state)

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> = simpleTicker(XLikeBlockEntity.MagicTable::onTick)

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        val defaultResult = checkAndOpenMenu<XLikeBlockEntity.MagicTable>(player, level, pos)
        val item = player.getItemInHand(hand)
        val blockEntity = level.getBlockEntity(pos, ECRRegistry.magicTableEntity).orElse(null) ?: return defaultResult

        val inv = blockEntity[XLikeBlockEntity.ItemContainer::class].items
        val slotItem = inv.getItem(7)

        return when {
            increaseItems.any { it.matcher(item) } -> {
                if (!slotItem.isEmpty) return defaultResult
                if (!level.isClientSide) {
                    inv.setItem(7, item)
                    item.shrink(1)
                }
                InteractionResult.SUCCESS
            }

            player.isShiftKeyDown -> {
                if (slotItem.isEmpty) return InteractionResult.CONSUME
                if (!level.isClientSide) {
                    val ie = ItemEntity(level, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, slotItem).apply {
                        setNoPickUpDelay()
                        setThrower(player.uuid)
                    }
                    slotItem.shrink(1)
                    level.addFreshEntity(ie)
                }
                InteractionResult.SUCCESS
            }

            else -> defaultResult
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        prepareDrops<XLikeBlockEntity.MagicTable>({ it[XLikeBlockEntity.ItemContainer::class].items },state, level, pos, newState)

        super.onRemove(state, level, pos, newState, isMoving)
    }

    companion object {
        @JvmStatic
        val increaseItems = mutableListOf<IncreaseItemData>()
    }

    class IncreaseItemData(
        val increaseCount: Double = 0.0,
        val mruCount: Double = 0.0,
        @get:JvmName("matches")
        val matcher: (ItemStack) -> Boolean,
    ) {
        companion object {
            fun empty() = IncreaseItemData { false }

            fun tag(tag: TagKey<Item>, increaseCount: Double = 0.0, mruCount: Double = 0.0) = IncreaseItemData(increaseCount, mruCount) { it.`is`(tag) }

            fun item(item: Item, increaseCount: Double = 0.0, mruCount: Double = 0.0) = IncreaseItemData(increaseCount, mruCount) { it.`is`(item) }

            fun item(item: Holder<Item>, increaseCount: Double = 0.0, mruCount: Double = 0.0) = IncreaseItemData(increaseCount, mruCount) { it.`is`(item) }

            fun item(item: Predicate<Holder<Item>>, increaseCount: Double = 0.0, mruCount: Double = 0.0) = IncreaseItemData(increaseCount, mruCount) { it.`is`(item) }
        }
    }
}