package com.algorithmlx.ecr.neoforge.init.registry

import com.algorithmlx.ecr.api.ModId
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MithrilineFurnace
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

    private val mithrilineFurnaceBlock = registerBlock("mithriline_furnace", ::MithrilineFurnace)
    private val mithrilineCrystalBlock = registerBlock("mithriline_crystal", ::CrystalBlock)
    private val mithrilinePlatingBlock = registerBlock("mithriline_plating", ::Block)

    override val mithrilineFurnace: MithrilineFurnace by lazy { mithrilineFurnaceBlock.get() }
    override val mithrilineCrystal: CrystalBlock by lazy { mithrilineCrystalBlock.get() }
    override val mithrilinePlating: Block by lazy { mithrilinePlatingBlock.get() }

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
