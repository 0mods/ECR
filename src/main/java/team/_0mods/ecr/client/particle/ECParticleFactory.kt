package team._0mods.ecr.client.particle

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import team._0mods.ecr.common.particle.ECParticleOptions
import kotlin.math.roundToInt

class ECParticleFactory(private val sprite: SpriteSet): ParticleProvider<ECParticleOptions> {
    override fun createParticle(
        type: ECParticleOptions,
        level: ClientLevel,
        x: Double,
        y: Double,
        z: Double,
        xSpeed: Double,
        ySpeed: Double,
        zSpeed: Double
    ): Particle = ECParticle(
        level, x, y, z,
        xSpeed, ySpeed, zSpeed,
        type.color, 1f, type.size,
        type.lifeTime, type.resizeSpeed.roundToInt(),
        type.physical, type.removeOnGround, this.sprite
    )
}
