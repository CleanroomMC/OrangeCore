package com.cleanroommc.orangecore.api.capability;

import com.cleanroommc.orangecore.api.Nutrient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nonnull;

public interface INutritionStats {
    @Nonnull
    EntityPlayer getPlayer();

    float getNutrientValue(Nutrient nutrient);

    void setNutrientValue(Nutrient nutrient, float value);

    float getHungerDelta();

    void updateNutrients(EntityPlayer player);

    void deserializeNBT(NBTTagCompound compound);

    NBTTagCompound serializeNBT();
}
