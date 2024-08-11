@file:Mod.EventBusSubscriber(modid = ModId, bus = Mod.EventBusSubscriber.Bus.MOD)

package team._0mods.ecr.common.init.events

import kotlinx.serialization.json.Json
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import team._0mods.ecr.ModId
import team._0mods.ecr.api.utils.loadConfig
import team._0mods.ecr.common.init.config.ECCommonConfig

@SubscribeEvent
fun onModSetup(e: FMLCommonSetupEvent) {
    val json = Json {
        encodeDefaults = true
        prettyPrint = true
        prettyPrintIndent = "  "
        allowComments = true
        allowTrailingComma = true
    }

    ECCommonConfig.instance = ECCommonConfig().loadConfig(json, "essential-craft/common")
}

