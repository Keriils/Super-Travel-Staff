package online.keriils.supertravelstaff.integration;

import net.minecraftforge.fml.ModList;

public final class ModStatus {

    public static final boolean isEnderIOLoaded = ModList.get()
        .isLoaded("enderio");

    public static final boolean isTravelAnchorsLoaded = ModList.get()
        .isLoaded("travelanchors");
}
