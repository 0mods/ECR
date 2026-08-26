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
import net.minecraft.world.level.block.TransparentBlock
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.PushReaction
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockRegistry(
    bus: IEventBus,
) : BlockRegistry {
    private val blockItems = DeferredRegister.createItems(ModId)
    private val blocks = DeferredRegister.createBlocks(ModId)

    init {
        blocks.register(bus)
        blockItems.register(bus)
    }

    private val assembledMultiblockPartBlock =
        registerBlock(
            ECRModIDs.ASSEMBLED_MULTIBLOCK_PART,
            ::AssembledMultiblockPartBlock,
            BlockBehaviour.Properties
                .of()
                .strength(3.0F)
                .noOcclusion()
                .noLootTable()
                .pushReaction(PushReaction.BLOCK),
            shouldRegisterItem = false,
        )
    private val mithrilineFurnaceBlock = registerBlock(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnace)
    private val mithrilineCrystalBlock = registerBlock(ECRModIDs.MITHRILINE_CRYSTAL, ::CrystalBlock)
    private val magicTableBlock = registerBlock(ECRModIDs.MAGIC_TABLE, ::MagicTable)
    private val magicalTeleporterBlock = registerBlock(ECRModIDs.MAGICAL_TELEPORTER, ::MagicalTeleporter)
    private val matrixDestructorBlock = registerBlock(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructor)
    private val solarPrismBlock = registerBlock(ECRModIDs.SOLAR_PRISM, ::SolarPrism)
    private val coldDistillerBlock = registerBlock(ECRModIDs.COLD_DISTILLER, ::ColdDistiller)
    private val heatGeneratorBlock = registerBlock(ECRModIDs.HEAT_GENERATOR, ::HeatGenerator)
    private val voidStoneBlock = registerBasic(ECRModIDs.VOID_STONE)
    private val mithrilinePlatingBlock = registerBasic(ECRModIDs.MITHRILINE_PLATING)
    private val paleBlock = registerBasic(ECRModIDs.PALE_BLOCK)
    private val palePlatingBlock = registerBasic(ECRModIDs.PALE_PLATING)
    private val magicPlatingBlock = registerBasic(ECRModIDs.MAGIC_PLATING)
    private val demonicPlatingBlock = registerBasic(ECRModIDs.DEMONIC_PLATING)
    private val fortifiedStoneBlock = registerBasic(ECRModIDs.FORTIFIED_STONE)
    private val flameClusterBlock = registerBlock(ECRModIDs.FLAME_CLUSTER, ::ClusterBlock, shouldRegisterItem = false)
    private val waterClusterBlock = registerBlock(ECRModIDs.WATER_CLUSTER, ::ClusterBlock, shouldRegisterItem = false)
    private val earthClusterBlock = registerBlock(ECRModIDs.EARTH_CLUSTER, ::ClusterBlock, shouldRegisterItem = false)
    private val airClusterBlock = registerBlock(ECRModIDs.AIR_CLUSTER, ::ClusterBlock, shouldRegisterItem = false)
    private val fortifiedGlassBlock =
        registerBlock(
            ECRModIDs.FORTIFIED_GLASS,
            ::TransparentBlock,
            BlockBehaviour.Properties.of().noOcclusion(),
        )
    private val enrichmentChamberHolderBlock = registerBasic(ECRModIDs.ENRICHMENT_CHAMBER_HOLDER)
    private val enrichmentChamberControllerBlock =
        registerBlock(
            ECRModIDs.ENRICHMENT_CHAMBER_CONTROLLER,
            ::EnrichmentChamberController,
        )
    private val enrichmentChamberExtractorBlock =
        registerBlock(
            ECRModIDs.ENRICHMENT_CHAMBER_EXTRACTOR,
            ::EnrichmentChamberExtractor,
        )
    private val enrichmentChamberReceiverBlock =
        registerBlock(
            ECRModIDs.ENRICHMENT_CHAMBER_RECEIVER,
            ::EnrichmentChamberReceiver,
        )
    private val rayTowerBaseBlock = registerBlock(ECRModIDs.RAY_TOWER_BASE, ::RayTowerBase)
    private val rayTowerBlock = registerBlock(ECRModIDs.RAY_TOWER, ::RayTower)
    private val creativeMRUSourceBlock = registerBlock(ECRModIDs.CREATIVE_MRU_SOURCE, ::CreativeMRUSource)

    override val assembledMultiblockPart: AssembledMultiblockPartBlock by lazy { assembledMultiblockPartBlock.get() }
    override val mithrilineFurnace: MithrilineFurnace by lazy { mithrilineFurnaceBlock.get() }
    override val mithrilineCrystal: CrystalBlock by lazy { mithrilineCrystalBlock.get() }
    override val magicTable: MagicTable by lazy { magicTableBlock.get() }
    override val magicalTeleporter: MagicalTeleporter by lazy { magicalTeleporterBlock.get() }
    override val matrixDestructor: MatrixDestructor by lazy { matrixDestructorBlock.get() }
    override val solarPrism: SolarPrism by lazy { solarPrismBlock.get() }
    override val coldDistiller: ColdDistiller by lazy { coldDistillerBlock.get() }
    override val heatGenerator: HeatGenerator by lazy { heatGeneratorBlock.get() }
    override val voidStone: Block by lazy { voidStoneBlock.get() }
    override val mithrilinePlating: Block by lazy { mithrilinePlatingBlock.get() }
    override val pale: Block by lazy { paleBlock.get() }
    override val palePlating: Block by lazy { palePlatingBlock.get() }
    override val magicPlating: Block by lazy { magicPlatingBlock.get() }
    override val demonicPlating: Block by lazy { demonicPlatingBlock.get() }
    override val fortifiedStone: Block by lazy { fortifiedStoneBlock.get() }
    override val flameCluster: ClusterBlock by lazy { flameClusterBlock.get() }
    override val waterCluster: ClusterBlock by lazy { waterClusterBlock.get() }
    override val earthCluster: ClusterBlock by lazy { earthClusterBlock.get() }
    override val airCluster: ClusterBlock by lazy { airClusterBlock.get() }
    override val fortifiedGlass: Block by lazy { fortifiedGlassBlock.get() }
    override val enrichmentChamberHolder: Block by lazy { enrichmentChamberHolderBlock.get() }
    override val enrichmentChamberController: EnrichmentChamberController by lazy { enrichmentChamberControllerBlock.get() }
    override val enrichmentChamberExtractor: EnrichmentChamberExtractor by lazy { enrichmentChamberExtractorBlock.get() }
    override val enrichmentChamberReceiver: EnrichmentChamberReceiver by lazy { enrichmentChamberReceiverBlock.get() }
    override val rayTowerBase: RayTowerBase by lazy { rayTowerBaseBlock.get() }
    override val rayTower: RayTower by lazy { rayTowerBlock.get() }
    override val creativeMRUSource: CreativeMRUSource by lazy { creativeMRUSourceBlock.get() }

    private fun registerBasic(
        id: String,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true,
    ) = registerBlock(id, ::Block, properties, shouldRegisterItem)

    private fun <B : Block> registerBlock(
        id: String,
        block: (BlockBehaviour.Properties) -> B,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true,
    ): DeferredBlock<B> {
        val blockKey = { it: Identifier -> ResourceKey.create(Registries.BLOCK, it) }
        val bl =
            blocks.register(id) { rk ->
                block(properties.setId(blockKey(rk)))
            }

        if (shouldRegisterItem) {
            blockItems.register(id) { rk ->
                NamedBlockItem(
                    bl.get(),
                    Item
                        .Properties()
                        .setId(ResourceKey.create(Registries.ITEM, rk))
                        .useBlockDescriptionPrefix(),
                )
            }
        }

        return bl
    }
}
