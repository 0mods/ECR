package team._0mods.ecr.client.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.*
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.resources.model.Material
import net.minecraft.world.inventory.InventoryMenu
import team._0mods.ecr.ModId
import team._0mods.ecr.common.blocks.entity.MithrilineFurnaceEntity
import team._0mods.ecr.common.rl

class MithrilineFurnaceRenderer(ctx: BlockEntityRendererProvider.Context): BlockEntityRenderer<MithrilineFurnaceEntity> {
    companion object {
        @JvmField val MF_LAYER = ModelLayerLocation("$ModId:mithriline_furnace".rl, "main")
        @JvmField val MF_RESOURCE_LOCATION = Material(InventoryMenu.BLOCK_ATLAS, "$ModId:block/entity/mithriline_furnace_core".rl)

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
                    PartPose.offset(0.0f, 16.0f, 0.0f)
                )
            }

            return LayerDefinition.create(meshDefinition, 16, 16)
        }
    }

    private var previousRot = 0f
    private var rotAngle = 0f
    private val body: ModelPart
    private val mc: Minecraft

    init {
        val part = ctx.bakeLayer(MF_LAYER)
        body = part.getChild("core")
        mc = Minecraft.getInstance()
    }

    override fun render(
        blockEntity: MithrilineFurnaceEntity,
        partialTick: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        previousRot = rotAngle

        if (blockEntity.successfulStructure) {
            rotAngle += 45f * (1f / 20f)
        } else if (rotAngle > 0) {
            rotAngle += 45f * (1f / 20f) / 2

            if (rotAngle % 360 == 0f) rotAngle = 0f
        }

        poseStack.apply {
            val interpol = previousRot + (rotAngle - previousRot) * partialTick

            pushPose()
            translate(0.5, -0.5, 0.5)

            body.yRot = Math.toRadians(interpol.toDouble()).toFloat()
            val consumer = MF_RESOURCE_LOCATION.buffer(bufferSource, RenderType::entitySolid)
            body.render(poseStack, consumer, packedLight, packedOverlay)

            popPose()
        }
    }
}