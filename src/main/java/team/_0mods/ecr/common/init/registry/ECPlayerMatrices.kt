package team._0mods.ecr.common.init.registry

import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import ru.hollowhorizon.hc.client.utils.colored
import ru.hollowhorizon.hc.client.utils.mcTranslate
import team._0mods.ecr.ModId
import team._0mods.ecr.api.mru.PlayerMatrixType

enum class ECPlayerMatrices(
    override val displayName: Component,
    override val reduceRadiationMultiplier: Double = 0.0,
    override val protectMatrixDecay: Boolean = false
): PlayerMatrixType {
    NEUTRAL("matrix.$ModId.neutral".mcTranslate.colored(ChatFormatting.GREEN));
}