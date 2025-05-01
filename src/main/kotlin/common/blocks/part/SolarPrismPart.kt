package team._0mods.ecr.common.blocks.part

import team._0mods.ecr.api.block.PartRepresentable

enum class SolarPrismPart(override val id: String): PartRepresentable {
    DEFAULT("default"),
    EMPTY("empty"),
    NORTH("north"),
    NORTH_EAST("north_east"),
    NORTH_EAST_SOUTH("north_east_south"),
    NORTH_EAST_WEST("north_east_west"),
    NORTH_SOUTH("north_south"),
    NORTH_SOUTH_WEST("north_south_west"),
    NORTH_WEST("north_west"),
    SOUTH("south"),
    SOUTH_WEST("south_west"),
    EAST("east"),
    EAST_SOUTH("east_south"),
    EAST_SOUTH_WEST("east_south_west"),
    EAST_WEST("east_west"),
    WEST("west")
}
