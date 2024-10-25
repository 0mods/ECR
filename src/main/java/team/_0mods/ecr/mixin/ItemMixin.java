package team._0mods.ecr.mixin;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import team._0mods.ecr.api.ECConstantsKt;
import team._0mods.ecr.api.item.BoundGem;
import team._0mods.ecr.api.mru.MRUGenerator;

import static ru.hollowhorizon.hc.client.utils.ForgeKotlinKt.mcTranslate;

@Mixin(Item.class)
public class ItemMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void use(Level level, Player player, InteractionHand usedHand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        var item = player.getItemInHand(usedHand);

        if (!(item.getItem() instanceof BoundGem bg)) return;
        if (!player.isShiftKeyDown()) return;
        if (bg.getBoundPos(item) == null) return;

        player.displayClientMessage(mcTranslate(String.format("tooltip.%s.bound_gem.unbound", ECConstantsKt.ModId)), true);
        bg.setBoundPos(item, null);
        cir.setReturnValue(InteractionResultHolder.success(item));
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    public void useOn(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        var player = context.getPlayer();
        if (player == null) return;
        var stack = context.getItemInHand();
        var level = context.getLevel();
        var pos = context.getClickedPos();

        var blockEntity = level.getBlockEntity(pos);

        if (!(stack.getItem() instanceof BoundGem bg)) return;
        if (!(blockEntity instanceof MRUGenerator)) return;
        if (bg.getBoundPos(stack) != null) return;

        var builder = "X:" + ' ' + pos.getX() + ' ' +
                "Y:" + ' ' + pos.getY() + ' ' +
                "Z:" + ' ' + pos.getZ();

        player.displayClientMessage(mcTranslate(String.format("tooltip.%s.bound_gem.bound", ECConstantsKt.ModId), builder), true);
        if (stack.getCount() > 1) {
            var copiedStack = stack.copy();
            copiedStack.setCount(1);
            bg.setBoundPos(copiedStack, pos);
            stack.shrink(1);
            var ent = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), copiedStack);
            ent.setNoPickUpDelay();
            ent.setThrower(player.getUUID());
            level.addFreshEntity(ent);
        } else bg.setBoundPos(stack, pos);

        cir.setReturnValue(InteractionResult.SUCCESS);
    }
}
