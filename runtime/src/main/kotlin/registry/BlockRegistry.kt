package com.algorithmlx.ecr.registry

import com.algorithmlx.ecr.common.block.AssembledMultiblockPartBlock
import com.algorithmlx.ecr.common.block.ClusterBlock
import com.algorithmlx.ecr.common.block.ColdDistiller
import com.algorithmlx.ecr.common.block.CreativeMRUSource
import com.algorithmlx.ecr.common.block.CrystalBlock
import com.algorithmlx.ecr.common.block.EnrichmentChamberController
import com.algorithmlx.ecr.common.block.EnrichmentChamberExtractor
import com.algorithmlx.ecr.common.block.EnrichmentChamberReceiver
import com.algorithmlx.ecr.common.block.HeatGenerator
import com.algorithmlx.ecr.common.block.MagicTable
import com.algorithmlx.ecr.common.block.MagicalTeleporter
import com.algorithmlx.ecr.common.block.MatrixDestructor
import com.algorithmlx.ecr.common.block.MithrilineFurnace
import com.algorithmlx.ecr.common.block.RadiatingChamber
import com.algorithmlx.ecr.common.block.RayTower
import com.algorithmlx.ecr.common.block.RayTowerBase
import com.algorithmlx.ecr.common.block.SolarPrism
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DropExperienceBlock

interface BlockRegistry {
    val assembledMultiblockPart: AssembledMultiblockPartBlock
    val mithrilineFurnace: MithrilineFurnace
    val radiatingChamber: RadiatingChamber
    val mithrilineCrystal: CrystalBlock
    val magicTable: MagicTable
    val magicalTeleporter: MagicalTeleporter
    val matrixDestructor: MatrixDestructor
    val coldDistiller: ColdDistiller
    val heatGenerator: HeatGenerator
    val solarPrism: SolarPrism
    val voidStone: Block
    val mithrilinePlating: Block
    val pale: Block
    val palePlating: Block
    val magicPlating: Block
    val demonicPlating: Block
    val fortifiedStone: Block
    val flameCluster: ClusterBlock
    val waterCluster: ClusterBlock
    val earthCluster: ClusterBlock
    val airCluster: ClusterBlock
    val fortifiedGlass: Block
    val enrichmentChamberHolder: Block
    val enrichmentChamberController: EnrichmentChamberController
    val enrichmentChamberExtractor: EnrichmentChamberExtractor
    val enrichmentChamberReceiver: EnrichmentChamberReceiver
    val rayTowerBase: RayTowerBase
    val rayTower: RayTower
    val creativeMRUSource: CreativeMRUSource
    val mithrilineOre: DropExperienceBlock
    val deepslateMithrilineOre: DropExperienceBlock

    companion object {
        @JvmStatic
        lateinit var instance: BlockRegistry
    }
}
