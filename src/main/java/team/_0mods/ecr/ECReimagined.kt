package team._0mods.ecr

import net.minecraftforge.fml.common.Mod
import org.slf4j.LoggerFactory
import team._0mods.ecr.common.init.initCommon

val LOGGER = LoggerFactory.getLogger("ECR")
const val ModId = "ecreimagined"
const val SHORT_ID = "ecr"

@Mod(ModId)
class ECRemained {
    init {

        initCommon()

        //HollowSoundHandler.MODS.add(ModId)
    }
}
