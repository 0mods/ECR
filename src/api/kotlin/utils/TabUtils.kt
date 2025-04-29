package team._0mods.ecr.api.utils

//? if fabric
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
//? if forge
/*import net.minecraft.world.item.CreativeModeTab*/

val creativeTabBuilder =
    //? if fabric
    FabricItemGroup.builder()
    //? if forge
    /*CreativeModeTab.builder()*/