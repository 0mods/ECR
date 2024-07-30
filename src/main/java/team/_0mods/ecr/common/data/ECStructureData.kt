package team._0mods.ecr.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ECStructureData(
    val pattern: Array<Array<String>>,
    val symbols: Array<SymbolMatcher>
) {
    @Serializable
    data class SymbolMatcher(
        val symbol: Char,
        val tag: TagMatcher? = null,
        val block: String? = null,
        @SerialName("center") val isCenter: Boolean = false
    )

    @Serializable
    data class TagMatcher(
        val value: String,
        val tag: String
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ECStructureData

        if (!pattern.contentDeepEquals(other.pattern)) return false
        if (!symbols.contentEquals(other.symbols)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pattern.contentDeepHashCode()
        result = 31 * result + symbols.contentHashCode()
        return result
    }
}
