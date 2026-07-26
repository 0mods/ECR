package com.algorithmlx.ecr.client.renderer

import com.algorithmlx.ecr.api.utils.ecRL
import com.algorithmlx.ecr.common.block.entity.EnrichmentChamberControllerEntity
import com.algorithmlx.ecr.mixin.client.RenderPipelinesAccessor
import com.algorithmlx.ecr.mixin.client.RenderTypeAccessor
import com.mojang.blaze3d.pipeline.RenderPipeline
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.client.renderer.feature.ModelFeatureRenderer
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.client.renderer.rendertype.RenderTypes
import net.minecraft.client.renderer.state.level.CameraRenderState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f
import java.util.Random
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.sin

class EnrichmentChamberControllerRenderState: BlockEntityRenderState() {
    var innerBounds: AABB? = null
    var fill = 0F
    var fullyCharged = false
    var overflowing = false
    var animationTicks = 0.0
}

class EnrichmentChamberControllerRenderer(
    context: BlockEntityRendererProvider.Context
): BlockEntityRenderer<EnrichmentChamberControllerEntity, EnrichmentChamberControllerRenderState> {
    override fun createRenderState() = EnrichmentChamberControllerRenderState()

    override fun extractRenderState(
        blockEntity: EnrichmentChamberControllerEntity,
        state: EnrichmentChamberControllerRenderState,
        partialTicks: Float,
        cameraPosition: Vec3,
        breakProgress: ModelFeatureRenderer.CrumblingOverlay?
    ) {
        super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress)

        val storage = blockEntity.mruStorage
        val amount = storage.mru
        val capacity = storage.mruCapacity
        state.fill = when {
            amount <= 0 -> 0F
            capacity <= 0 -> 1F
            else -> (amount.toDouble() / capacity).coerceIn(0.0, 1.0).toFloat()
        }
        state.fullyCharged = capacity > 0 && amount >= capacity
        state.overflowing = amount > capacity
        state.animationTicks = (blockEntity.level?.gameTime ?: 0L) + partialTicks.toDouble()
        state.innerBounds = blockEntity.innerBounds?.move(
            -blockEntity.blockPos.x.toDouble(),
            -blockEntity.blockPos.y.toDouble(),
            -blockEntity.blockPos.z.toDouble()
        )
    }

    override fun submit(
        state: EnrichmentChamberControllerRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState
    ) {
        val bounds = state.innerBounds ?: return

        val centerX = (bounds.minX + bounds.maxX) * 0.5
        val centerY = (bounds.minY + bounds.maxY) * 0.5
        val centerZ = (bounds.minZ + bounds.maxZ) * 0.5
        val fullHalfX = ((bounds.maxX - bounds.minX - BOUNDS_INSET * 2.0) * 0.5)
            .coerceAtLeast(0.0)
        val fullHalfY = ((bounds.maxY - bounds.minY - BOUNDS_INSET * 2.0) * 0.5)
            .coerceAtLeast(0.0)
        val fullHalfZ = ((bounds.maxZ - bounds.minZ - BOUNDS_INSET * 2.0) * 0.5)
            .coerceAtLeast(0.0)
        if (fullHalfX == 0.0 || fullHalfY == 0.0 || fullHalfZ == 0.0) return

        poseStack.pushPose()
        poseStack.translate(centerX, centerY, centerZ)
        if (state.fill > 0F) {
            val halfX = fullHalfX * state.fill
            val halfY = fullHalfY * state.fill
            val halfZ = fullHalfZ * state.fill
            val lowColor = if (state.overflowing) OVERFLOW_LOW_COLOR else NORMAL_LOW_COLOR
            val highColor = if (state.overflowing) OVERFLOW_HIGH_COLOR else NORMAL_HIGH_COLOR

            submitNodeCollector.submitCustomGeometry(poseStack, ENERGY_RENDER_TYPE) { pose, consumer ->
                renderBox(
                    pose,
                    consumer,
                    -halfX.toFloat(),
                    -halfY.toFloat(),
                    -halfZ.toFloat(),
                    halfX.toFloat(),
                    halfY.toFloat(),
                    halfZ.toFloat(),
                    lowColor,
                    highColor
                )
                renderBox(
                    pose,
                    consumer,
                    (-halfX * MIDDLE_LAYER_SCALE).toFloat(),
                    (-halfY * MIDDLE_LAYER_SCALE).toFloat(),
                    (-halfZ * MIDDLE_LAYER_SCALE).toFloat(),
                    (halfX * MIDDLE_LAYER_SCALE).toFloat(),
                    (halfY * MIDDLE_LAYER_SCALE).toFloat(),
                    (halfZ * MIDDLE_LAYER_SCALE).toFloat(),
                    scaleAlpha(lowColor, MIDDLE_LAYER_ALPHA),
                    scaleAlpha(highColor, MIDDLE_LAYER_ALPHA)
                )
                renderBox(
                    pose,
                    consumer,
                    (-halfX * INNER_LAYER_SCALE).toFloat(),
                    (-halfY * INNER_LAYER_SCALE).toFloat(),
                    (-halfZ * INNER_LAYER_SCALE).toFloat(),
                    (halfX * INNER_LAYER_SCALE).toFloat(),
                    (halfY * INNER_LAYER_SCALE).toFloat(),
                    (halfZ * INNER_LAYER_SCALE).toFloat(),
                    scaleAlpha(lowColor, INNER_LAYER_ALPHA),
                    scaleAlpha(highColor, INNER_LAYER_ALPHA)
                )
            }
        }
        submitLightning(
            state,
            poseStack,
            submitNodeCollector,
            camera,
            centerX,
            centerY,
            centerZ,
            fullHalfX.toFloat(),
            fullHalfY.toFloat(),
            fullHalfZ.toFloat()
        )
        poseStack.popPose()
    }

    override fun shouldRenderOffScreen(): Boolean = true

    override fun getViewDistance(): Int = VIEW_DISTANCE

    private fun renderBox(
        pose: PoseStack.Pose,
        consumer: VertexConsumer,
        minX: Float,
        minY: Float,
        minZ: Float,
        maxX: Float,
        maxY: Float,
        maxZ: Float,
        lowColor: Int,
        highColor: Int
    ) {
        quad(pose, consumer, lowColor, lowColor, lowColor, lowColor,
            minX, minY, minZ, maxX, minY, minZ, maxX, minY, maxZ, minX, minY, maxZ)
        quad(pose, consumer, highColor, highColor, highColor, highColor,
            minX, maxY, maxZ, maxX, maxY, maxZ, maxX, maxY, minZ, minX, maxY, minZ)

        quad(pose, consumer, lowColor, lowColor, highColor, highColor,
            minX, minY, minZ, minX, minY, maxZ, minX, maxY, maxZ, minX, maxY, minZ)
        quad(pose, consumer, lowColor, lowColor, highColor, highColor,
            maxX, minY, maxZ, maxX, minY, minZ, maxX, maxY, minZ, maxX, maxY, maxZ)
        quad(pose, consumer, lowColor, lowColor, highColor, highColor,
            maxX, minY, minZ, minX, minY, minZ, minX, maxY, minZ, maxX, maxY, minZ)
        quad(pose, consumer, lowColor, lowColor, highColor, highColor,
            minX, minY, maxZ, maxX, minY, maxZ, maxX, maxY, maxZ, minX, maxY, maxZ)
    }

    private fun quad(
        pose: PoseStack.Pose,
        consumer: VertexConsumer,
        color1: Int,
        color2: Int,
        color3: Int,
        color4: Int,
        x1: Float,
        y1: Float,
        z1: Float,
        x2: Float,
        y2: Float,
        z2: Float,
        x3: Float,
        y3: Float,
        z3: Float,
        x4: Float,
        y4: Float,
        z4: Float
    ) {
        consumer.addVertex(pose, x1, y1, z1).setColor(color1)
        consumer.addVertex(pose, x2, y2, z2).setColor(color2)
        consumer.addVertex(pose, x3, y3, z3).setColor(color3)
        consumer.addVertex(pose, x4, y4, z4).setColor(color4)
    }

    private fun scaleAlpha(color: Int, scale: Double): Int {
        val alpha = (((color ushr 24) and 0xFF) * scale).toInt().coerceIn(0, 0xFF)
        return (color and 0x00FFFFFF) or (alpha shl 24)
    }

    private fun submitLightning(
        state: EnrichmentChamberControllerRenderState,
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        camera: CameraRenderState,
        centerX: Double,
        centerY: Double,
        centerZ: Double,
        halfX: Float,
        halfY: Float,
        halfZ: Float
    ) {
        val period = when {
            state.overflowing -> OVERFLOW_LIGHTNING_PERIOD
            state.fullyCharged -> FULL_LIGHTNING_PERIOD
            else -> NORMAL_LIGHTNING_PERIOD
        }
        val duration = when {
            state.overflowing -> OVERFLOW_LIGHTNING_DURATION
            state.fullyCharged -> FULL_LIGHTNING_DURATION
            else -> NORMAL_LIGHTNING_DURATION
        }
        val cycle = floor(state.animationTicks / period).toLong()
        val cycleRandom = Random(state.blockPos.asLong() xor (cycle * LIGHTNING_SEED_STEP))
        val actualDuration = duration * (
            MIN_DURATION_SCALE + cycleRandom.nextDouble() * (MAX_DURATION_SCALE - MIN_DURATION_SCALE)
        )
        val flashStart = cycleRandom.nextDouble() * (period - actualDuration).coerceAtLeast(0.0)
        val age = state.animationTicks - cycle * period - flashStart
        if (age !in 0.0..actualDuration) return

        val lifetime = sin(PI * age / actualDuration).coerceAtLeast(0.0)
        val maxBoltCount = when {
            state.overflowing -> OVERFLOW_BOLT_COUNT
            state.fullyCharged -> FULL_BOLT_COUNT
            else -> NORMAL_BOLT_COUNT
        }
        val boltCount = selectBoltCount(cycleRandom, maxBoltCount)
        val cameraLocal = Vector3f(
            (camera.pos.x - (state.blockPos.x + centerX)).toFloat(),
            (camera.pos.y - (state.blockPos.y + centerY)).toFloat(),
            (camera.pos.z - (state.blockPos.z + centerZ)).toFloat()
        )
        val minimumSize = minOf(halfX, halfY, halfZ) * 2F
        val baseThickness = (minimumSize * 0.006F).coerceIn(0.008F, 0.035F)

        submitNodeCollector.submitCustomGeometry(poseStack, RenderTypes.lightning()) { pose, consumer ->
            repeat(boltCount) { boltIndex ->
                val seed = state.blockPos.asLong() xor (cycle * LIGHTNING_SEED_STEP) xor boltIndex.toLong()
                val boltRandom = Random(seed)
                val intensity = lifetime * (
                    MIN_LIGHTNING_INTENSITY +
                        boltRandom.nextDouble() * (MAX_LIGHTNING_INTENSITY - MIN_LIGHTNING_INTENSITY)
                    )
                val thickness = baseThickness * (
                    MIN_THICKNESS_SCALE +
                        boltRandom.nextFloat() * (MAX_THICKNESS_SCALE - MIN_THICKNESS_SCALE)
                    )
                val outerColor = scaleAlpha(
                    if (state.overflowing) OVERFLOW_LIGHTNING_OUTER_COLOR else NORMAL_LIGHTNING_OUTER_COLOR,
                    intensity
                )
                val coreColor = scaleAlpha(
                    if (state.overflowing) OVERFLOW_LIGHTNING_CORE_COLOR else NORMAL_LIGHTNING_CORE_COLOR,
                    intensity
                )
                val points = createLightningPoints(
                    boltRandom,
                    halfX,
                    halfY,
                    halfZ,
                    state.overflowing
                )
                renderLightning(
                    pose,
                    consumer,
                    points,
                    cameraLocal,
                    thickness,
                    outerColor,
                    coreColor
                )
            }
        }
    }

    private fun selectBoltCount(random: Random, maximum: Int): Int {
        if (maximum <= 1) return 1

        val roll = random.nextFloat()
        return when {
            roll < SINGLE_BOLT_CHANCE -> 1
            maximum == 2 || roll < DOUBLE_BOLT_CHANCE -> 2
            else -> 3
        }
    }

    private fun createLightningPoints(
        random: Random,
        halfX: Float,
        halfY: Float,
        halfZ: Float,
        overflowing: Boolean
    ): List<Vector3f> {
        fun randomCoordinate(halfExtent: Float, scale: Float = 1F): Float =
            (random.nextFloat() * 2F - 1F) * halfExtent * scale

        val start = Vector3f(
            randomCoordinate(halfX, LIGHTNING_SPAWN_SCALE),
            randomCoordinate(halfY, LIGHTNING_SPAWN_SCALE),
            randomCoordinate(halfZ, LIGHTNING_SPAWN_SCALE)
        )
        val direction = Vector3f(
            randomCoordinate(1F),
            randomCoordinate(1F),
            randomCoordinate(1F)
        )
        if (direction.lengthSquared() < 0.000001F) {
            direction.set(0F, 1F, 0F)
        }
        val maximumLength = (minOf(halfX, halfY, halfZ) * 2F * LOCAL_LIGHTNING_SCALE)
            .coerceIn(MIN_LIGHTNING_LENGTH, MAX_LIGHTNING_LENGTH)
        val length = (
            maximumLength * (
                MIN_LENGTH_SCALE + random.nextFloat() * (MAX_LENGTH_SCALE - MIN_LENGTH_SCALE)
            )
        ).coerceAtLeast(MIN_LIGHTNING_LENGTH)
        val end = direction.normalize(length).add(start)
        end.x = end.x.coerceIn(-halfX * LIGHTNING_BOUNDS_SCALE, halfX * LIGHTNING_BOUNDS_SCALE)
        end.y = end.y.coerceIn(-halfY * LIGHTNING_BOUNDS_SCALE, halfY * LIGHTNING_BOUNDS_SCALE)
        end.z = end.z.coerceIn(-halfZ * LIGHTNING_BOUNDS_SCALE, halfZ * LIGHTNING_BOUNDS_SCALE)

        val actualLength = start.distance(end)
        val segmentCount = (actualLength / LIGHTNING_SEGMENT_LENGTH).toInt().coerceIn(3, 10)
        val jitterScale = if (overflowing) OVERFLOW_JITTER_SCALE else NORMAL_JITTER_SCALE
        val jitter = (actualLength * jitterScale).coerceIn(MIN_LIGHTNING_JITTER, MAX_LIGHTNING_JITTER)

        return List(segmentCount + 1) { index ->
            val progress = index.toFloat() / segmentCount
            val envelope = sin(PI * progress).toFloat()
            Vector3f(
                (start.x + (end.x - start.x) * progress + randomCoordinate(jitter) * envelope)
                    .coerceIn(-halfX * LIGHTNING_BOUNDS_SCALE, halfX * LIGHTNING_BOUNDS_SCALE),
                (start.y + (end.y - start.y) * progress + randomCoordinate(jitter) * envelope)
                    .coerceIn(-halfY * LIGHTNING_BOUNDS_SCALE, halfY * LIGHTNING_BOUNDS_SCALE),
                (start.z + (end.z - start.z) * progress + randomCoordinate(jitter) * envelope)
                    .coerceIn(-halfZ * LIGHTNING_BOUNDS_SCALE, halfZ * LIGHTNING_BOUNDS_SCALE)
            )
        }
    }

    private fun renderLightning(
        pose: PoseStack.Pose,
        consumer: VertexConsumer,
        points: List<Vector3f>,
        cameraLocal: Vector3f,
        thickness: Float,
        outerColor: Int,
        coreColor: Int
    ) {
        points.zipWithNext().forEach { (start, end) ->
            renderLightningSegment(
                pose,
                consumer,
                start,
                end,
                cameraLocal,
                thickness * 2.4F,
                outerColor
            )
            renderLightningSegment(
                pose,
                consumer,
                start,
                end,
                cameraLocal,
                thickness,
                coreColor
            )
        }
    }

    private fun renderLightningSegment(
        pose: PoseStack.Pose,
        consumer: VertexConsumer,
        start: Vector3f,
        end: Vector3f,
        cameraLocal: Vector3f,
        thickness: Float,
        color: Int
    ) {
        val middle = Vector3f(start).add(end).mul(0.5F)
        val viewDirection = Vector3f(cameraLocal).sub(middle)
        val side = Vector3f(end).sub(start).cross(viewDirection)
        if (side.lengthSquared() < 0.000001F) side.set(0F, 1F, 0F)
        side.normalize(thickness)

        quad(
            pose,
            consumer,
            color,
            color,
            color,
            color,
            start.x - side.x,
            start.y - side.y,
            start.z - side.z,
            end.x - side.x,
            end.y - side.y,
            end.z - side.z,
            end.x + side.x,
            end.y + side.y,
            end.z + side.z,
            start.x + side.x,
            start.y + side.y,
            start.z + side.z
        )
        quad(
            pose,
            consumer,
            color,
            color,
            color,
            color,
            start.x + side.x,
            start.y + side.y,
            start.z + side.z,
            end.x + side.x,
            end.y + side.y,
            end.z + side.z,
            end.x - side.x,
            end.y - side.y,
            end.z - side.z,
            start.x - side.x,
            start.y - side.y,
            start.z - side.z
        )
    }

    companion object {
        private const val BOUNDS_INSET = 0.02
        private const val VIEW_DISTANCE = 256
        private const val MIDDLE_LAYER_SCALE = 0.7
        private const val MIDDLE_LAYER_ALPHA = 0.48
        private const val INNER_LAYER_SCALE = 0.4
        private const val INNER_LAYER_ALPHA = 0.3
        private const val NORMAL_LIGHTNING_PERIOD = 160.0
        private const val NORMAL_LIGHTNING_DURATION = 8.0
        private const val FULL_LIGHTNING_PERIOD = 110.0
        private const val FULL_LIGHTNING_DURATION = 9.0
        private const val OVERFLOW_LIGHTNING_PERIOD = 75.0
        private const val OVERFLOW_LIGHTNING_DURATION = 11.0
        private const val NORMAL_BOLT_COUNT = 1
        private const val FULL_BOLT_COUNT = 2
        private const val OVERFLOW_BOLT_COUNT = 3
        private const val SINGLE_BOLT_CHANCE = 0.75F
        private const val DOUBLE_BOLT_CHANCE = 0.95F
        private const val MIN_DURATION_SCALE = 0.7
        private const val MAX_DURATION_SCALE = 1.25
        private const val MIN_LIGHTNING_INTENSITY = 0.35
        private const val MAX_LIGHTNING_INTENSITY = 1.0
        private const val MIN_THICKNESS_SCALE = 0.55F
        private const val MAX_THICKNESS_SCALE = 1.15F
        private const val LIGHTNING_SPAWN_SCALE = 0.78F
        private const val LIGHTNING_BOUNDS_SCALE = 0.92F
        private const val LOCAL_LIGHTNING_SCALE = 0.2F
        private const val MIN_LIGHTNING_LENGTH = 0.25F
        private const val MAX_LIGHTNING_LENGTH = 1.35F
        private const val MIN_LENGTH_SCALE = 0.3F
        private const val MAX_LENGTH_SCALE = 1.0F
        private const val LIGHTNING_SEGMENT_LENGTH = 0.22F
        private const val NORMAL_JITTER_SCALE = 0.09F
        private const val OVERFLOW_JITTER_SCALE = 0.15F
        private const val MIN_LIGHTNING_JITTER = 0.025F
        private const val MAX_LIGHTNING_JITTER = 0.22F
        private const val LIGHTNING_SEED_STEP = -7046029254386353131L

        private const val NORMAL_LOW_COLOR = 0x6050127A
        private const val NORMAL_HIGH_COLOR = 0x608B00FF
        private const val OVERFLOW_LOW_COLOR = 0x600A3696
        private const val OVERFLOW_HIGH_COLOR = 0x6000AEFF
        private const val NORMAL_LIGHTNING_OUTER_COLOR = 0x306620D9
        private const val NORMAL_LIGHTNING_CORE_COLOR = 0x70E2B0FF
        private const val OVERFLOW_LIGHTNING_OUTER_COLOR = 0x300078D9
        private const val OVERFLOW_LIGHTNING_CORE_COLOR = 0x70D8F8FF

        private val ENERGY_RENDER_TYPE: RenderType by lazy {
            val pipeline = RenderPipelinesAccessor.ecrRegister(
                RenderPipeline.builder(RenderPipelinesAccessor.ecrDebugFilledSnippet())
                    .withLocation("pipeline/enrichment_chamber_energy".ecRL)
                    .withVertexShader("core/enrichment_chamber_energy".ecRL)
                    .withFragmentShader("core/enrichment_chamber_energy".ecRL)
                    .build()
            )

            RenderTypeAccessor.ecrCreate(
                "ecreimagined_enrichment_chamber_energy",
                RenderSetup.builder(pipeline)
                    .sortOnUpload()
                    .createRenderSetup()
            )
        }
    }
}
