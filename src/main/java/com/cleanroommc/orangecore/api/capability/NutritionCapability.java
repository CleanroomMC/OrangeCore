package com.cleanroommc.orangecore.api.capability;

import com.cleanroommc.orangecore.capability.DumbStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.ParametersAreNonnullByDefault;

import static com.cleanroommc.orangecore.OrangeCoreMod.MOD_ID;

@ParametersAreNonnullByDefault
public class NutritionCapability {
    @CapabilityInject(INutritionStats.class)
    public static Capability<INutritionStats> CAPABILITY;
    public static final ResourceLocation KEY = new ResourceLocation(MOD_ID, "nutrition");

    public static void preInit() {
        CapabilityManager.INSTANCE.register(INutritionStats.class, new DumbStorage<>(), () -> null);
    }
}
