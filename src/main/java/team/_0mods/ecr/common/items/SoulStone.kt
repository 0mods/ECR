package team._0mods.ecr.common.items

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.SwordItem
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.living.LivingDeathEvent
import net.minecraftforge.server.ServerLifecycleHooks
import ru.hollowhorizon.hc.client.utils.get
import ru.hollowhorizon.hc.client.utils.literal
import ru.hollowhorizon.hc.client.utils.mcTranslate
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.ModId
import team._0mods.ecr.api.mru.MRUMultiplierWeapon
import team._0mods.ecr.api.mru.PlayerMatrixType
import team._0mods.ecr.api.registries.ECRegistries
import team._0mods.ecr.common.capability.PlayerMRU
import team._0mods.ecr.common.init.registry.ECTabs
import java.util.*
import kotlin.math.roundToInt

class SoulStone: Item(Properties().tab(ECTabs.tabItems)) {
    companion object {
        @get:JvmStatic
        val entityCapacityAdd = mutableMapOf<EntityType<*>, IntRange>()
        lateinit var defaultCapacityAdd: IntRange
        lateinit var defaultEnemyAdd: IntRange
    }

    @get:JvmName("_getOwner")
    @set:JvmName("_setOwner")
    private var ItemStack.owner
        get() = getOwner(this)
        set(value) = setOwner(this, value)

    init {
        MinecraftForge.EVENT_BUS.addListener(this::onEntityKill)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (!level.isClientSide) {
            if (!player.isShiftKeyDown) {
                if (stack.owner == null) {
                    if (stack.count > 1) {
                        val copiedStack = stack.copy().apply {
                            count = 1
                            owner = player.uuid
                        }
                        stack.shrink(1)

                        val ent = ItemEntity(level, player.x, player.y, player.z, copiedStack).apply {
                            setNoPickUpDelay()
                            this.owner = player.uuid
                        }

                        level.addFreshEntity(ent)
                    } else {
                        stack.owner = player.uuid
                    }

                    player.displayClientMessage(
                        Component.translatable("tooltip.$ModId.soul_stone.bounded", player.name),
                        true
                    )
                    return InteractionResultHolder.success(stack)
                }
            } else {
                if (stack.owner != null) {
                    if (stack.owner != player.uuid) {
                        player.displayClientMessage(
                            Component.translatable("tooltip.$ModId.soul_stone.can_not_unbound"),
                            true
                        )
                        return InteractionResultHolder.fail(stack)
                    } else {
                        stack.owner = null
                        setOwnerNick(stack, null)
                        player.displayClientMessage(Component.translatable("tooltip.$ModId.soul_stone.unbounded"), true)
                        return InteractionResultHolder.fail(stack)
                    }
                }
            }
        }

        return super.use(level, player, usedHand)
    }

