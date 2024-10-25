@file:JvmName("ECManager")

package team._0mods.ecr.common.init

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext

@JvmName("init")
fun initCommon(ctx: FMLJavaModLoadingContext) {
    val modBus = ctx.modEventBus
    val forgeBus = MinecraftForge.EVENT_BUS
}
