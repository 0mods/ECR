package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.api.menu.MenuTypeData
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.menu.EnrichmentChamberControllerMenu
import com.algorithmlx.ecr.common.menu.EnrichmentChamberReceiverMenu
import com.algorithmlx.ecr.common.menu.MagicTableMenu
import com.algorithmlx.ecr.common.menu.MatrixDestructorMenu
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import com.algorithmlx.ecr.registry.MenuTypeRegistry
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuType
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.flag.FeatureFlags
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType

object FabricMenuTypeRegistry : MenuTypeRegistry {
    override val mithrilineFurnace: MenuType<MithrilineFurnaceMenu> = register(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnaceMenu)
    override val magicTable: MenuType<MagicTableMenu> = register(ECRModIDs.MAGIC_TABLE, ::MagicTableMenu)
    override val matrixDestructor: MenuType<MatrixDestructorMenu> = register(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructorMenu)
    override val enrichmentChamberController: MenuType<EnrichmentChamberControllerMenu> = register(ECRModIDs.ENRICHMENT_CHAMBER_CONTROLLER, ::EnrichmentChamberControllerMenu)
    override val enrichmentChamberReceiver: MenuType<EnrichmentChamberReceiverMenu> =
        register(ECRModIDs.ENRICHMENT_CHAMBER_RECEIVER, ::EnrichmentChamberReceiverMenu)

    private fun <T: AbstractContainerMenu> register(id: String, menu: (Int, Inventory) -> T) =
        register(id, createDefaulted(menu))

    private fun <T: AbstractContainerMenu> register(id: String, menu: (Int, Inventory, MenuTypeData) -> T) =
        register(id, createTyped(menu))

    private fun <T: MenuType<*>> register(id: String, menu: T): T = Registry.register(BuiltInRegistries.MENU, id.ecRL, menu)

    private fun <T: AbstractContainerMenu> createDefaulted(factory: (Int, Inventory) -> T) =
        MenuType(factory, FeatureFlags.VANILLA_SET)

    private fun <T: AbstractContainerMenu> createTyped(
        factory: (Int, Inventory, MenuTypeData) -> T
    ) = createMenu(MenuTypeData.codec, factory)

    private fun <T: AbstractContainerMenu, D : Any> createMenu(
        codec: StreamCodec<RegistryFriendlyByteBuf, D>,
        factory: (Int, Inventory, D) -> T
    ): MenuType<T> = ExtendedMenuType(factory, codec)
}
