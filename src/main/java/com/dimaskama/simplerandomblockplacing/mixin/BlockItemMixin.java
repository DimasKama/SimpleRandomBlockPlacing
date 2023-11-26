package com.dimaskama.simplerandomblockplacing.mixin;

import com.dimaskama.simplerandomblockplacing.client.SRBPMod;
import com.dimaskama.simplerandomblockplacing.client.config.SlotOption;
import it.unimi.dsi.fastutil.ints.IntDoubleImmutablePair;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {
    @Inject(
            method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
            at = @At("TAIL")
    )
    private void randomizeSelectedSlot(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (!context.getWorld().isClient || !cir.getReturnValue().isAccepted() || SRBPMod.CONFIG.enabled) return;
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        boolean bl = true;
        for (int i = 0; i < 9; i++) {
            if (i == player.getInventory().selectedSlot && SRBPMod.CONFIG.slots.get(i).enabled) {
                bl = false;
                break;
            }
        }
        if (bl) return;
        List<IntDoubleImmutablePair> list = new ArrayList<>();
        double sum = 0.0;
        for (int i = 0; i < 9; i++) {
            SlotOption o = SRBPMod.CONFIG.slots.get(i);
            if (!o.enabled || o.chance <= 0.0) continue;
            ItemStack stack = player.getInventory().getStack(i);
            if (stack.isEmpty() || !(stack.getItem() instanceof BlockItem)) continue;
            list.add(new IntDoubleImmutablePair(i, sum += o.chance));
        }
        if (list.size() < 2) return;
        double rand = player.getRandom().nextDouble() * sum;
        for (IntDoubleImmutablePair pair : list) {
            if (rand < pair.rightDouble()) {
                player.getInventory().selectedSlot = pair.leftInt();
                return;
            }
        }
    }
}
