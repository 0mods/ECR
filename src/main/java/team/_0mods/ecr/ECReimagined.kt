package team._0mods.ecr

import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import team._0mods.ecr.api.ModId
import team._0mods.ecr.common.init.initCommon

@Mod(ModId)
class ECReimagined(ctx: FMLJavaModLoadingContext) {
    @Deprecated("Removed in 1.21")
    @Suppress("REMOVAL", "DEPRECATION")
    constructor(): this(FMLJavaModLoadingContext.get())

    init {
        initCommon(ctx)
    }
}
