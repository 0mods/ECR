package team._0mods.ecr.common.compact

import mezz.jei.api.IModPlugin
import mezz.jei.api.JeiPlugin
import mezz.jei.api.registration.IAdvancedRegistration
import mezz.jei.api.registration.IGuiHandlerRegistration
import mezz.jei.api.registration.IRecipeCatalystRegistration
import net.minecraft.resources.ResourceLocation
import team._0mods.ecr.ModId
import team._0mods.ecr.common.rl

@JeiPlugin
class ECJEIPlugin: IModPlugin {
    override fun getPluginUid(): ResourceLocation = "$ModId:jei_plugin".rl

    override fun registerGuiHandlers(registration: IGuiHandlerRegistration) {}

    override fun registerRecipeCatalysts(registration: IRecipeCatalystRegistration) {}

    override fun registerAdvanced(registration: IAdvancedRegistration) {}
}