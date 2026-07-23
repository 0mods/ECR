package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.*
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.item.NamedBlockItem
import com.algorithmlx.ecr.registry.BlockRegistry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockRegistry(bus: IEventBus) : BlockRegistry {
    private val blockItems = DeferredRegister.createItems(ModId)
    private val blocks = DeferredRegister.createBlocks(ModId)

    init{
        blocks.register(bus)
        blockItems.register(bus)
    }

    private val mithrilineFurnaceBlock = registerBlock(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnace)
    private val mithrilineCrystalBlock = registerBlock(ECRModIDs.MITHRILINE_CRYSTAL, ::CrystalBlock)
    private val magicTableBlock = registerBlock(ECRModIDs.MAGIC_TABLE, ::MagicTable)
    private val magicalTeleporterBlock = registerBlock(ECRModIDs.MAGICAL_TELEPORTER, ::MagicalTeleporter)
    private val matrixDestructorBlock = registerBlock(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructor)
    private val solarPrismBlock = registerBlock(ECRModIDs.SOLAR_PRISM, ::SolarPrism)
    private val coldDistillerBlock = registerBlock(ECRModIDs.COLD_DISTILLER, ::ColdDistiller)
    private val voidStoneBlock = registerBasic(ECRModIDs.VOID_STONE)
    private val mithrilinePlatingBlock = registerBasic(ECRModIDs.MITHRILINE_PLATING)
    private val paleBlock = registerBasic(ECRModIDs.PALE_BLOCK)
    private val palePlatingBlock = registerBasic(ECRModIDs.PALE_PLATING)
    private val magicPlatingBlock = registerBasic(ECRModIDs.MAGIC_PLATING)
    private val demonicPlatingBlock = registerBasic(ECRModIDs.DEMONIC_PLATING)
    private val flameClusterBlock = registerBasic(ECRModIDs.FLAME_CLUSTER, shouldRegisterItem = false)
    private val waterClusterBlock = registerBasic(ECRModIDs.WATER_CLUSTER, shouldRegisterItem = false)
    private val earthClusterBlock = registerBasic(ECRModIDs.EARTH_CLUSTER, shouldRegisterItem = false)
    private val airClusterBlock = registerBasic(ECRModIDs.AIR_CLUSTER, shouldRegisterItem = false)

    override val mithrilineFurnace: MithrilineFurnace by lazy { mithrilineFurnaceBlock.get() }
    override val mithrilineCrystal: CrystalBlock by lazy { mithrilineCrystalBlock.get() }
    override val magicTable: MagicTable by lazy { magicTableBlock.get() }
    override val magicalTeleporter: MagicalTeleporter by lazy { magicalTeleporterBlock.get() }
    override val matrixDestructor: MatrixDestructor by lazy { matrixDestructorBlock.get() }
    override val solarPrism: SolarPrism by lazy { solarPrismBlock.get() }
    override val coldDistiller: ColdDistiller by lazy { coldDistillerBlock.get() }
    override val voidStone: Block by lazy { voidStoneBlock.get() }
    override val mithrilinePlating: Block by lazy { mithrilinePlatingBlock.get() }
    override val pale: Block by lazy { paleBlock.get() }
    override val palePlating: Block by lazy { palePlatingBlock.get() }
    override val magicPlating: Block by lazy { magicPlatingBlock.get() }
    override val demonicPlating: Block by lazy { demonicPlatingBlock.get() }
    override val flameCluster: Block by lazy { flameClusterBlock.get() }
    override val waterCluster: Block by lazy { waterClusterBlock.get() }
    override val earthCluster: Block by lazy { earthClusterBlock.get() }
    override val airCluster: Block by lazy { airClusterBlock.get() }

    private fun registerBasic(
        id: String,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true
    ) = registerBlock(id, ::Block, properties, shouldRegisterItem)

    private fun <B: Block> registerBlock(
        id: String,
        block: (BlockBehaviour.Properties) -> B,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true
    ): DeferredBlock<B> {
        val blockKey = { it: Identifier -> ResourceKey.create(Registries.BLOCK, it) }
        val bl = blocks.register(id) { rk ->
            block(properties.setId(blockKey(rk)))
        }

        if (shouldRegisterItem) {
            blockItems.register(id) { rk ->
                NamedBlockItem(
                    bl.get(),
                    Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, rk))
                        .useBlockDescriptionPrefix()
                )
            }
        }

        return bl
    }
}
