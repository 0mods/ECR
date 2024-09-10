package team._0mods.ecr.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.hollowhorizon.hc.client.utils.ForgeKotlinKt;
import team._0mods.ecr.api.PlayerHeartType;
import team._0mods.ecr.common.capability.PlayerMRU;

@Mixin(Gui.HeartType.class)
public class GuiHeartTypeMixin {
    @Inject(method = "forPlayer", at = @At("HEAD"), cancellable = true)
    private static void fp(Player player, CallbackInfoReturnable<Gui.HeartType> cir) {
        if (FMLEnvironment.production) {
            var cap = ForgeKotlinKt.get(player, PlayerMRU.class);
            if (cap.isInfused()) {
                cir.setReturnValue(PlayerHeartType.getRadiationInfused());
            }
        }
    }
}
