package com.algorithmlx.ecr.mixin;

import com.algorithmlx.ecr.common.components.SoulStoneComponent;
import com.algorithmlx.ecr.common.init.Registry;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ResultSlot.class)
public class ResultSlotMixin {
    @Inject(method = "onTake", at = @At("HEAD"))
    private void onTake(Player player, ItemStack carried, CallbackInfo ci) {
        if (carried.is(Registry.getInstance().getSoulStone())) {
            var playerUUID = player.getUUID();
            var playerName = player.getName().getString();

            carried.applyComponents(
                DataComponentPatch.builder()
                    .set(
                        Registry.getInstance().getSoulStoneComponent(),
                        new SoulStoneComponent(playerUUID, playerName, 0)
                    ).build()
            );
        }
    }
}
