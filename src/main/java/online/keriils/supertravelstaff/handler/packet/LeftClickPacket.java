package online.keriils.supertravelstaff.handler.packet;

import static net.minecraft.world.InteractionHand.MAIN_HAND;
import static net.minecraft.world.InteractionHand.OFF_HAND;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import online.keriils.supertravelstaff.handler.StsTravelHandler;

public class LeftClickPacket {

    private final boolean isMainHand;

    public LeftClickPacket(boolean isMainHand) {
        this.isMainHand = isMainHand;
    }

    public static void encode(LeftClickPacket msg, FriendlyByteBuf byteBuf) {
        byteBuf.writeBoolean(msg.isMainHand);
    }

    public static LeftClickPacket decode(FriendlyByteBuf byteBuf) {
        return new LeftClickPacket(byteBuf.readBoolean());
    }

    public static void handle(LeftClickPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection()
                .getReceptionSide()
                .isServer()) {
                ServerPlayer sender = context.getSender();
                if (sender == null) return;
                Level level = sender.level();
                StsTravelHandler.performAction(sender, level, msg.isMainHand ? MAIN_HAND : OFF_HAND);
            }
        });
        context.setPacketHandled(true);
    }
}
