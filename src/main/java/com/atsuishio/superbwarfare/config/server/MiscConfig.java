package com.atsuishio.superbwarfare.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class MiscConfig {

    public static ForgeConfigSpec.BooleanValue ALLOW_TACTICAL_SPRINT;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("misc");

        builder.comment("Set true to enable tactical sprint");
        ALLOW_TACTICAL_SPRINT = builder.define("allow_tactical_sprint", true);

        builder.pop();
    }
}
