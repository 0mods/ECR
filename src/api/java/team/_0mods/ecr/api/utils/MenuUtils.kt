package team._0mods.ecr.api.utils

//? if fabric
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
//? if forge
/*import net.minecraftforge.common.extensions.IForgeMenuType*/

inline fun <reified T: AbstractContainerMenu> simpleMenuFactory(crossinline factory: (Int, Inventory, FriendlyByteBuf) -> T): MenuType<T> =
    //? if forge {
    /*IForgeMenuType.create { i, inv, b -> factory(i, inv, b) }
    *///?} elif fabric {
    ExtendedScreenHandlerType { i, inv, b -> factory(i, inv, b) }
    //?}
