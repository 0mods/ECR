@file:JvmName("ECManager")

package team._0mods.ecr.common.init

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import ru.hollowhorizon.hc.client.sounds.HollowSoundHandler
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.common.init.config.ECCommonConfig

@JvmName("init")
fun initCommon(ctx: FMLJavaModLoadingContext) {
    val modBus = ctx.modEventBus
    val forgeBus = MinecraftForge.EVENT_BUS
    ECCommonConfig.instance = ECCommonConfig().loadConfig("essential-craft/common")
    HollowSoundHandler.MODS.add(ModId)
}
