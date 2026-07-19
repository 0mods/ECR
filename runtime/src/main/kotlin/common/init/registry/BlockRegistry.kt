package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.block.*
import net.minecraft.world.level.block.Block

interface BlockRegistry {
    val mithrilineFurnace: MithrilineFurnace
    val mithrilineCrystal: CrystalBlock
    val magicTable: MagicTable
    val matrixDestructor: MatrixDestructor
    val coldDistiller: ColdDistiller
    val solarPrism: SolarPrism
    val voidStone: Block
    val mithrilinePlating: Block
    val pale: Block
    val palePlating: Block
    val magicPlating: Block
    val demonicPlating: Block
    val flameCluster: Block
    val waterCluster: Block
    val earthCluster: Block
    val airCluster: Block

    companion object {
        lateinit var instance: BlockRegistry
    }
}
