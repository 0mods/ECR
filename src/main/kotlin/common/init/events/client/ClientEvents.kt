package team._0mods.ecr.common.init.events.client

import net.minecraft.ChatFormatting
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.SwordItem
import ru.hollowhorizon.hc.common.utils.literal
import ru.hollowhorizon.hc.common.utils.mcTranslate
import ru.hollowhorizon.hc.common.events.client.ItemTooltipEvent
import ru.hollowhorizon.hc.common.events.client.render.RegisterEntityLayersDefinitions
import ru.hollowhorizon.hc.common.events.registry.RegisterBlockEntityRenderersEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterKeyBindingsEvent
import ru.hollowhorizon.hc.common.events.registry.RegisterParticlesEvent
import team._0mods.ecr.api.ModId
import team._0mods.ecr.api.item.BoundGem
import team._0mods.ecr.api.mru.MRUHolder
import team._0mods.ecr.api.mru.MRUMultiplierWeapon
import team._0mods.ecr.client.keys.ECKeys
import team._0mods.ecr.client.particle.ECParticleFactory
import team._0mods.ecr.client.renderer.MatrixDestructorRenderer
import team._0mods.ecr.client.renderer.MithrilineFurnaceRenderer
import team._0mods.ecr.common.init.registry.ECRegistry
import ru.hollowhorizon.hc.common.events.SubscribeEvent as HCSubscribe

@HCSubscribe
fun onItemTooltip(e: ItemTooltipEvent) {
    val stack = e.itemStack
    val item = stack.item
    val tooltip = e.toolTip

    if (item is SwordItem && item is MRUMultiplierWeapon) {
        tooltip.add(
            Component.literal(" ").append("${item.multiplier}").append(" ").append(
            Component.translatable("tooltip.$ModId.sword_multiplier")
        ).withStyle(ChatFormatting.DARK_GREEN))
    }

    if (item is BoundGem) {
        val level = Minecraft.getInstance().level ?: return
        val pos = item.getBoundPos(stack) ?: return
        val blockEntity = pos.let { level.getBlockEntity(pos) }
        tooltip.add("tooltip.$ModId.bound_gem.linked.pos".mcTranslate.append(":").withStyle(ChatFormatting.GOLD))
        tooltip.add(
            "X".literal.withStyle(ChatFormatting.RED).append(": ")
                .append("${pos.x}".literal).append(" ")
                .append("Y".literal.withStyle(ChatFormatting.GREEN).append(": ")
                    .append("${pos.y}".literal).append(" "))
                .append(
                    "Z".literal.withStyle(ChatFormatting.BLUE).append(": ")
                        .append("${pos.z}".literal))
        )

        if (blockEntity == null || blockEntity !is MRUHolder || !blockEntity.holderType.isExporter) {
            tooltip.add("tooltip.$ModId.bound_gem.linked.not_mru".mcTranslate.withStyle(ChatFormatting.GOLD))
        }

        if (!item.dimensionalBounds)
            tooltip.add("tooltip.ecreimagined.bound_gem.dimension.disallowed".mcTranslate.withStyle(ChatFormatting.RED))
    }
}

@HCSubscribe
fun onKeyBindRegister(e: RegisterKeyBindingsEvent) {
    ECKeys.kbList.forEach(e::registerKeyMapping)
}

@HCSubscribe
fun onRenderRegister(e: RegisterBlockEntityRenderersEvent) {
    e.registerEntity(ECRegistry.mithrilineFurnaceEntity, ::MithrilineFurnaceRenderer)
    e.registerEntity(ECRegistry.matrixDestructorEntity, ::MatrixDestructorRenderer)
}

@HCSubscribe
fun onParticleRegister(e: RegisterParticlesEvent) {
    e.registerSpriteSet(ECRegistry.ecParticle, ::ECParticleFactory)
}

@HCSubscribe
fun onLayersRegister(e: RegisterEntityLayersDefinitions) {
    e.registerLayerDefinition(MithrilineFurnaceRenderer.MF_LAYER, MithrilineFurnaceRenderer::createBodyLayer)
}