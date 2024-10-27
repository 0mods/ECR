package team._0mods.ecr.common.compact.jade

import net.minecraft.world.level.block.Block
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin
import team._0mods.ecr.api.ModId
import team._0mods.ecr.common.compact.jade.components.MRUComponent

@WailaPlugin
class ECJadePlugin: IWailaPlugin {
    companion object {
        val String.withJade: String
            get() = "config.jade.plugin_$ModId.$this"
    }

    override fun registerClient(registration: IWailaClientRegistration) {
//        registration.registerBlockComponent(MithrilineFurnaceComponent(), MithrilineFurnace::class.java)
        registration.registerBlockComponent(MRUComponent(), Block::class.java)
    }
}