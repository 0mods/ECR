package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.api.menu.MenuTypeData
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.menu.EnrichmentChamberControllerMenu
import com.algorithmlx.ecr.common.menu.EnrichmentChamberReceiverMenu
import com.algorithmlx.ecr.common.menu.HeatGeneratorMenu
import com.algorithmlx.ecr.common.menu.MagicTableMenu
import com.algorithmlx.ecr.common.menu.MagicalTeleporterMenu
import com.algorithmlx.ecr.common.menu.MatrixDestructorMenu
import com.algorithmlx.ecr.common.menu.MithrilineFurnaceMenu
import com.algorithmlx.ecr.common.menu.RayTowerMenu
import com.algorithmlx.ecr.registry.MenuTypeRegistry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.flag.FeatureFlags
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

    override val mithrilineFurnace: MenuType<MithrilineFurnaceMenu> by register(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnaceMenu)
    override val heatGenerator: MenuType<HeatGeneratorMenu> by register(ECRModIDs.HEAT_GENERATOR, ::HeatGeneratorMenu)
    override val magicTable: MenuType<MagicTableMenu> by register(ECRModIDs.MAGIC_TABLE, ::MagicTableMenu)
    override val matrixDestructor: MenuType<MatrixDestructorMenu> by register(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructorMenu)
    override val enrichmentChamberController: MenuType<EnrichmentChamberControllerMenu> by register(ECRModIDs.ENRICHMENT_CHAMBER_CONTROLLER, ::EnrichmentChamberControllerMenu)
    override val enrichmentChamberReceiver: MenuType<EnrichmentChamberReceiverMenu> by register(ECRModIDs.ENRICHMENT_CHAMBER_RECEIVER, ::EnrichmentChamberReceiverMenu)
    override val rayTower: MenuType<RayTowerMenu> by register(ECRModIDs.RAY_TOWER, ::RayTowerMenu)
    override val magicalTeleporter: MenuType<MagicalTeleporterMenu> by register(ECRModIDs.MAGICAL_TELEPORTER, ::MagicalTeleporterMenu)

    private fun <T: AbstractContainerMenu> register(id: String, factory: (Int, Inventory, MenuTypeData) -> T) = menuType.register(id) { _ -> createMenu(MenuTypeData.codec, factory) }

    private fun <T: AbstractContainerMenu> register(id: String, factory: (Int, Inventory) -> T) = menuType.register(id) { _ -> MenuType(factory, FeatureFlags.VANILLA_SET) }

    private fun <T: AbstractContainerMenu, D: Any> createMenu(codec: StreamCodec<RegistryFriendlyByteBuf, D>, factory: (Int, Inventory, D) -> T): MenuType<T> = IMenuTypeExtension.create { id, inv, buf ->
        val data = codec.decode(buf)
        factory(id, inv, data)
    }
}
