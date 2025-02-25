package team._0mods.ecr

import net.minecraftforge.fml.common.Mod
import ru.hollowhorizon.hc.client.utils.isPhysicalClient
import team._0mods.ecr.api.ModId
import team._0mods.ecr.common.init.initClient
import team._0mods.ecr.common.init.initCommon

@Mod(ModId)
class ECReimagined {
    init {
        initCommon()

        if (isPhysicalClient)
            initClient()
    }
}
