package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.Envoyer
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import com.algorithmlx.ecr.common.item.NamedBlockItem
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredRegister

class NeoForgeBlockRegistry(bus: IEventBus): BlockRegistry {
    private val blockItems = DeferredRegister.createItems(ModId)
    private val blocks = DeferredRegister.createBlocks(ModId)

    init {
        blocks.register(bus)
        blockItems.register(bus)
    }

    private val mithrilineFurnaceBlock = registerBlock(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnace)
    private val mithrilineCrystalBlock = registerBlock(ECRModIDs.MITHRILINE_CRYSTAL, ::CrystalBlock)
    private val mithrilinePlatingBlock = registerBlock(ECRModIDs.MITHRILINE_PLATING, ::Block)
    private val envoyerBlock = registerBlock(ECRModIDs.ENVOYER, ::Envoyer)
    private val matrixDestructorBlock = registerBlock(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructor)
    private val solarPrismBlock = registerBlock(ECRModIDs.SOLAR_PRISM, ::SolarPrism)
    private val coldDistillerBlock = registerBlock(ECRModIDs.COLD_DISTILLER, ::ColdDistiller)

    override val mithrilineFurnace: MithrilineFurnace by lazy { mithrilineFurnaceBlock.get() }
    override val mithrilineCrystal: CrystalBlock by lazy { mithrilineCrystalBlock.get() }
    override val mithrilinePlating: Block by lazy { mithrilinePlatingBlock.get() }
    override val envoyer: Envoyer by lazy { envoyerBlock.get() }
    override val matrixDestructor: MatrixDestructor by lazy { matrixDestructorBlock.get() }
    override val solarPrism: SolarPrism by lazy { solarPrismBlock.get() }
    override val coldDistiller: ColdDistiller by lazy { coldDistillerBlock.get() }

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
