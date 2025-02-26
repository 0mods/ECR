package team._0mods.ecr.mixin.client;

import net.minecraft.client.renderer.texture.AbstractTexture;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import team._0mods.ecr.common.api.IAbstractTexture;

@Mixin(AbstractTexture.class)
public abstract class AbstractTextureMixin /*? if fabric {*/ implements IAbstractTexture /*?}*/ {
    @Shadow protected boolean blur;
    @Shadow protected boolean mipmap;

    @Shadow public abstract void setFilter(boolean blur, boolean mipmap);

    //? if fabric {
    @Unique private boolean ecr$lastBlur;
    @Unique private boolean ecr$lastMipmap;

    @Override
    public void ecr_setBlurMipmap(boolean b1, boolean b2) {
        this.ecr$lastBlur = this.blur;
        this.ecr$lastMipmap = this.mipmap;
        this.setFilter(blur, mipmap);
    }

    @Override
    public void ecr_restoreLastBlurMipmap() {
        this.setFilter(this.ecr$lastBlur, this.ecr$lastMipmap);
    }
    //?}
}
