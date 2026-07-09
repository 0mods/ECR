package com.algorithmlx.ecr.mixin;

import com.algorithmlx.ecr.api.block.FullBlockParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {
    @ModifyVariable(method = "addDestroyBlockEffect", at = @At("STORE"), name = "shape")
    private VoxelShape lol(VoxelShape shape, BlockPos pos, BlockState blockState) {
        if (blockState.getBlock() instanceof FullBlockParticles fbp) {
            if (!fbp.isEnableForPart(blockState)) return Shapes.empty();
            return Shapes.block();
        }
        return shape;
    }
}
