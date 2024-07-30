package team._0mods.ecr

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraftforge.fml.common.Mod
import org.slf4j.LoggerFactory
import team._0mods.ecr.common.init.initCommon

val LOGGER = LoggerFactory.getLogger("ECR")
const val ModId = "ecremained"

val ECCoroutine = CoroutineScope(Dispatchers.Default)

@Mod(ModId)
class ECRemained {
    init {
        initCommon()
    }
}
