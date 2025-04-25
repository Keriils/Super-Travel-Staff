package online.keriils.supertravelstaff.handler;

import static online.keriils.supertravelstaff.main.SuperTravelStaff.MODID;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import online.keriils.supertravelstaff.handler.packet.LeftClickPacket;

public class NetworkHandler {

    private static final String VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry
        .newSimpleChannel(new ResourceLocation(MODID, "event"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void registerMessages() {
        INSTANCE.registerMessage(
            0,
            LeftClickPacket.class,
            LeftClickPacket::encode,
            LeftClickPacket::decode,
            LeftClickPacket::handle);
    }

}
