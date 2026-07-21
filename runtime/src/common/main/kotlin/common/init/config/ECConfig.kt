package com.algorithmlx.ecr.common.init.config

import com.algorithmlx.ecr.api.ModId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

@JsonComment([
    "Hello! This is $ModId's config.",
    "You can configure mod it however you like. If it is possible.",
    "The config supports both single-line and multi-line comments, similar to those in compiled languages.",
    "If config broken, it backups with current time.",
    "Comments are not cleared when the mod is restarted."
], multiline = true)
@JsonDefaults
@Serializable
data class ECConfig(
    @SerialName("disabled_researches")
    val disabledResearches: List<String> = emptyList(),
    @SerialName("research_book") val researchBook: ResearchBookConfig = ResearchBookConfig(),
    @SerialName("cold_distiller") val coldDistillerConfig: ColdDistillerConfig = ColdDistillerConfig()
) {
    companion object {
        @JvmStatic
        lateinit var instance: ECConfig

        @JvmStatic
        val current: ECConfig
            get() = if (::instance.isInitialized) instance else ECConfig()
    }
}

@JsonDefaults
@Serializable
data class ResearchBookConfig(
    val space: ResearchBookSpaceConfig = ResearchBookSpaceConfig(),
    val graph: ResearchBookGraphConfig = ResearchBookGraphConfig()
)

@JsonDefaults
@Serializable
data class ResearchBookSpaceConfig(
    @SerialName("parallax_strength") val parallaxStrength: Float = 1F,
    @SerialName("star_density") val starDensity: Float = 1F,
    @SerialName("star_size") val starSize: Float = 1F
)

@JsonDefaults
@Serializable
data class ResearchBookGraphConfig(
    @SerialName("zoom_step") val zoomStep: Float = 0.12F,
    @SerialName("available_blink_seconds") val availableBlinkSeconds: Double = 3.0
)

@JsonDefaults
@Serializable
data class ColdDistillerConfig(
    @SerialName("ice_block_radius")
    val iceBlockRadius: Int = 3,
    @SerialName("min_ice_blocks")
    val minIceBlocks: Int = 2,
    @SerialName("min_mru")
    val minMruPerSecond: Int = 1,
    @SerialName("max_mru")
    val maxMruPerSecond: Int = 16,
    @SerialName("destroy_ice")
    val destroyIce: DestroyIceConfig = DestroyIceConfig()
) {
}

@JsonDefaults
@Serializable
data class DestroyIceConfig(
    val enabled: Boolean = true,
    val chance: Chance = Chance(7, 10),
    val time: Int = 100
)

@JsonDefaults
@Serializable
data class Chance(
    val min: Int,
    val max: Int
) {
    init {
        require(-100 <= max && max <= 100)
        require(min in (-100 .. max))
    }

    fun roll(): Int = Random.nextInt(max)

    fun isRolled(): Boolean = min >= this.roll()
}
