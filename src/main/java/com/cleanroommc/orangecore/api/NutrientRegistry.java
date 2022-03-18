package com.cleanroommc.orangecore.api;

import net.minecraft.util.registry.RegistrySimple;

public class NutrientRegistry extends RegistrySimple<String, Nutrient> {
    public static final NutrientRegistry NUTRIENT_REGISTRY = new NutrientRegistry();

    public void register(Nutrient value) {
        super.putObject(value.toString(), value);
    }
}
