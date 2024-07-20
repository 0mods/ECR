package team._0mods.ecr

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import team._0mods.ecr.common.init.ECRegistry

const val ModId = "ecremained"

@Mod(ModId)
class ECRemained {
    init {
        val modBus = FMLJavaModLoadingContext.get().modEventBus
        val forgeBus = MinecraftForge.EVENT_BUS
        ECRegistry.init(modBus)
    }
}
