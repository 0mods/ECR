package team._0mods.ecr.api.block

import net.minecraft.util.StringRepresentable

interface PartRepresentable: StringRepresentable {
    val id: String

    override fun getSerializedName(): String {
        return id
    }
}
