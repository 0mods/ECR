package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.menu.MenuTypeData
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.MenuTypeRegistry
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeMenuTypeRegistry(bus: IEventBus): MenuTypeRegistry {
    private val menuType = DeferredRegister.create(BuiltInRegistries.MENU, ModId)

    init {
        menuType.register(bus)
    }

    private val mithrilineFurnaceMenu = menuType.register(ECRModIDs.MITHRILINE_FURNACE) { _ -> createDefaulted(::MithrilineFurnaceMenu) }

    override val mithrilineFurnace: MenuType<MithrilineFurnaceMenu> by lazy { mithrilineFurnaceMenu.get() }

    private fun <T: AbstractContainerMenu> createDefaulted(
        factory: (Int, Inventory, MenuTypeData) -> T
    ) = createMenu(MenuTypeData.codec, factory)

    private fun <T: AbstractContainerMenu, D : Any> createMenu(
        codec: StreamCodec<RegistryFriendlyByteBuf, D>,
        factory: (Int, Inventory, D) -> T
    ): MenuType<T> = IMenuTypeExtension.create { id, inv, buf ->
        val data = codec.decode(buf)
        factory(id, inv, data)
    }
}
