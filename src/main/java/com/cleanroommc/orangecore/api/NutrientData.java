package com.cleanroommc.orangecore.api;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class NutrientData implements INBTSerializable<NBTTagCompound> { // Pretty much just a wrapper for a Map with some NBT functionality.
    private Map<Nutrient, Float> nutrientFloatMap = new HashMap<>();

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        for (Map.Entry<Nutrient, Float> nutrientValue : nutrientFloatMap.entrySet()) {
            nbt.setFloat(nutrientValue.getKey().toString(), nutrientValue.getValue());
        }
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        nutrientFloatMap.clear();
        for(String nutrientId : nbt.getKeySet()) {
            nutrientFloatMap.put(NutrientRegistry.NUTRIENT_REGISTRY.getObject(nutrientId),nbt.getFloat(nutrientId));
        }
    }

    public void addNutrient(Nutrient nutrient, float value) {
        nutrientFloatMap.put(nutrient, value);
    }
}
