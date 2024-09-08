package team._0mods.ecr.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import ru.hollowhorizon.hc.client.utils.rl
import team._0mods.ecr.ModId
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity

class MithrilineFurnaceRenderer(ctx: BlockEntityRendererProvider.Context): BlockEntityRenderer<MithrilineFurnaceEntity> {
    companion object {
        @JvmField val MF_LAYER = ModelLayerLocation("$ModId:mithriline_furnace".rl, "core")
        @JvmField val MF_RESOURCE_LOCATION = Material(TextureAtlas.LOCATION_BLOCKS, "$ModId:block/mithriline_furnace".rl)

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition().apply {
                root.addOrReplaceChild(
                    "core",
                    CubeListBuilder.create().texOffs(90, 8).addBox(2.0f, 2.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(90, 2).addBox(-4.0f, 2.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(98, 8).addBox(-2.0f, 2.0f, 2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(82, 6).addBox(2.0f, -4.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(82, 0).addBox(-4.0f, -4.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(98, 0).addBox(-2.0f, -4.0f, 2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(94, 14).addBox(-2.0f, 2.0f, -4.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(82, 14).addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(104, 22).addBox(-4.0f, -2.0f, -4.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(98, 18).addBox(-4.0f, -2.0f, 2.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(82, 18).addBox(2.0f, -2.0f, -4.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(90, 18).addBox(2.0f, -2.0f, 2.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),
                    PartPose.ZERO
                )
            }

            return LayerDefinition.create(meshDefinition, 128, 128)
        }
    }

    private val body: ModelPart

    init {
        val mp = ctx.bakeLayer(MF_LAYER)
        body = mp.getChild("core")
    }

    override fun render(
        blockEntity: MithrilineFurnaceEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        poseStack.apply {
            val interpol = blockEntity.previousRot + (blockEntity.rotAngle - blockEntity.previousRot) * partialTick

            pushPose()
            translate(0.5, 0.5, 0.5)

            body.yRot = Math.toRadians(interpol.toDouble()).toFloat()
            val consumer = MF_RESOURCE_LOCATION.buffer(bufferSource, RenderType::entityCutoutNoCull)
            body.render(poseStack, consumer, packedLight, packedOverlay)

            popPose()
        }
    }
}
