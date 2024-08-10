package team._0mods.ecr.common.compact.jade

import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin
import team._0mods.ecr.common.blocks.MithrilineFurnace
import team._0mods.ecr.common.compact.jade.components.MithrilineFurnaceComponent

@WailaPlugin
class ECJadePlugin: IWailaPlugin {
    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerBlockComponent(MithrilineFurnaceComponent(), MithrilineFurnace::class.java)
    }
}