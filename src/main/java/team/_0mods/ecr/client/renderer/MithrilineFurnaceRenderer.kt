package team._0mods.ecr.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
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
import net.minecraft.client.resources.model.Material
import net.minecraft.world.inventory.InventoryMenu
import team._0mods.ecr.ModId
import team._0mods.ecr.api.rl
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import kotlin.math.roundToInt

class MithrilineFurnaceRenderer(ctx: BlockEntityRendererProvider.Context): BlockEntityRenderer<MithrilineFurnaceEntity> {
    companion object {
        @JvmField val MF_LAYER = ModelLayerLocation("$ModId:block/mithriline_furnace".rl, "main")
        @JvmField val MF_RESOURCE_LOCATION = Material(InventoryMenu.BLOCK_ATLAS, "$ModId:entity/mithriline_furnace/core".rl)

        @JvmStatic
        fun createBodyLayer(): LayerDefinition {
            val meshDefinition = MeshDefinition().apply {
                root.addOrReplaceChild(
                    "core",
                    CubeListBuilder.create().texOffs(0, 4).addBox(2.0f, 2.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(0, 4).addBox(-4.0f, 2.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(-2.0f, 2.0f, 2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 4).addBox(2.0f, -4.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(0, 4).addBox(-4.0f, -4.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(-2.0f, -4.0f, 2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(-2.0f, 2.0f, -4.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(-4.0f, -3.0f, -4.0f, 2.0f, 6.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(-4.0f, -3.0f, 2.0f, 2.0f, 6.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(2.0f, -3.0f, -4.0f, 2.0f, 6.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 2).addBox(2.0f, -3.0f, 2.0f, 2.0f, 6.0f, 2.0f, CubeDeformation(0.0f)),
                    /*CubeListBuilder.create().texOffs(8, 8)
                        .addBox(2.0f, 2.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(8, 2).addBox(-4.0f, 2.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(16, 8).addBox(-2.0f, 2.0f, 2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 6).addBox(2.0f, -4.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(0, 0).addBox(-4.0f, -4.0f, -2.0f, 2.0f, 2.0f, 4.0f, CubeDeformation(0.0f))
                        .texOffs(16, 0).addBox(-2.0f, -4.0f, 2.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(12, 14).addBox(-2.0f, 2.0f, -4.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 14).addBox(-2.0f, -4.0f, -4.0f, 4.0f, 2.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(22, 22).addBox(-4.0f, -2.0f, -4.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(16, 18).addBox(-4.0f, -2.0f, 2.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(0, 18).addBox(2.0f, -2.0f, -4.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f))
                        .texOffs(8, 18).addBox(2.0f, -2.0f, 2.0f, 2.0f, 4.0f, 2.0f, CubeDeformation(0.0f)),*/
                    PartPose.offset(0.0f, 16.0f, 0.0f)
                )
            }

            return LayerDefinition.create(meshDefinition, 16, 16)
//            return LayerDefinition.create(meshDefinition, 32, 32)
        }
    }

    private val body: ModelPart

    init {
        val part = ctx.bakeLayer(MF_LAYER)
        body = part.getChild("core")
    }

    override fun render(
        blockEntity: MithrilineFurnaceEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        val rotationSpeedCoefficient = 45f

        val totalTicks = blockEntity.tickCount + partialTick
        var rotAngle = totalTicks * rotationSpeedCoefficient / 20f

        if (!blockEntity.successfulStructure) {
            rotAngle /= 4

            rotAngle = (rotAngle / 90).roundToInt() * 90f
        }

        poseStack.pushPose()
        poseStack.translate(0.5, -0.5, 0.5)
        poseStack.mulPose(Vector3f.YP.rotationDegrees(rotAngle))

        body.yRot = 0f
        val consumer = MF_RESOURCE_LOCATION.buffer(bufferSource, RenderType::entityCutoutNoCull)
        body.render(poseStack, consumer, packedLight, packedOverlay)

        poseStack.popPose()
    }
}
