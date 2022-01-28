package com.cleanroommc.orangecore;

import net.minecraftforge.common.config.Config;

@Config(modid = OrangeCore.MOD_ID)
public class OrangeCoreConfig {
    @Config.Comment("Modifier for how quickly food will decay. Higher values = faster decay. Set to 0 for infinite expiration time")
    @Config.RangeDouble(min = 0, max = 10)
    @Config.LangKey("config." + OrangeCore.MOD_ID + ".general.food.decayModifier")
    public static double decayModifier = 1.0;

    @Config.Comment("The number of hours to which initial food decay will be synced. When a food item is dropped, it's initial expiration date will be rounded to the closest multiple of this (in hours).")
    @Config.RangeInt(min = 1, max = 48)
    @Config.LangKey("config." + OrangeCore.MOD_ID + ".general.food.decayStackTime")
    public static int decayStackTime = 6;

    public static boolean debug = false;
}
