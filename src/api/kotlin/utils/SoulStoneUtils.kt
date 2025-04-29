package team._0mods.ecr.api.utils

import net.minecraft.world.item.ItemStack
import org.jetbrains.annotations.NotNull
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.item.SoulStoneLike
import team._0mods.ecr.api.mru.PlayerMatrixType
import team._0mods.ecr.api.registries.ECRegistries
import java.util.*
import kotlin.math.roundToInt

object SoulStoneUtils {
    @JvmStatic
    var ItemStack.owner: UUID?
        get() {
            if (this.item !is SoulStoneLike) return null
            val tag = this.orCreateTag

            if (tag.contains("SoulStoneOwner")) return tag.getUUID("SoulStoneOwner")
            return null
        }
        set(value) {
            if (this.item !is SoulStoneLike) return
            val tag = this.orCreateTag

            if (!tag.contains("SoulStoneOwner")) {
                if (value != null) {
                    tag.putUUID("SoulStoneOwner", value)
                    tag.putString("SoulStoneOwnerName", "")
                }
            } else {
                if (value == null) {
                    tag.remove("SoulStoneOwner")
                    tag.remove("SoulStoneCapacity")
                    tag.remove("SoulStoneOwnerName")
                }
            }
        }

    @JvmStatic
    @get:NotNull
    var ItemStack.ownerName: String?
        get() {
            if (this.item !is SoulStoneLike) return "Lol, why you try to load owner nick, if item is not soul stone? Bro, it is not work."
            val tag = this.orCreateTag

            if (tag.contains("SoulStoneOwnerName")) return tag.getString("SoulStoneOwnerName")

            return "Not Loaded"
        }
        set(value) {
            if (this.item !is SoulStoneLike) return
            val tag = this.orCreateTag

            if (value != null) {
                tag.putString("SoulStoneOwnerName", value)
            } else {
                tag.remove("SoulStoneOwnerName")
            }
        }

    var ItemStack.capacity: Int
        get() {
            if (this.item !is SoulStoneLike) return 0
            val tag = this.orCreateTag
            this.owner ?: tag.remove("SoulStoneCapacity")

            if (tag.contains("SoulStoneCapacity")) return tag.getInt("SoulStoneCapacity")

            return 0
        }
        set(value) {
            if (this.item !is SoulStoneLike) return
            val tag = this.orCreateTag

            if (this.owner != null) tag.putInt("SoulStoneCapacity", value)
        }

    var ItemStack.matrix: PlayerMatrixType?
        get() {
            if (this.item !is SoulStoneLike) return null
            val tag = this.orCreateTag

            if (tag.contains("PlayerMatrixType")) {
                val matrixId = tag.getString("PlayerMatrixType").rl
                if (ECRegistries.PLAYER_MATRICES.isPresent(matrixId)) return ECRegistries.PLAYER_MATRICES.getValue(matrixId)
                else tag.remove("PlaterMatrixType")
            }

            return null
        }
        set(value) {
            if (this.item !is SoulStoneLike) return
            val tag = this.orCreateTag

            if (value != null) {
                ECRegistries.PLAYER_MATRICES.getKey(value)?.let { tag.putString("PlayerMatrixType", it.toString()) }
            } else if (tag.contains("PlayerMatrixType")) tag.remove("PlayerMatrixType")
        }

    var ItemStack.isCreative: Boolean
        get() {
            if (this.item !is SoulStoneLike) return false
            val tag = this.orCreateTag

            if (tag.contains("HasCreativeAbilities")) return tag.getBoolean("HasCreativeAbilities")

            return false
        }
        set(value) {
            if (this.item !is SoulStoneLike) return
            val tag = this.orCreateTag

            tag.putBoolean("HasCreativeAbilities", value)
        }

    fun ItemStack.addUBMRU(count: Float) = this.addUBMRU(count.toDouble())

    fun ItemStack.addUBMRU(count: Double) {
        if (this.item !is SoulStoneLike) return

        this.owner ?: return

        val conv = count.roundToInt()
        if (this.owner != null) this.capacity += conv
    }

    fun ItemStack.consumeUBMRU(count: Int) {
        this.owner ?: return
        if (this.isCreative) return

        if (this.capacity - count > 0) this.capacity -= count
        else this.capacity = 0
    }
}