package online.keriils.supertravelstaff.main;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import online.keriils.supertravelstaff.config.CommonConfig;
import online.keriils.supertravelstaff.config.MixinConfig;
import online.keriils.supertravelstaff.handler.EventHandler;
import online.keriils.supertravelstaff.handler.NetworkHandler;
import online.keriils.supertravelstaff.integration.ModStatus;
import online.keriils.supertravelstaff.item.SuperTravelStaffItem;

@SuppressWarnings("unused")
@Mod(SuperTravelStaff.MODID)
public class SuperTravelStaff {

    public static final String MODID = "supertravelstaff";
    public static final Logger LOGGER = LogUtils.getLogger();

    // Deferred Register
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TAB = DeferredRegister
        .create(Registries.CREATIVE_MODE_TAB, MODID);

    // Define Staff Item
    public static final String SUPER_TRAVEL_STAFF_NAME = "super_travel_staff";
    public static final RegistryObject<SuperTravelStaffItem> SuperStaffItem = ITEMS
        .register(SUPER_TRAVEL_STAFF_NAME, SuperTravelStaffItem::new);

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_TAB.register(
        "super_travel_staff_tab",
        () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(
                () -> SuperStaffItem.get()
                    .getDefaultInstance())
            .displayItems((parameters, output) -> output.accept(SuperStaffItem.get()))
            .build());

    public SuperTravelStaff() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get()
            .getModEventBus();
        ITEMS.register(modEventBus);
        CREATIVE_TAB.register(modEventBus);
        modEventBus.addListener(this::setup);
        ModLoadingContext.get()
            .registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "supertravelstaff/common_cfg.toml");
        if (shouldRegisterEvent()) MinecraftForge.EVENT_BUS.register(new EventHandler());
    }

    private void setup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkHandler::registerMessages);
    }

    private boolean shouldRegisterEvent() {
        var enderIO = MixinConfig.INSTANCE.withEnderIO.value() && ModStatus.isEnderIOLoaded;
        var travelAnchors = MixinConfig.INSTANCE.withTravelAnchors.value() && ModStatus.isTravelAnchorsLoaded;
        return enderIO || travelAnchors;
    }
}
