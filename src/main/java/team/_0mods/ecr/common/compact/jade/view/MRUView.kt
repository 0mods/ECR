package team._0mods.ecr.common.compact.jade.view

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import snownee.jade.api.ui.IDisplayHelper

class MRUView {
    var current: String = ""
    var max: String = ""
    var ratio: Float = 0F
    var overrideText: Component? = null

    companion object {
        @JvmStatic
        fun read(tag: CompoundTag, unit: String): MRUView? {
            val cap = tag.getLong("Capacity")

            return if (cap <= 0L) null
            else {
                val cur = tag.getLong("Cur")
                val view = MRUView().apply {
                    current = IDisplayHelper.get().humanReadableNumber(cur.toDouble(), unit, false)
                    max = IDisplayHelper.get().humanReadableNumber(cap.toDouble(), unit, false)
                    ratio = cur.toFloat() / cap.toFloat()
                }
                return view
            }
        }

        @JvmStatic
        fun of(current: Long, cap: Long) = CompoundTag().apply {
            putLong("Capacity", current)
            putLong("Cur", current)
        }
    }
}