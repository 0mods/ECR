package team._0mods.ecr.common.init.registry

import net.minecraft.core.Holder
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import team._0mods.ecr.api.utils.ecRL

object ECDamageSources {
    @JvmStatic
    fun mru(access: RegistryAccess): Holder<DamageType> =
        access.getHolder(ResourceKey.create(Registries.DAMAGE_TYPE, "mru".ecRL))

    @get:JvmStatic
    val Holder<DamageType>.asSource get() = DamageSource(this)

    private fun RegistryAccess.getHolder(id: ResourceKey<DamageType>) =
        this.registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(id)
}