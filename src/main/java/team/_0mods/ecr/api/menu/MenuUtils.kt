package team._0mods.ecr.api.menu

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraftforge.network.IContainerFactory

inline fun <reified T: AbstractContainerMenu> simpleMenuFactory(crossinline factory: (Int, Inventory, FriendlyByteBuf) -> T): MenuType<T> =
    MenuType(IContainerFactory {i, inv, fb -> factory(i, inv, fb) })