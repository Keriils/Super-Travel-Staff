package online.keriils.supertravelstaff.handler;

import static net.minecraft.world.InteractionHand.MAIN_HAND;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import online.keriils.supertravelstaff.handler.packet.LeftClickPacket;
import online.keriils.supertravelstaff.main.SuperTravelStaff;

public class EventHandler {

    @SubscribeEvent
    public void leftClickAir(PlayerInteractEvent.LeftClickEmpty event) {
        ItemStack itemInHand = event.getItemStack();
        if (itemInHand == ItemStack.EMPTY) return;
        if (itemInHand.getItem() != SuperTravelStaff.SuperStaffItem.get()) return;
        NetworkHandler.INSTANCE.sendToServer(new LeftClickPacket(event.getHand() == MAIN_HAND));
    }

    @SubscribeEvent
    public void leftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        ItemStack itemInHand = event.getItemStack();
        if (itemInHand == ItemStack.EMPTY) return;
        if (itemInHand.getItem() != SuperTravelStaff.SuperStaffItem.get()) return;
        if (StsTravelHandler.performAction(event.getEntity(), event.getLevel(), event.getHand())) {
            event.setCanceled(true);
        }
    }

}
