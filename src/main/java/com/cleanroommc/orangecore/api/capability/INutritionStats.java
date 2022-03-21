package com.cleanroommc.orangecore.api.capability;

import com.cleanroommc.orangecore.api.Nutrient;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nonnull;

public interface INutritionStats {
    @Nonnull
    EntityPlayer getPlayer();

    float getNutrientValue(Nutrient nutrient);

    void updateNutrients(EntityPlayer player);
}
