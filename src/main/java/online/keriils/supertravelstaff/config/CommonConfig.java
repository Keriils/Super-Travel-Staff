package online.keriils.supertravelstaff.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue shouldFireTeleportEvent;
    public static ForgeConfigSpec.IntValue maxDistance;
    public static ForgeConfigSpec.IntValue coolDownTick;

    private static final String CATEGORY_COMMON = "common";

    static {
        BUILDER.push(CATEGORY_COMMON);
        shouldFireTeleportEvent = BUILDER.comment("Whether to fire a teleport event when teleporting.")
            .define("shouldFire", true);
        maxDistance = BUILDER.comment("The maximum distance allowed for teleportation.")
            .defineInRange("maxDistance", 256, 1, Integer.MAX_VALUE);
        coolDownTick = BUILDER.comment("The cooldown time before the staff can be used again.")
            .defineInRange("coolDownTick", 3, 1, Integer.MAX_VALUE);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
