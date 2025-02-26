package team._0mods.ecr.common.api

import net.minecraft.client.renderer.texture.AbstractTexture

interface IAbstractTexture {
    fun ecr_setBlurMipmap(blur: Boolean, mipmap: Boolean)
    fun ecr_restoreLastBlurMipmap()
}

fun AbstractTexture.blurMipmap(blur: Boolean, mipmap: Boolean) {
    //? if fabric {
    (this as IAbstractTexture).ecr_setBlurMipmap(blur, mipmap)
    //?} else {
    /*this.setBlurMipmap(blur, mipmap)
    *///?}
}

fun AbstractTexture.restoreLastMipmapBlur() {
    //? if fabric {
    (this as IAbstractTexture).ecr_restoreLastBlurMipmap()
    //?} else {
    /*this.restoreLastBlurMipmap()
    *///?}
}