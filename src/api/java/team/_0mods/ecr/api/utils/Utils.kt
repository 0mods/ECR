package team._0mods.ecr.api.utils

import net.minecraft.resources.ResourceLocation
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.api.ModId

val String.ecRL: ResourceLocation
    get() = "$ModId:$this".rl
