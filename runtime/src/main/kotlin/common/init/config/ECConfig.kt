package com.algorithmlx.ecr.common.init.config

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@JsonDefaults
@Serializable
data class ECConfig(
    @SerialName("disabled_researches")
    val disabledResearches: List<String> = emptyList(),
    @SerialName("research_book") val researchBook: ResearchBookConfig = ResearchBookConfig()
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
