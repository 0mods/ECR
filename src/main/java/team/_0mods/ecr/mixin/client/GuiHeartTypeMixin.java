package team._0mods.ecr.mixin.client;

import net.minecraft.client.gui.Gui;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import ru.hollowhorizon.hc.client.utils.ForgeKotlinKt;
import team._0mods.ecr.api.PlayerHeartType;
import team._0mods.ecr.api.PlayerHeartTypeKt;
import team._0mods.ecr.common.capability.PlayerMRU;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(Gui.HeartType.class)
public class GuiHeartTypeMixin {
    @Shadow(remap = false) @Final
    @Mutable
    private static Gui.HeartType[] $VALUES;

    @Invoker("<init>")
    public static Gui.HeartType ecr$invoke(String internalName, int internalId, int index, boolean canBlink) { throw new AssertionError(); }

    @Inject(method = "forPlayer", at = @At("HEAD"), cancellable = true)
    private static void fp(Player player, CallbackInfoReturnable<Gui.HeartType> cir) {
        var cap = ForgeKotlinKt.get(player, PlayerMRU.class);
        if (cap.isInfused()) {
            cir.setReturnValue(PlayerHeartType.getRadiationInfused());
        }
    }

    @Unique
    private static Gui.HeartType ecr$addVariant(String internalName, boolean canBlink) {
        var entries = new ArrayList<>(Arrays.asList($VALUES));
        var v = ecr$invoke(internalName, entries.get(entries.size() - 1).ordinal() + 1, entries.get(entries.size() - 1).ordinal() + 1, canBlink);
        entries.add(v);
        $VALUES = entries.toArray(new Gui.HeartType[0]);
        return v;
    }

    static {
        PlayerHeartTypeKt.setReg(GuiHeartTypeMixin::ecr$addVariant);
    }
}
