package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.ecRL
import com.algorithmlx.ecr.api.menu.MenuTypeData
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.MenuTypeRegistry
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

object FabricMenuTypeRegistry: MenuTypeRegistry {
    override val mithrilineFurnace: MenuType<MithrilineFurnaceMenu> = register(ECRModIDs.MITHRILINE_FURNACE, createDefaulted(::MithrilineFurnaceMenu))

    private fun <T: MenuType<*>> register(id: String, menu: T): T = Registry.register(BuiltInRegistries.MENU, id.ecRL, menu)

    private fun <T: AbstractContainerMenu> createDefaulted(
        factory: (Int, Inventory, MenuTypeData) -> T
    ) = createMenu(MenuTypeData.codec, factory)

    private fun <T: AbstractContainerMenu, D : Any> createMenu(
        codec: StreamCodec<RegistryFriendlyByteBuf, D>,
        factory: (Int, Inventory, D) -> T
    ): MenuType<T> = ExtendedMenuType(factory, codec)
}
