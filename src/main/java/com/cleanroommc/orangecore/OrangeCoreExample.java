package com.cleanroommc.orangecore;

import com.cleanroommc.orangecore.api.Nutrient;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class OrangeCoreExample {
    public static Nutrient PROTEIN = new Nutrient(new ResourceLocation(OrangeCoreMod.MOD_ID, "protein"),
            new ResourceLocation(OrangeCoreMod.MOD_ID, "protein"),
            TextFormatting.DARK_RED,
            Nutrient.basicNutrientFormulaWithSideEffect((float) 0.2, MobEffects.WEAKNESS));
    public static void init() {

    }
}
