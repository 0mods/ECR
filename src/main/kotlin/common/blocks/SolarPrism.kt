package team._0mods.ecr.common.blocks

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.EnumProperty
import team._0mods.ecr.common.api.PropertiedBlock
import team._0mods.ecr.common.blocks.part.SolarPrismPart

class SolarPrism(properties: Properties) : PropertiedBlock(properties) {
    init {
        this.registerDefaultState(
            this.stateDefinition.any().setValue(PART, SolarPrismPart.DEFAULT)
        )
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(PART)
    }

    companion object {
        @JvmField
        val PART: EnumProperty<SolarPrismPart> = EnumProperty.create("prism_part", SolarPrismPart::class.java)
    }
}