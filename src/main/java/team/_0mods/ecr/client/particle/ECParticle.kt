package team._0mods.ecr.client.particle

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.BufferBuilder
import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.Tesselator
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.ParticleRenderType
import net.minecraft.client.particle.SpriteSet
import net.minecraft.client.particle.TextureSheetParticle
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.renderer.texture.TextureManager
import org.lwjgl.opengl.GL11
import team._0mods.ecr.ModId
import java.awt.Color

class ECParticle(
    level: ClientLevel, x: Double, y: Double, z: Double,
    xSpeed: Double, ySpeed: Double, zSpeed: Double,
    color: Color, alpha: Float, size: Float, lt: Int,
    val resizeSpeed: Int, physical: Boolean, val removeOnGround: Boolean, val sprites: SpriteSet
): TextureSheetParticle(level, x, y, z, xSpeed, ySpeed, zSpeed) {
    init {
        this.setColor(color.red / 255f, color.green / 255f, color.blue / 255f)
        this.setSize(size, size)
        this.lifetime = lt
        this.quadSize = size
        this.alpha = alpha
        this.xd = xSpeed
        this.yd = ySpeed
        this.zd = zSpeed
        this.hasPhysics = physical

        this.pickSprite(sprites)
    }

    override fun getQuadSize(scaleFactor: Float): Float = this.quadSize

    override fun getLightColor(partialTick: Float): Int = LightTexture.pack(15, 15)

    override fun getRenderType(): ParticleRenderType = object : ParticleRenderType {
        @Suppress("DEPRECATION")
        val atlas = TextureAtlas.LOCATION_PARTICLES

        override fun begin(builder: BufferBuilder, textureManager: TextureManager) {
            RenderSystem.depthMask(false)
            RenderSystem.enableBlend()
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)
            textureManager.getTexture(atlas).setBlurMipmap(true, false)
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.PARTICLE)
        }

        override fun end(tesselator: Tesselator) {
            tesselator.end()
            Minecraft.getInstance().textureManager.getTexture(atlas).restoreLastBlurMipmap()
            RenderSystem.disableBlend()
            RenderSystem.depthMask(true)
        }

        override fun toString(): String = "$ModId:ec_part"
    }

    override fun tick() {
        this.quadSize *= this.resizeSpeed

        this.xo = x
        this.yo = y
        this.zo = z

        this.move(xd, yd, zd)

        if (this.onGround && this.removeOnGround) this.remove()

        if (this.yo == this.y && this.yd > 0) this.remove()

        if (this.age++ >= this.lifetime) this.remove()
    }
}
