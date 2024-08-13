package team._0mods.ecr.common.effects

import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectCategory
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import team._0mods.ecr.common.init.registry.ECDamageSources
import java.awt.Color

class MRUCorruption: MobEffect(MobEffectCategory.HARMFUL, Color.magenta.rgb) {
    override fun applyEffectTick(livingEntity: LivingEntity, amplifier: Int) {
        if (!livingEntity.commandSenderWorld.isClientSide && livingEntity is Player) {
            if (livingEntity.hasEffect(MobEffects.REGENERATION))
                livingEntity.removeEffect(MobEffects.REGENERATION)
            livingEntity.hurt(ECDamageSources.MRU, if (amplifier > 0) 2.5f * amplifier + amplifier else 2.5f)
        }
    }

    override fun isDurationEffectTick(duration: Int, amplifier: Int): Boolean = true
}
