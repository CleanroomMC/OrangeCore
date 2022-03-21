package com.cleanroommc.orangecore.api.capability;

import com.cleanroommc.orangecore.api.Nutrient;
import com.cleanroommc.orangecore.api.NutrientData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class NutritionStatsHandler implements INutritionStats, ICapabilitySerializable<NBTTagCompound> {
    private NutrientData playerNutrientData;
    private EntityPlayer player;

    public NutritionStatsHandler(EntityPlayer player) {
        this.player = player;
        this.playerNutrientData = new NutrientData();
    }


    @Nonnull
    @Override
    public EntityPlayer getPlayer() {
        return null;
    }

    @Override
    public float getNutrientValue(Nutrient nutrient) {
        return playerNutrientData.getNutrient(nutrient);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == NutritionCapability.CAPABILITY;
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        return capability == NutritionCapability.CAPABILITY ? (T) this : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return playerNutrientData.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        playerNutrientData.deserializeNBT(nbt);
    }

    @Override
    public void updateNutrients(EntityPlayer player) {
        for(Nutrient nutrient : Nutrient.NUTRIENTS.values()) {
            if (nutrient.isEnabled())
                playerNutrientData.addNutrient(nutrient, nutrient.update(playerNutrientData.getNutrient(nutrient), player));
        }
    }
}
