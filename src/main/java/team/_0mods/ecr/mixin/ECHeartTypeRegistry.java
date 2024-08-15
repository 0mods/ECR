package team._0mods.ecr.mixin;

import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import team._0mods.ecr.api.PlayerHeartType;

import java.util.ArrayList;
import java.util.Arrays;

@Mixin(Gui.HeartType.class)
public class ECHeartTypeRegistry {
    @Shadow(remap = false) @Final @Mutable private static Gui.HeartType[] $VALUES;

    @Invoker("<init>")
    public static Gui.HeartType ecr$invoke(String internalName, int internalId, int index, boolean canBlink) { throw new AssertionError(); }

    @Unique
    private static Gui.HeartType ecr$addVariant(String internalName, int index, boolean canBlink) {
        var entries = new ArrayList<>(Arrays.asList($VALUES));
        var v = ecr$invoke(internalName, entries.get(entries.size() - 1).ordinal() + 1, index, canBlink);
        entries.add(v);
        ECHeartTypeRegistry.$VALUES = entries.toArray(new Gui.HeartType[0]);
        return v;
    }

    static {
        PlayerHeartType.setReg$EC_Reimagined(ECHeartTypeRegistry::ecr$addVariant);
    }
}
