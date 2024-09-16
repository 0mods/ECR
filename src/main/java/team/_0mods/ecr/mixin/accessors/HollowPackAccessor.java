package team._0mods.ecr.mixin.accessors;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import ru.hollowhorizon.hc.client.utils.HollowPack;

import java.io.InputStream;
import java.util.HashMap;

// TODO: Fix it
@Mixin(HollowPack.class)
public interface HollowPackAccessor {
    @Accessor("resourceMap") HashMap<ResourceLocation, Resource.IoSupplier<InputStream>> resourceMap();
}
