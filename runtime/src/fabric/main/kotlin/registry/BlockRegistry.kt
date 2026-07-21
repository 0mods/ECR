package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MagicTable
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.item.NamedBlockItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

@Suppress("ACTUAL_WITHOUT_EXPECT", "unused")
actual object BlockRegistry {
    actual val mithrilineFurnace: MithrilineFurnace = register(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnace)
    actual val mithrilineCrystal: CrystalBlock = register(ECRModIDs.MITHRILINE_CRYSTAL, ::CrystalBlock)
    actual val magicTable: MagicTable = register(ECRModIDs.MAGIC_TABLE, ::MagicTable)
    actual val matrixDestructor: MatrixDestructor = register(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructor)
    actual val solarPrism: SolarPrism = register(ECRModIDs.SOLAR_PRISM, ::SolarPrism)
    actual val coldDistiller: ColdDistiller = register(ECRModIDs.COLD_DISTILLER, ::ColdDistiller)
    actual val voidStone: Block = registerBasic(ECRModIDs.VOID_STONE)
    actual val mithrilinePlating: Block = registerBasic(ECRModIDs.MITHRILINE_PLATING)
    actual val pale: Block = registerBasic(ECRModIDs.PALE_BLOCK)
    actual val palePlating: Block = registerBasic(ECRModIDs.PALE_PLATING)
    actual val magicPlating: Block = registerBasic(ECRModIDs.MAGIC_PLATING)
    actual val demonicPlating: Block = registerBasic(ECRModIDs.DEMONIC_PLATING)
    actual val flameCluster: Block = registerBasic(ECRModIDs.FLAME_CLUSTER, shouldRegisterItem = false)
    actual val waterCluster: Block = registerBasic(ECRModIDs.WATER_CLUSTER, shouldRegisterItem = false)
    actual val earthCluster: Block = registerBasic(ECRModIDs.EARTH_CLUSTER, shouldRegisterItem = false)
    actual val airCluster: Block = registerBasic(ECRModIDs.AIR_CLUSTER, shouldRegisterItem = false)

    private fun registerBasic(
        id: String,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true
    ) = register(id, ::Block, properties, shouldRegisterItem)

    private fun <B: Block> register(
        id: String,
        block: (BlockBehaviour.Properties) -> B,
        properties: BlockBehaviour.Properties = BlockBehaviour.Properties.of(),
        shouldRegisterItem: Boolean = true
    ): B {
        val blockKey = { it: Identifier -> ResourceKey.create(Registries.BLOCK, it) }
        val regId = id.ecRL
        val bl = Registry.register(BuiltInRegistries.BLOCK, regId, block(properties.setId(blockKey(regId))))

        if (shouldRegisterItem) {
            Registry.register(
                BuiltInRegistries.ITEM, regId,
                NamedBlockItem(
                    bl, Item.Properties()
                        .setId(ResourceKey.create(Registries.ITEM, regId))
                        .useBlockDescriptionPrefix()
                )
            )
        }

        return bl
    }
}