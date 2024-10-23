package team._0mods.ecr.common.particle

import com.mojang.brigadier.StringReader
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.particles.ParticleOptions
import net.minecraft.core.particles.ParticleType
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.util.Mth
import net.minecraftforge.registries.ForgeRegistries
import team._0mods.ecr.common.init.registry.ECRegistry
import java.awt.Color

class ECParticleOptions(
    val color: Color,
    size: Float,
    val lifeTime: Int,
    val resizeSpeed: Float,
    val gravity: Float = 0f,
    val friction: Float = 0.97f,
    val physical: Boolean,
    val removeOnGround: Boolean
): ParticleOptions {
    companion object {
        @JvmField
        val CODEC: Codec<ECParticleOptions> = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("color").forGetter { t -> t.color.rgb },
                Codec.FLOAT.fieldOf("size").forGetter { t -> t.size },
                Codec.INT.fieldOf("lifetime").forGetter { t -> t.lifeTime },
                Codec.FLOAT.fieldOf("resize_speed").forGetter { t -> t.resizeSpeed },
                Codec.FLOAT.fieldOf("gravity").orElse(0f).forGetter { t -> t.gravity },
                Codec.FLOAT.fieldOf("friction").orElse(0.97f).forGetter { t -> t.friction },
                Codec.BOOL.fieldOf("has_physics").forGetter { t -> t.physical },
                Codec.BOOL.fieldOf("remove_on_ground").forGetter { t -> t.removeOnGround }
            ).apply(it, ::ECParticleOptions)
        }

        @JvmField
        val DESERIALIZER = object : ParticleOptions.Deserializer<ECParticleOptions> {
            override fun fromCommand(
                particleType: ParticleType<ECParticleOptions>,
                reader: StringReader
            ): ECParticleOptions {
                return try {
                    reader.expect(' '); val r = Mth.clamp(reader.readInt(), 0, 255)
                    reader.expect(' '); val g = Mth.clamp(reader.readInt(), 0, 255)
                    reader.expect(' '); val b = Mth.clamp(reader.readInt(), 0, 255)
                    reader.expect(' '); val size = Mth.clamp(reader.readFloat(), 0.05f, 5f)
                    reader.expect(' '); val lifeTime = reader.readInt()
                    reader.expect(' '); val resizeSpeed = reader.readFloat()
                    reader.expect(' '); val gravity = reader.readFloat()
                    reader.expect(' '); val friction = reader.readFloat()
                    reader.expect(' '); val hasPhysics = reader.readBoolean()
                    reader.expect(' '); val removeOnGround = reader.readBoolean()

                    ECParticleOptions(Color(r, g, b), size, lifeTime, resizeSpeed, gravity, friction, hasPhysics, removeOnGround)
                } catch (e: Exception) {
                    reader.expect(' '); val r = Mth.clamp(reader.readInt(), 0, 255)
                    reader.expect(' '); val g = Mth.clamp(reader.readInt(), 0, 255)
                    reader.expect(' '); val b = Mth.clamp(reader.readInt(), 0, 255)
                    reader.expect(' '); val size = Mth.clamp(reader.readFloat(), 0.05f, 5f)
                    reader.expect(' '); val lifeTime = reader.readInt()
                    reader.expect(' '); val resizeSpeed = reader.readFloat()
                    reader.expect(' '); val hasPhysics = reader.readBoolean()
                    reader.expect(' '); val removeOnGround = reader.readBoolean()
                    ECParticleOptions(Color(r, g, b), size, lifeTime, resizeSpeed, physical = hasPhysics, removeOnGround = removeOnGround)
                }
            }

            override fun fromNetwork(
                particleType: ParticleType<ECParticleOptions>,
                buffer: FriendlyByteBuf
            ): ECParticleOptions {
                val r = Mth.clamp(buffer.readInt(), 0, 255)
                val g = Mth.clamp(buffer.readInt(), 0, 255)
                val b = Mth.clamp(buffer.readInt(), 0, 255)

                val color = Color(r, g, b)
                val size = Mth.clamp(buffer.readFloat(), 0.05f, 5f)
                val lifeTime = buffer.readInt()
                val speed = buffer.readFloat()
                val gravity = buffer.readFloat()
                val friction = buffer.readFloat()
                val hasPhysics = buffer.readBoolean()
                val removeOnGround = buffer.readBoolean()

                return ECParticleOptions(color, size, lifeTime, speed, gravity, friction, hasPhysics, removeOnGround)
            }
        }
    }

    constructor(
        color: Color,
        size: Float,
        lifeTime: Int,
        resizeSpeed: Float,
        physical: Boolean,
        removeOnGround: Boolean
    ): this(color, size, lifeTime, resizeSpeed, 0f, 0.97f, physical = physical, removeOnGround = removeOnGround)

    private constructor(
        rgb: Int,
        size: Float,
        lifeTime: Int,
        resizeSpeed: Float,
        gravity: Float,
        friction: Float,
        physical: Boolean,
        removeOnGround: Boolean
    ): this(Color(rgb), Mth.clamp(size, 0.05f, 5f), lifeTime, resizeSpeed, gravity, friction, physical, removeOnGround)

    val size = Mth.clamp(size, 0.05f, 5f)

    override fun getType(): ParticleType<*> = ECRegistry.ecParticle.get()

    override fun writeToNetwork(buffer: FriendlyByteBuf) {
        buffer.writeInt(color.red)
        buffer.writeInt(color.green)
        buffer.writeInt(color.blue)
        buffer.writeFloat(size)
        buffer.writeInt(lifeTime)
        buffer.writeFloat(resizeSpeed)
        buffer.writeFloat(gravity)
        buffer.writeFloat(friction)
        buffer.writeBoolean(physical)
        buffer.writeBoolean(removeOnGround)
    }

    override fun writeToString(): String =
        "${ForgeRegistries.PARTICLE_TYPES.getKey(this.type)} ${color.red} ${color.green} ${color.blue} $size $lifeTime $gravity $friction $resizeSpeed $physical $removeOnGround"
}
