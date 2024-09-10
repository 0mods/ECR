@file:JvmName("ECManager")

package team._0mods.ecr.common.init

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import team._0mods.ecr.api.multiblock.IMultiblock
import team._0mods.ecr.api.utils.ecRL
import team._0mods.ecr.common.init.registry.ECAnnotationProcessor

@JvmName("init")
fun initCommon() {
    val modBus = FMLJavaModLoadingContext.get().modEventBus
    val forgeBus = MinecraftForge.EVENT_BUS

    IMultiblock.createMultiBlock("nil".ecRL, arrayOf(arrayOf()), false)

    ECAnnotationProcessor.init()
}
