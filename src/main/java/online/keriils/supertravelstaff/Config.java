package online.keriils.supertravelstaff;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {

    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static ForgeConfigSpec.BooleanValue shouldFireTeleportEvent;
    public static ForgeConfigSpec.IntValue maxDistance;
    public static ForgeConfigSpec.IntValue coolDownTick;

    static {
        shouldFireTeleportEvent = BUILDER.comment("Should fire teleport event")
            .define("shouldFire", true);
        maxDistance = BUILDER.comment("The maximum distance you are allowed to teleport.")
            .defineInRange("maxDistance", 256, 1, Integer.MAX_VALUE);
        coolDownTick = BUILDER.comment("Staff cool down tick")
            .defineInRange("coolDownTick", 3, 1, Integer.MAX_VALUE);
        SPEC = BUILDER.build();
    }

}
