package online.keriils.supertravelstaff.mixin.TravelAnchors;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import de.castcrafter.travelanchors.TeleportHandler;
import online.keriils.supertravelstaff.config.CommonConfig;
import online.keriils.supertravelstaff.item.SuperTravelStaffItem;
import online.keriils.supertravelstaff.main.SuperTravelStaff;

@Mixin(value = TeleportHandler.class, remap = false)
public class StaffItemHandler_Mixin {

    @Inject(method = "canItemTeleport", at = @At("TAIL"), require = 1, cancellable = true)
    private static void STS$handler(Player player, InteractionHand hand, CallbackInfoReturnable<Boolean> cir) {
        if (player.getItemInHand(hand)
            .getItem() == SuperTravelStaff.SuperStaffItem.get()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(
        method = "getMaxDistance",
        at = @At("TAIL"),
        require = 1,
        locals = LocalCapture.CAPTURE_FAILHARD,
        cancellable = true)
    private static void STS$distance(Player player, CallbackInfoReturnable<Double> cir, int lvl) {
        if (STS$isSSI(player)) cir.setReturnValue(((double) CommonConfig.maxDistance.get()) * (1 + (lvl / 2d)));
    }

    @Unique
    private static boolean STS$isSSI(Player player) {
        SuperTravelStaffItem superTravelStaffItem = SuperTravelStaff.SuperStaffItem.get();
        boolean main = player.getItemInHand(InteractionHand.MAIN_HAND)
            .getItem() == superTravelStaffItem;
        boolean off = player.getItemInHand(InteractionHand.OFF_HAND)
            .getItem() == superTravelStaffItem;
        return main || off;
    }
}
