package com.algorithmlx.ecr.common.init.registry

import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import net.minecraft.world.level.block.Block

interface BlockRegistry {
    val mithrilineFurnace: MithrilineFurnace
    val mithrilineCrystal: CrystalBlock
    val mithrilinePlating: Block

    companion object {
        lateinit var instance: BlockRegistry
    }
}
