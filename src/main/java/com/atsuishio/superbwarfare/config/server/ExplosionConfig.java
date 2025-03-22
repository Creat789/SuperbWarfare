package com.atsuishio.superbwarfare.config.server;

import net.minecraftforge.common.ForgeConfigSpec;

public class ExplosionConfig {

    public static ForgeConfigSpec.IntValue EXPLOSION_PENETRATION_RATIO;
    public static ForgeConfigSpec.BooleanValue EXPLOSION_DESTROY;

    public static ForgeConfigSpec.IntValue RGO_GRENADE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue RGO_GRENADE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue M67_GRENADE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue M67_GRENADE_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue MORTAR_SHELL_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue MORTAR_SHELL_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue DRONE_KAMIKAZE_HIT_DAMAGE;
    public static ForgeConfigSpec.IntValue DRONE_KAMIKAZE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue DRONE_KAMIKAZE_EXPLOSION_RADIUS;
    public static ForgeConfigSpec.IntValue DRONE_KAMIKAZE_HIT_DAMAGE_C4;

    public static ForgeConfigSpec.IntValue C4_EXPLOSION_COUNTDOWN;
    public static ForgeConfigSpec.IntValue C4_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue C4_EXPLOSION_RADIUS;

    public static ForgeConfigSpec.IntValue WIRE_GUIDE_MISSILE_DAMAGE;
    public static ForgeConfigSpec.IntValue WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE;
    public static ForgeConfigSpec.IntValue WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS;

    public static void init(ForgeConfigSpec.Builder builder) {
        builder.push("explosion");

        builder.comment("The percentage of explosion damage you take behind cover");
        EXPLOSION_PENETRATION_RATIO = builder.defineInRange("explosion_penetration_ratio", 15, 0, 100);

        builder.comment("Set true to allow Explosion to destroy blocks");
        EXPLOSION_DESTROY = builder.define("explosion_destroy", true);

        builder.push("RGO Grenade");

        builder.comment("The explosion damage of RGO grenade");
        RGO_GRENADE_EXPLOSION_DAMAGE = builder.defineInRange("rgo_grenade_explosion_damage", 90, 1, 10000000);

        builder.comment("The explosion radius of RGO grenade");
        RGO_GRENADE_EXPLOSION_RADIUS = builder.defineInRange("rgo_grenade_explosion_radius", 5, 1, 50);

        builder.pop();


        builder.push("M67 Grenade");

        builder.comment("The explosion damage of M67 grenade");
        M67_GRENADE_EXPLOSION_DAMAGE = builder.defineInRange("m67_grenade_explosion_damage", 120, 1, 10000000);

        builder.comment("The explosion radius of M67 grenade");
        M67_GRENADE_EXPLOSION_RADIUS = builder.defineInRange("m67_grenade_explosion_radius", 6, 1, 50);

        builder.pop();


        builder.push("Mortar Shell");

        builder.comment("The explosion damage of Mortar shell");
        MORTAR_SHELL_EXPLOSION_DAMAGE = builder.defineInRange("mortar_shell_explosion_damage", 160, 1, 10000000);

        builder.comment("The explosion radius of Mortar shell");
        MORTAR_SHELL_EXPLOSION_RADIUS = builder.defineInRange("mortar_shell_explosion_radius", 9, 1, 50);

        builder.pop();

        builder.push("Drone Kamikaze");

        builder.comment("The hit damage of Drone Kamikaze");
        DRONE_KAMIKAZE_HIT_DAMAGE = builder.defineInRange("drone_kamikaze_hit_damage", 200, 1, 10000000);

        builder.comment("The hit damage of Drone Kamikaze with C4");
        DRONE_KAMIKAZE_HIT_DAMAGE_C4 = builder.defineInRange("drone_kamikaze_hit_damage_c4", 150, 1, 10000000);

        builder.comment("The explosion damage of Drone Kamikaze");
        DRONE_KAMIKAZE_EXPLOSION_DAMAGE = builder.defineInRange("drone_kamikaze_explosion_damage", 160, 1, 10000000);

        builder.comment("The explosion radius of Drone Kamikaze");
        DRONE_KAMIKAZE_EXPLOSION_RADIUS = builder.defineInRange("drone_kamikaze_explosion_radius", 9, 1, 50);

        builder.pop();

        builder.push("C4");

        builder.comment("The explosion damage of C4");
        C4_EXPLOSION_DAMAGE = builder.defineInRange("c4_explosion_damage", 300, 1, Integer.MAX_VALUE);

        builder.comment("The explosion countdown of C4");
        C4_EXPLOSION_COUNTDOWN = builder.defineInRange("c4_explosion_countdown", 514, 1, Integer.MAX_VALUE);

        builder.comment("The explosion radius of C4");
        C4_EXPLOSION_RADIUS = builder.defineInRange("c4_explosion_radius", 14, 1, Integer.MAX_VALUE);

        builder.pop();

        builder.push("Wire Guide Missile");

        builder.comment("The damage of wire guide missile");
        WIRE_GUIDE_MISSILE_DAMAGE = builder.defineInRange("wire_guide_missile_damage", 700, 1, Integer.MAX_VALUE);

        builder.comment("The explosion damage of wire guide missile");
        WIRE_GUIDE_MISSILE_EXPLOSION_DAMAGE = builder.defineInRange("wire_guide_missile_explosion_damage", 60, 1, Integer.MAX_VALUE);

        builder.comment("The explosion radius of wre guide missile");
        WIRE_GUIDE_MISSILE_EXPLOSION_RADIUS = builder.defineInRange("wire_guide_missile_explosion_radius", 6, 1, Integer.MAX_VALUE);

        builder.pop();

        builder.pop();
    }
}
