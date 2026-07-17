package com.algorithmlx.ecr.fabric.init.registry

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.Envoyer
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import com.algorithmlx.ecr.common.init.ECRModIDs
import com.algorithmlx.ecr.common.init.registry.BlockRegistry
import com.algorithmlx.ecr.common.item.NamedBlockItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour

object FabricBlockRegistry: BlockRegistry {
    override val mithrilineFurnace: MithrilineFurnace = register(ECRModIDs.MITHRILINE_FURNACE, ::MithrilineFurnace)
    override val mithrilineCrystal: CrystalBlock = register(ECRModIDs.MITHRILINE_CRYSTAL, ::CrystalBlock)
    override val mithrilinePlating: Block = register(ECRModIDs.MITHRILINE_PLATING, ::Block)
    override val envoyer: Envoyer = register(ECRModIDs.ENVOYER, ::Envoyer)
    override val matrixDestructor: MatrixDestructor = register(ECRModIDs.MATRIX_DESTRUCTOR, ::MatrixDestructor)
    override val solarPrism: SolarPrism = register(ECRModIDs.SOLAR_PRISM, ::SolarPrism)
    override val coldDistiller: ColdDistiller = register(ECRModIDs.COLD_DISTILLER, ::ColdDistiller)

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