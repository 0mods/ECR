@file:JvmName("ECManager")

package team._0mods.ecr.common.init

import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import team._0mods.ecr.common.init.events.onCapabilityPlayerAttach
import team._0mods.ecr.common.init.registry.ECAnnotationProcessor
import team._0mods.ecr.network.ECNetworkManager

@JvmName("init")
fun initCommon() {
    val modBus = FMLJavaModLoadingContext.get().modEventBus
    val forgeBus = MinecraftForge.EVENT_BUS

    ECAnnotationProcessor.init()

    ECNetworkManager.init()

    forgeBus.addGenericListener(Player::class.java, ::onCapabilityPlayerAttach)
}
