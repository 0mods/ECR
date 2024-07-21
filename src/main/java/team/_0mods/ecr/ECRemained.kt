package team._0mods.ecr

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment
import org.slf4j.LoggerFactory
import team._0mods.ecr.common.init.events.ForgeCommonEvents
import team._0mods.ecr.common.init.events.ModCommonEvents
import team._0mods.ecr.common.init.events.client.ForgeClientEvents
import team._0mods.ecr.common.init.events.client.ModClientEvents
import team._0mods.ecr.common.init.registry.ECRegistry

val LOGGER = LoggerFactory.getLogger("ECR")
const val ModId = "ecremained"

@Mod(ModId)
class ECRemained {
    init {
        val modBus = FMLJavaModLoadingContext.get().modEventBus
        val forgeBus = MinecraftForge.EVENT_BUS

        forgeBus.register(ForgeCommonEvents())
        modBus.register(ModCommonEvents())

        ECRegistry.init(modBus)

        if (FMLEnvironment.dist.isClient) {
            modBus.register(ModClientEvents())
            forgeBus.register(ForgeClientEvents())
        }
    }
}
