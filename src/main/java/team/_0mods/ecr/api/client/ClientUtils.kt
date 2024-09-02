// Class from https://github.com/HollowHorizon/HollowCore
package team._0mods.ecr.api.client

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.resources.ResourceLocation
import java.io.FileNotFoundException
import java.io.InputStream

fun isCursorAtPos(cursorX: Int, cursorY: Int, x: Int, y: Int, width: Int, height: Int) : Boolean =
    cursorX >= x && cursorY >=y && cursorX <= x + width && cursorY <= y + height

fun PoseStack.blit(
    id: ResourceLocation,
    x: Int,
    y: Int,
    uOffset: Float = 0f,
    vOffset: Float = 0f,
    width: Int = 176,
    height: Int = 166,
    textureWidth: Int = 256,
    textureHeight: Int = 256
) {
    RenderSystem.setShader(GameRenderer::getPositionShader)
    RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
    RenderSystem.setShaderTexture(0, id)
    AbstractContainerScreen.blit(
        this,
        x,
        y,
        uOffset,
        vOffset,
        width,
        height,
        textureWidth,
        textureHeight
    )
}

fun AbstractContainerScreen<*>.xPos(x: Int): Int {
    val j = ((this.width / 2) - (this.imageWidth / 2))
    return x + j
}

fun AbstractContainerScreen<*>.yPos(y: Int): Int {
    val j = ((this.height / 2) - (this.imageHeight / 2))
    return y + j
}

fun AbstractContainerScreen<*>.xPos(x: Float): Float {
    val j = ((this.width / 2) - (this.imageWidth / 2))
    return x + j
}

fun AbstractContainerScreen<*>.yPos(y: Float): Float {
    val j = ((this.height / 2) - (this.imageHeight / 2))
    return y + j
}

val ResourceLocation.stream: InputStream
    get() = try {
        Minecraft.getInstance().resourceManager.getResource(this).orElseThrow().open()
    } catch (e: Exception) {
        Thread.currentThread().contextClassLoader.getResourceAsStream("assets/${this.namespace}/${this.path}") ?: throw FileNotFoundException("Resource $this not found!")
    }
