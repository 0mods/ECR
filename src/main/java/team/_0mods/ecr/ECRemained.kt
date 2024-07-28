package team._0mods.ecr

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.fml.loading.FMLEnvironment
import org.slf4j.LoggerFactory
import team._0mods.ecr.common.init.registry.ECRegistry

val LOGGER = LoggerFactory.getLogger("ECR")
const val ModId = "ecremained"

val ECCoroutine = CoroutineScope(Dispatchers.Default)

@Mod(ModId)
class ECRemained {
    init {
        val modBus = FMLJavaModLoadingContext.get().modEventBus
        val forgeBus = MinecraftForge.EVENT_BUS

        ECRegistry.init(modBus)
    }
}
