package team._0mods.ecr.common.particle

import com.mojang.serialization.Codec
import net.minecraft.core.particles.ParticleType

class ECParticleType : ParticleType<ECParticleOptions>(
    true,
    ECParticleOptions.DESERIALIZER
) {
    override fun codec(): Codec<ECParticleOptions> = ECParticleOptions.CODEC
}
