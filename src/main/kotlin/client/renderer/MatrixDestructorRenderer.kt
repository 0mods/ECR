package team._0mods.ecr.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemDisplayContext
import ru.hollowhorizon.hc.common.utils.get
import team._0mods.ecr.common.blocks.entity.MatrixDestructorEntity

class MatrixDestructorRenderer(private val ctx: BlockEntityRendererProvider.Context): BlockEntityRenderer<MatrixDestructorEntity> {
    override fun render(
        blockEntity: MatrixDestructorEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val stack = blockEntity[MatrixDestructorEntity.ItemContainer::class].items.getItem(0)

        if (!stack.isEmpty) {
            poseStack.apply {
                pushPose()
                translate(0.5, 0.71875, 0.5)
                val scale = if (stack.item is BlockItem) 0.75F else 0.55F
                scale(scale, scale, scale)
                val time = System.currentTimeMillis() / 800.0
                mulPose(Axis.YP.rotationDegrees(((time * 12.5) % 360).toFloat()))
                ctx.itemRenderer.renderStatic(stack, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.level, 0)
                popPose()
            }
        }
    }
}