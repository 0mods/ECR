package team._0mods.ecr.api.ui

enum class Alignment(override val factorX: Float, override val factorY: Float): Placement {
    BOTTOM_CENTER(0.5F, 1.0F),
    BOTTOM_RIGHT(1F, 1.0F),
    BOTTOM_LEFT(0F, 1.0F),

    CENTER(0.5F, 0.5F),
    RIGHT_CENTER(1F, 0.5F),
    LEFT_CENTER(0F, 0.5F),

    TOP_CENTER(0.5F, 0F),
    TOP_RIGHT(1F, 0F),
    TOP_LEFT(0F, 0F);

    fun factorX() = factorX
    fun factorY() = factorY
}


enum class Anchor(val factor: Float) {
    START(0f),
    CENTER(0.5f),
    END(1f)
}


interface Placement {
    val factorX: Float
    val factorY: Float
}