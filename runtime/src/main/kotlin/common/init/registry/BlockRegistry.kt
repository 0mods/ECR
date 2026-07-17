package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.Envoyer
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.SolarPrism
import net.minecraft.world.level.block.Block

interface BlockRegistry {
    val mithrilineFurnace: MithrilineFurnace
    val mithrilineCrystal: CrystalBlock
    val mithrilinePlating: Block
    val envoyer: Envoyer
    val matrixDestructor: MatrixDestructor
    val solarPrism: SolarPrism
    val coldDistiller: ColdDistiller
    val voidStone: Block

    companion object {
        lateinit var instance: BlockRegistry
    }
}
