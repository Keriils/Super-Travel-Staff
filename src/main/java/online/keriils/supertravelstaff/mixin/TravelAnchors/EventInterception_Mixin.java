package online.keriils.supertravelstaff.mixin.TravelAnchors;

import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import de.castcrafter.travelanchors.EventListener;
import online.keriils.supertravelstaff.main.SuperTravelStaff;

@Mixin(value = EventListener.class, remap = false)
public class EventInterception_Mixin {

    @Inject(
        method = "onRightClick",
        at = @At(
            value = "INVOKE_ASSIGN",
            target = "Lnet/minecraftforge/event/entity/player/PlayerInteractEvent$RightClickItem;getEntity()Lnet/minecraft/world/entity/player/Player;"),
        require = 1,
        cancellable = true)
    private void STS$ban(PlayerInteractEvent.RightClickItem event, CallbackInfo ci) {
        if (event.getEntity()
            .getItemInHand(event.getHand())
            .getItem() == SuperTravelStaff.SuperStaffItem.get()) ci.cancel();
    }
}
