@file:JvmName("ECManager")

package team._0mods.ecr.common.init

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import team._0mods.ecr.common.init.registry.ECAnnotationProcessor
import team._0mods.ecr.common.init.registry.ECMultiblocks

@JvmName("init")
fun initCommon() {
    val modBus = FMLJavaModLoadingContext.get().modEventBus
    val forgeBus = MinecraftForge.EVENT_BUS

    ECMultiblocks.init()

    ECAnnotationProcessor.init()
}
