package team._0mods.ecr.api.utils

import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.common.utils.rl
import team._0mods.ecr.api.ModId

val String.ecRL: ResourceLocation
    get() = "$ModId:$this".rl

val Int.toTag: IntTag
    get() = IntTag.valueOf(this)

val String.toTag: StringTag
    get() = StringTag.valueOf(this)

val Double.toTag: DoubleTag
    get() = DoubleTag.valueOf(this)

val Float.toTag: FloatTag
    get() = FloatTag.valueOf(this)

val Byte.toTag: ByteTag
    get() = ByteTag.valueOf(this)

val Boolean.toTag: ByteTag
    get() = ByteTag.valueOf(this)

val Long.toTag: LongTag
    get() = LongTag.valueOf(this)

val Short.toTag: ShortTag
    get() = ShortTag.valueOf(this)
