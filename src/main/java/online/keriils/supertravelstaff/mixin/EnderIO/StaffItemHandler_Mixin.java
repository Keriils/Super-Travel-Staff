package online.keriils.supertravelstaff.mixin.EnderIO;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.enderio.base.common.handler.TravelHandler;

import online.keriils.supertravelstaff.main.SuperTravelStaff;

@Mixin(value = TravelHandler.class, remap = false)
public class StaffItemHandler_Mixin {

    @Inject(
        method = "canItemTeleport(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/InteractionHand;)Z",
        at = @At("TAIL"),
        locals = LocalCapture.CAPTURE_FAILHARD,
        require = 1,
        cancellable = true)
    private static void STS$handler(Player player, InteractionHand hand, CallbackInfoReturnable<Boolean> cir,
        ItemStack stack) {
        if (stack.getItem() == SuperTravelStaff.SuperStaffItem.get()) {
            cir.setReturnValue(true);
        }
    }
}