    override fun getMaxStackSize(stack: ItemStack?): Int {
        if (stack != null && stack.hasTag()) return 1

        return super.getMaxStackSize(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        if (stack.owner != null) {
            tooltipComponents.add(
                "tooltip.$ModId.soul_stone.tracking".mcTranslate(
                    getOwnerNick(stack).literal.withStyle(if (getOwnerNick(stack) == "Not Loaded") ChatFormatting.RED else ChatFormatting.GOLD)
                ).withStyle(ChatFormatting.DARK_GRAY)
            )

            tooltipComponents.add(
                "tooltip.$ModId.soul_stone.detected_ubmru".mcTranslate(
                    this.getCapacity(stack).toString().literal.withStyle(ChatFormatting.GREEN)
                ).withStyle(ChatFormatting.DARK_GRAY)
            )

            this.getMatrix(stack)?.let {
                tooltipComponents.add("tooltip.$ModId.soul_stone.matrix".mcTranslate(it.displayName).withStyle(ChatFormatting.DARK_GRAY))
            }
        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (stack.item !is SoulStone) return
        if (!level.isClientSide) {
            val server = level.server!!
            val uuid = this.getOwner(stack)
            if (uuid != null) {
                val player = server.playerList.getPlayer(uuid)
                player?.let { this.setMatrix(stack, it[PlayerMRU::class].getMatrixType()) }
            } else if (this.getMatrix(stack) != null) {
                this.setMatrix(stack, null)
            }
        }
    }

    private fun onEntityKill(e: LivingDeathEvent) {
        val source = e.source.entity ?: return
        val ent = e.entity
        if (source !is Player) return

        val items = source.inventory.items.filter { it.item is SoulStone }

        if (items.isEmpty()) return

        val item = items.random()

        if (item.owner == null) return
        if (ent.isBaby && ent !is Enemy) return

        val weapon = source.getItemInHand(InteractionHand.MAIN_HAND).item
        val multiplier = if (weapon is MRUMultiplierWeapon && weapon is SwordItem) weapon.multiplier else 1f

        if (entityCapacityAdd.contains(ent.type)) {
            val a = entityCapacityAdd[ent.type]!!.random() * multiplier
            this.add(item, a)
        } else {
            if (ent is Enemy) {
                val a = defaultEnemyAdd.random() * multiplier
                this.add(item, a)
            } else {
                val a = defaultCapacityAdd.random() * multiplier
                this.add(item, a)
            }
        }
    }

    fun getOwner(stack: ItemStack): UUID? {
        if (stack.item !is SoulStone) return null
        val tag = stack.orCreateTag
        return if (tag.contains("SoulStoneOwner")) {
            try {
                tag.getUUID("SoulStoneOwner")
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun setOwner(stack: ItemStack, newOwner: UUID?) {
        if (stack.item !is SoulStone) return
        val tag = stack.orCreateTag

        if (!tag.contains("SoulStoneOwner")) {
            if (newOwner != null) {
                tag.putUUID("SoulStoneOwner", newOwner)
                ServerLifecycleHooks.getCurrentServer()?.let { l ->
                    l.playerList.getPlayer(newOwner)?.let {
                        tag.putString("SoulStoneOwnerName", it.name.string)
                    }
                }
            }
        } else {
            if (newOwner == null) {
                tag.remove("SoulStoneOwner")
                tag.remove("SoulStoneCapacity")
                tag.remove("SoulStoneOwnerName")
            }
        }
    }

    fun getOwnerNick(stack: ItemStack): String {
        if (stack.item !is SoulStone) return "Lol, why you try to load owner nick, if item is not soul stone? Bro, it is not work."
        val tag = stack.orCreateTag
        return if (tag.contains("SoulStoneOwnerName")) {
            try {
                tag.getString("SoulStoneOwnerName")
            } catch (_: Exception) {
                "Not Loaded"
            }
        } else "Not Loaded"
    }

    fun setOwnerNick(stack: ItemStack, name: String?) {
        if (stack.item !is SoulStone) return
        val tag = stack.orCreateTag

        if (name != null) {
            tag.putString("SoulStoneOwnerName", name)
        } else {
            tag.remove("SoulStoneOwnerName")
        }
    }

    fun getCapacity(stack: ItemStack): Int {
        if (stack.item !is SoulStone) return 0
        val tag = stack.orCreateTag
        if (stack.owner == null) {
            tag.remove("SoulStoneCapacity")
            return 0
        }

        return tag.getInt("SoulStoneCapacity")
    }

    fun setCapacity(stack: ItemStack, newCapacity: Int) {
        if (stack.item !is SoulStone) return
        val tag = stack.orCreateTag

        if (stack.owner != null) tag.putInt("SoulStoneCapacity", newCapacity)
    }

    fun add(stack: ItemStack, count: Float) {
        val conv = count.roundToInt()
        if (stack.owner != null) {
            val cap = getCapacity(stack)
            setCapacity(stack, cap + conv)
        }
    }

    fun add(stack: ItemStack, count: Double) {
        val conv = count.roundToInt()
        if (stack.owner != null) {
            val cap = getCapacity(stack)
            setCapacity(stack, cap + conv)
        }
    }

    fun remove(stack: ItemStack, count: Int) {
        val cap = getCapacity(stack)
        if (stack.owner != null) {
            if (cap - count > 0) {
                setCapacity(stack, cap - count)
            } else {
                setCapacity(stack, 0)
            }
        }
    }

    fun getMatrix(stack: ItemStack): PlayerMatrixType? {
        if (stack.item !is SoulStone) return null
        val tag = stack.orCreateTag

        return if (tag.contains("PlayerMatrixType")) {
            val matrixId = tag.getString("PlayerMatrixType").rl
            if (ECRegistries.PLAYER_MATRICES.isPresent(matrixId))
                ECRegistries.PLAYER_MATRICES.getValue(matrixId)
            else {
                tag.remove("PlayerMatrixType")
                null
            }
        } else null
    }

    fun setMatrix(stack: ItemStack, matrixType: PlayerMatrixType?) {
        if (stack.item !is SoulStone) return
        val tag = stack.orCreateTag

        if (matrixType != null) {
            ECRegistries.PLAYER_MATRICES.getKey(matrixType)?.let {
                tag.putString("PlayerMatrixType", it.toString())
            }
        } else {
            if (tag.contains("PlayerMatrixType"))
                tag.remove("PlayerMatrixType")
        }
    }
}
