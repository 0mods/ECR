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

class ECParticleOptions(val color: Color, size: Float, val lifeTime: Int, val resizeSpeed: Float, val physical: Boolean, val removeOnGround: Boolean): ParticleOptions {
    companion object {
        @JvmField
        val CODEC = RecordCodecBuilder.create {
            it.group(
                Codec.INT.fieldOf("color").forGetter { t: ECParticleOptions -> t.color.rgb },
                Codec.FLOAT.fieldOf("size").forGetter { t: ECParticleOptions -> t.size },
                Codec.INT.fieldOf("lifetime").forGetter { t: ECParticleOptions -> t.lifeTime },
                Codec.FLOAT.fieldOf("resize_speed").forGetter { t: ECParticleOptions -> t.resizeSpeed },
                Codec.BOOL.fieldOf("has_physics").forGetter { t: ECParticleOptions -> t.physical },
                Codec.BOOL.fieldOf("remove_on_ground").forGetter { t: ECParticleOptions -> t.removeOnGround }
            ).apply(it, ::ECParticleOptions)
        }

        val DESERIALIZER = object : ParticleOptions.Deserializer<ECParticleOptions> {
            override fun fromCommand(
                particleType: ParticleType<ECParticleOptions>,
                reader: StringReader
            ): ECParticleOptions {
                reader.expect(' '); val r = Mth.clamp(reader.readInt(), 0, 255)
                reader.expect(' '); val g = Mth.clamp(reader.readInt(), 0, 255)
                reader.expect(' '); val b = Mth.clamp(reader.readInt(), 0, 255)
                reader.expect(' '); val size = Mth.clamp(reader.readFloat(), 0.05f, 5f)
                reader.expect(' '); val lifeTime = reader.readInt()
                reader.expect(' '); val resizeSpeed = reader.readFloat()
                reader.expect(' '); val hasPhysics = reader.readBoolean()
                reader.expect(' '); val removeOnGround = reader.readBoolean()

                return ECParticleOptions(Color(r, g, b), size, lifeTime, resizeSpeed, hasPhysics, removeOnGround)
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
                val hasPhysics = buffer.readBoolean()
                val removeOnGround = buffer.readBoolean()

                return ECParticleOptions(color, size, lifeTime, speed, hasPhysics, removeOnGround)
            }
        }
    }

    private constructor(rgb: Int, size: Float, lifeTime: Int, resizeSpeed: Float, physical: Boolean, removeOnGround: Boolean):
            this(Color(rgb), Mth.clamp(size, 0.05f, 5f), lifeTime, resizeSpeed, physical, removeOnGround)

    val size = Mth.clamp(size, 0.05f, 5f)

    override fun getType(): ParticleType<*> = ECRegistry.ecParticle.get()

    override fun writeToNetwork(buffer: FriendlyByteBuf) {
        buffer.writeInt(color.red)
        buffer.writeInt(color.green)
        buffer.writeInt(color.blue)
        buffer.writeFloat(size)
        buffer.writeInt(lifeTime)
        buffer.writeFloat(resizeSpeed)
        buffer.writeBoolean(physical)
        buffer.writeBoolean(removeOnGround)
    }

    override fun writeToString(): String =
        "${ForgeRegistries.PARTICLE_TYPES.getKey(this.type)} ${color.red} ${color.green} ${color.blue} $size $lifeTime $resizeSpeed $physical $removeOnGround"
}
